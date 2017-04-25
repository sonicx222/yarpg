package de.pho.descent.fxclient.business.ws;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.model.Player;
import java.util.logging.Logger;
import javax.ws.rs.ClientErrorException;
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

    public static Player registerPlayer(String username, String password) throws ClientErrorException {
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
                showError(postResponse);
            }

            return player;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    public static Player loginPlayer(String username, String password) throws ClientErrorException {
        Client client = null;

        try {
            client = ClientBuilder.newClient();

            WebTarget baseTarget = client.target(getBaseUri());
            WebTarget playersTarget = baseTarget.path("players");
            WebTarget loginPlayerTarget = playersTarget.path("{" + ParamValue.USERNAME + "}");

            String uriPath = loginPlayerTarget.resolveTemplate(ParamValue.USERNAME, username).getUri().getPath();
            String authToken = SecurityTools.createAuthHeaderValue(username, password, HttpMethod.GET, uriPath);
            LOG.info("Login Authorization : " + authToken);
            
            Player player = null;
            Response getResponse = loginPlayerTarget
                    .resolveTemplate(ParamValue.USERNAME, username)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .get();

            if (getResponse.getStatus() == Status.ACCEPTED.getStatusCode()) {
                player = getResponse.readEntity(Player.class);
            } else {
                showError(getResponse);
            }

            return player;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

}
