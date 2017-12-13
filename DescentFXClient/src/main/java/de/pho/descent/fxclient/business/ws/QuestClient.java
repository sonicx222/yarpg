package de.pho.descent.fxclient.business.ws;

import static de.pho.descent.fxclient.business.ws.BaseRESTClient.getSecuredBaseUri;
import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.dto.WsQuestEncounter;
import de.pho.descent.shared.exception.ErrorMessage;
import java.util.logging.Logger;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author pho
 */
public class QuestClient extends BaseRESTClient {

    private static final Logger LOG = Logger.getLogger(QuestClient.class.getName());

    public static WsQuestEncounter getQuestEncounter(String username, String pwdHash, long questId) throws ServerException {

        Client client = null;

        try {
            client = ClientBuilder.newClient();
            WebTarget securedTarget = client.target(getSecuredBaseUri());
            WebTarget questsTarget = securedTarget.path("quests").path("{" + ParamValue.QUEST_ID + "}");

            String uriPath = questsTarget.resolveTemplate(ParamValue.QUEST_ID, questId).getUri().getPath();
            String authToken = SecurityTools.createAuthenticationToken(username,
                    pwdHash,
                    HttpMethod.GET, uriPath);

            Response getResponse = questsTarget
                    .resolveTemplate(ParamValue.QUEST_ID, questId)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .get();

            WsQuestEncounter wsQuestEncounter = null;
            if (getResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                wsQuestEncounter = getResponse.readEntity(WsQuestEncounter.class);
            } else {
                ErrorMessage errorMsg = getResponse.readEntity(ErrorMessage.class);
                throw new ServerException(errorMsg.getErrorMessage());
            }

            return wsQuestEncounter;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
