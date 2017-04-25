package de.pho.descent.fxclient.business.ws;

import de.pho.descent.fxclient.business.auth.Credentials;
import static de.pho.descent.fxclient.business.ws.BaseRESTClient.showError;
import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.model.campaign.Campaign;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
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
public class CampaignClient extends BaseRESTClient {

    private static final Logger LOG = Logger.getLogger(CampaignClient.class.getName());

    @Inject
    private Credentials credentials;
    
    public static Campaign createCampaign(String username, String password) throws ClientErrorException {
        Client client = null;

        try {
            client = ClientBuilder.newClient();
            WebTarget securedTarget = client.target(getSecuredBaseUri());
            WebTarget campaignsTarget = securedTarget.path("campaigns");

            String uriPath = campaignsTarget.getUri().getPath();
            String authToken = SecurityTools.createAuthHeaderValue(username, password, HttpMethod.POST, uriPath);
            LOG.info("Login Authorization: " + authToken);

            Campaign campaign = null;
            Response postResponse = campaignsTarget
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .post(null, Response.class);
            if (postResponse.getStatus() == Response.Status.CREATED.getStatusCode()) {
                campaign = postResponse.readEntity(Campaign.class);
            } else {
                showError(postResponse);
            }

            return campaign;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
