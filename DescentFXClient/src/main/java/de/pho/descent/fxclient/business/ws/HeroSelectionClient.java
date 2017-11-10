package de.pho.descent.fxclient.business.ws;

import de.pho.descent.fxclient.business.auth.Credentials;
import static de.pho.descent.fxclient.business.ws.BaseRESTClient.getSecuredBaseUri;
import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsHeroSelection;
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
public class HeroSelectionClient extends BaseRESTClient {

    private static final Logger LOG = Logger.getLogger(HeroSelectionClient.class.getName());

    public static WsHeroSelection saveSelection(Credentials credentials, WsHeroSelection wsHeroSelection, WsCampaign wsCampaign) throws ServerException {

        Client client = null;

        try {
            client = ClientBuilder.newClient();
            WebTarget securedTarget = client.target(getSecuredBaseUri());
            WebTarget campaignsTarget = securedTarget.path("campaigns");
            WebTarget heroSelectionTarget = campaignsTarget.path("{" + ParamValue.CAMPAIGN_ID + "}").path("heroselections");

            String uriPath = heroSelectionTarget.resolveTemplate(ParamValue.CAMPAIGN_ID, wsCampaign.getId()).getUri().getPath();
            String authToken = SecurityTools.createAuthenticationToken(
                    credentials.getPlayer().getUsername(),
                    credentials.getPlayer().getPassword(),
                    HttpMethod.POST, uriPath);

            Response postResponse = heroSelectionTarget
                    .resolveTemplate(ParamValue.CAMPAIGN_ID, wsCampaign.getId())
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .post(Entity.json(wsHeroSelection), Response.class);

            WsHeroSelection savedSelection = null;
            if (postResponse.getStatus() == Response.Status.CREATED.getStatusCode()) {
                savedSelection = postResponse.readEntity(WsHeroSelection.class);
            } else {
                throw new ServerException(postResponse);
            }

            return savedSelection;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    public static List<WsHeroSelection> getCurrentSelections(String username, String password, WsCampaign wsCampaign) throws ServerException {
        Client client = null;

        try {
            client = ClientBuilder.newClient();
            WebTarget securedTarget = client.target(getSecuredBaseUri());
            WebTarget campaignsTarget = securedTarget.path("campaigns");
            WebTarget heroSelectionTarget = campaignsTarget.path("{" + ParamValue.CAMPAIGN_ID + "}").path("heroselections");

            String uriPath = heroSelectionTarget.resolveTemplate(ParamValue.CAMPAIGN_ID, wsCampaign.getId()).getUri().getPath();
            String authToken = SecurityTools.createAuthenticationToken(
                    username, password, HttpMethod.GET, uriPath);

            List<WsHeroSelection> currentSelections = null;
            Response getResponse = heroSelectionTarget
                    .resolveTemplate(ParamValue.CAMPAIGN_ID, wsCampaign.getId())
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .get();

            if (getResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                currentSelections = getResponse.readEntity(new GenericType<List<WsHeroSelection>>() {
                });
            } else {
                throw new ServerException(getResponse);
            }
            return currentSelections;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
