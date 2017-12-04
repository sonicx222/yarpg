package de.pho.descent.fxclient.business.ws;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsMessage;
import de.pho.descent.shared.exception.ErrorMessage;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.Campaign;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author pho
 */
public class MessageClient extends BaseRESTClient {

    private static final Logger LOG = Logger.getLogger(MessageClient.class.getName());

    public static WsMessage postMessage(Player player, WsCampaign campaign, String text) throws ServerException {
        LOG.log(Level.INFO, "User {0} posted message", player.getUsername());

        Client client = null;

        try {
            client = ClientBuilder.newClient();
            WebTarget securedTarget = client.target(getSecuredBaseUri());
            WebTarget messagesTarget = securedTarget.path("messages");

            String uriPath = messagesTarget.getUri().getPath();
            String authToken = SecurityTools.createAuthenticationToken(
                    player.getUsername(),
                    player.getPassword(),
                    HttpMethod.POST, uriPath);

            long campaignId = campaign == null ? 0 : campaign.getId();
            WsMessage wsMessage = new WsMessage(player.getUsername(), campaignId, text);
            Response postResponse = messagesTarget
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .post(Entity.json(wsMessage), Response.class);

            if (postResponse.getStatus() == Status.CREATED.getStatusCode()) {
                wsMessage = postResponse.readEntity(WsMessage.class);
            } else {
                ErrorMessage errorMsg = postResponse.readEntity(ErrorMessage.class);
                throw new ServerException(errorMsg.getErrorMessage());
            }

            return wsMessage;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    public static List<WsMessage> getGeneralMessages(String username, String pwdHash) throws ServerException {
        Client client = null;

        try {
            client = ClientBuilder.newClient();
            WebTarget securedTarget = client.target(getSecuredBaseUri());
            WebTarget messagesTarget = securedTarget.path("messages");

            String uriPath = messagesTarget.getUri().getPath();
            String authToken = SecurityTools.createAuthenticationToken(username, pwdHash, HttpMethod.GET, uriPath);

            List<WsMessage> generalMessages = null;
            Response getResponse = messagesTarget
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .get();

            if (getResponse.getStatus() == Status.OK.getStatusCode()) {
                generalMessages = getResponse.readEntity(new GenericType<List<WsMessage>>() {
                });
            } else {
                ErrorMessage errorMsg = getResponse.readEntity(ErrorMessage.class);
                throw new ServerException(errorMsg.getErrorMessage());
            }
            return generalMessages;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    public static List<WsMessage> getCampaignMessages(String username, String pwdHash, WsCampaign wsCampaign) throws ServerException {
        Client client = null;

        try {
            client = ClientBuilder.newClient();
            WebTarget securedTarget = client.target(getSecuredBaseUri());
            WebTarget messagesTarget = securedTarget.path("messages");
            WebTarget messagesByCampaignTarget = messagesTarget.path("campaign/{" + ParamValue.CAMPAIGN_ID + "}");

            String uriPath = messagesByCampaignTarget.resolveTemplate(ParamValue.CAMPAIGN_ID, wsCampaign.getId()).getUri().getPath();
            String authToken = SecurityTools.createAuthenticationToken(username, pwdHash, HttpMethod.GET, uriPath);

            List<WsMessage> generalMessages = null;
            Response getResponse = messagesByCampaignTarget
                    .resolveTemplate(ParamValue.CAMPAIGN_ID, wsCampaign.getId())
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .get();

            if (getResponse.getStatus() == Status.OK.getStatusCode()) {
                generalMessages = getResponse.readEntity(new GenericType<List<WsMessage>>() {
                });
            } else {
                ErrorMessage errorMsg = getResponse.readEntity(ErrorMessage.class);
                throw new ServerException(errorMsg.getErrorMessage());
            }
            return generalMessages;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
