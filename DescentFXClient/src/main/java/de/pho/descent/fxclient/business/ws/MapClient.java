package de.pho.descent.fxclient.business.ws;

import static de.pho.descent.fxclient.business.ws.BaseRESTClient.getSecuredBaseUri;
import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.dto.WsGameMap;
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
public class MapClient extends BaseRESTClient {

    private static final Logger LOG = Logger.getLogger(MapClient.class.getName());

    public static WsGameMap getQuestMap(String username, String pwdHash, long mapId) throws ServerException {

        Client client = null;

        try {
            client = ClientBuilder.newClient();
            WebTarget securedTarget = client.target(getSecuredBaseUri());
            WebTarget mapsTarget = securedTarget.path("maps").path("{" + ParamValue.MAP_ID + "}");

            String uriPath = mapsTarget.resolveTemplate(ParamValue.MAP_ID, mapId).getUri().getPath();
            String authToken = SecurityTools.createAuthenticationToken(username,
                    pwdHash,
                    HttpMethod.GET, uriPath);

            Response getResponse = mapsTarget
                    .resolveTemplate(ParamValue.MAP_ID, mapId)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .get();

            WsGameMap wsGameMap = null;
            if (getResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                wsGameMap = getResponse.readEntity(WsGameMap.class);
            } else {
                ErrorMessage errorMsg = getResponse.readEntity(ErrorMessage.class);
                throw new ServerException(errorMsg.getErrorMessage());
            }

            return wsGameMap;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
