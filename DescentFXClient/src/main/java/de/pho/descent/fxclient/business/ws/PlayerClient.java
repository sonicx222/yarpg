package de.pho.descent.fxclient.business.ws;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.model.Player;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author pho
 */
public class PlayerClient extends BaseRESTClient {

    private static final Logger LOG = Logger.getLogger(PlayerClient.class.getName());

    public static Player registerPlayer(String username, String password) throws ServerException {
        Client client = null;

        try {
            client = ClientBuilder.newClient();
            WebTarget target = client.target(getBaseUri());

            Player player = null;
            Response postResponse = target.path("players").request()
                    .header(ParamValue.USERNAME, username)
                    .header(ParamValue.ENCRYPTED_PWD, SecurityTools.createHash(password, false))
                    .post(null, Response.class);
            if (postResponse.getStatus() == Status.CREATED.getStatusCode()) {
                player = postResponse.readEntity(Player.class);
            } else {
                throw new ServerException(postResponse);
            }

            return player;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    public static Player loginPlayer(String username, String password) throws ServerException {
        Client client = null;

        try {
            client = ClientBuilder.newClient();

            WebTarget baseTarget = client.target(getBaseUri());
            WebTarget securedPlayersTarget = baseTarget.path("players").path(ParamValue.SECURED_URL);
            WebTarget loginPlayerTarget = securedPlayersTarget.path("{" + ParamValue.USERNAME + "}");

            String uriPath = loginPlayerTarget.resolveTemplate(ParamValue.USERNAME, username).getUri().getPath();
            String authToken = SecurityTools.createAuthenticationToken(username,
                    SecurityTools.createHash(password, false),
                    HttpMethod.GET,
                    uriPath);
            LOG.log(Level.INFO, "Login Authorization : {0}", authToken);
            
            Player player = null;
            Response getResponse = loginPlayerTarget
                    .resolveTemplate(ParamValue.USERNAME, username)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .get();

            if (getResponse.getStatus() == Status.ACCEPTED.getStatusCode()) {
                player = getResponse.readEntity(Player.class);
            } else {
                throw new ServerException(getResponse);
            }

            return player;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

}
