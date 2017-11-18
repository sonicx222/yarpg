package de.pho.descent.fxclient.business.ws;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.exception.ErrorMessage;
import de.pho.descent.shared.model.Player;
import java.util.logging.Logger;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
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
                ErrorMessage errorMsg = postResponse.readEntity(ErrorMessage.class);
                throw new ServerException(errorMsg.getErrorMessage());
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
            String authToken = SecurityTools.createAuthenticationToken(
                    username,
                    SecurityTools.createHash(password, false),
                    HttpMethod.GET,
                    uriPath);

            Player player = null;
            Response getResponse = loginPlayerTarget
                    .resolveTemplate(ParamValue.USERNAME, username)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .get();

            if (getResponse.getStatus() == Status.ACCEPTED.getStatusCode()) {
                player = getResponse.readEntity(Player.class);
            } else {
                ErrorMessage errorMsg = getResponse.readEntity(ErrorMessage.class);
                throw new ServerException(errorMsg.getErrorMessage());
            }

            return player;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    public static Player updatePlayer(String username, String pwdHash, Player actualPlayer) throws ServerException {
        Client client = null;

        try {
            client = ClientBuilder.newClient();

            WebTarget baseTarget = client.target(getBaseUri());
            WebTarget securedPlayersTarget = baseTarget.path("players").path(ParamValue.SECURED_URL);
            WebTarget updatePlayerTarget = securedPlayersTarget.path("{" + ParamValue.USERNAME + "}");

            String uriPath = updatePlayerTarget.resolveTemplate(ParamValue.USERNAME, actualPlayer.getUsername()).getUri().getPath();
            String authToken = SecurityTools.createAuthenticationToken(username,
                    pwdHash,
                    HttpMethod.PUT,
                    uriPath);

            Player updatedPlayer = null;
            Response putResponse = updatePlayerTarget
                    .resolveTemplate(ParamValue.USERNAME, actualPlayer.getUsername())
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .put(Entity.json(actualPlayer), Response.class);
            if (putResponse.getStatus() == Status.OK.getStatusCode()) {
                updatedPlayer = putResponse.readEntity(Player.class);
            } else {
                ErrorMessage errorMsg = putResponse.readEntity(ErrorMessage.class);
                throw new ServerException(errorMsg.getErrorMessage());
            }

            return updatedPlayer;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

}
