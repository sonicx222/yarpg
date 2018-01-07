package de.pho.descent.fxclient.business.ws;

import de.pho.descent.fxclient.business.auth.Credentials;
import static de.pho.descent.fxclient.business.ws.BaseRESTClient.getSecuredBaseUri;
import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.dto.WsAction;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsHeroSelection;
import de.pho.descent.shared.exception.ErrorMessage;
import de.pho.descent.shared.model.Player;
import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author pho
 */
public class ActionClient extends BaseRESTClient {

    private static final Logger LOG = Logger.getLogger(ActionClient.class.getName());

    public static WsCampaign sendAction(Credentials credentials, WsAction wsAction) throws ServerException {
        return sendAction(credentials.getPlayer(), wsAction);
    }

    public static WsCampaign sendAction(Player player, WsAction wsAction) throws ServerException {
        return sendAction(player.getUsername(), player.getPassword(), wsAction);
    }

    public static WsCampaign sendAction(String username, String pwdHash, WsAction wsAction) throws ServerException {

        Client client = null;

        try {
            client = ClientBuilder.newClient();
            WebTarget securedTarget = client.target(getSecuredBaseUri());
            WebTarget actionTarget = securedTarget.path("action");

            String uriPath = actionTarget.getUri().getPath();
            String authToken = SecurityTools.createAuthenticationToken(username,
                    pwdHash,
                    HttpMethod.POST, uriPath);

            Response postResponse = actionTarget
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .post(Entity.json(wsAction), Response.class);

            WsCampaign wsCampaign = null;
            if (postResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                wsCampaign = postResponse.readEntity(WsCampaign.class);
            } else {
                ErrorMessage errorMsg = postResponse.readEntity(ErrorMessage.class);
                throw new ServerException(errorMsg.getErrorMessage());
            }

            return wsCampaign;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
