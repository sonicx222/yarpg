package de.pho.descent.fxclient.business.ws;

import de.pho.descent.fxclient.business.auth.Credentials;
import static de.pho.descent.fxclient.business.ws.BaseRESTClient.showError;
import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.model.GameMaster;
import de.pho.descent.shared.model.campaign.Campaign;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
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
public class CampaignClient extends BaseRESTClient {

    private static final Logger LOG = Logger.getLogger(CampaignClient.class.getName());
    
    public static Campaign createCampaign(Credentials credentials) throws ClientErrorException {
        
        LOG.info(credentials.getPlayer().getUsername());
        
        Client client = null;

        try {
            client = ClientBuilder.newClient();
            WebTarget securedTarget = client.target(getSecuredBaseUri());
            WebTarget campaignsTarget = securedTarget.path("campaigns");

            String uriPath = campaignsTarget.getUri().getPath();
            String authToken = SecurityTools.createAuthenticationToken(
                    credentials.getPlayer().getUsername(),
                    credentials.getPlayer().getPassword(),
                    HttpMethod.POST, uriPath);
            LOG.info("Login Authorization: " + authToken);

            Campaign campaign = new Campaign();
            campaign.setOverlord(credentials.getPlayer());
            
            Response postResponse = campaignsTarget
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .post(Entity.json(campaign), Response.class);
            
            if (postResponse.getStatus() == Status.CREATED.getStatusCode()) {
                campaign = postResponse.readEntity(Campaign.class);
            } else {
                campaign = null;
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
