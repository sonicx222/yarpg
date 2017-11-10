package de.pho.descent.fxclient.business.ws;

import de.pho.descent.fxclient.business.auth.Credentials;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.model.Player;
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
public class CampaignClient extends BaseRESTClient {

    private static final Logger LOG = Logger.getLogger(CampaignClient.class.getName());

    public static WsCampaign createCampaign(Credentials credentials) throws ServerException {
        return createCampaign(credentials.getPlayer().getUsername(), credentials.getPlayer().getPassword());
    }

    public static WsCampaign createCampaign(Player player) throws ServerException {
        return createCampaign(player.getUsername(), player.getPassword());
    }

    private static WsCampaign createCampaign(String username, String password) throws ServerException {
        LOG.log(Level.INFO, "Player {0} started new campaign", username);

        Client client = null;

        try {
            client = ClientBuilder.newClient();
            WebTarget securedTarget = client.target(getSecuredBaseUri());
            WebTarget campaignsTarget = securedTarget.path("campaigns");

            String uriPath = campaignsTarget.getUri().getPath();
            String authToken = SecurityTools.createAuthenticationToken(
                    username, password, HttpMethod.POST, uriPath);
            LOG.log(Level.INFO, "Login Authorization: {0}", authToken);

            WsCampaign wsCampaign = null;

            Response postResponse = campaignsTarget
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .post(Entity.json(wsCampaign), Response.class);

            if (postResponse.getStatus() == Status.CREATED.getStatusCode()) {
                wsCampaign = postResponse.readEntity(WsCampaign.class);
            } else {
                throw new ServerException(postResponse);
            }

            return wsCampaign;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    public static List<WsCampaign> getActiveCampaigns(String username, String password) throws ServerException {
        Client client = null;

        try {
            client = ClientBuilder.newClient();
            WebTarget securedTarget = client.target(getSecuredBaseUri());
            WebTarget campaignsTarget = securedTarget.path("campaigns");

            String uriPath = campaignsTarget.getUri().getPath();
            String authToken = SecurityTools.createAuthenticationToken(
                    username, password, HttpMethod.GET, uriPath);

            
//            List<WsCampaign> activeCampaigns = campaignsTarget
//                    .request(MediaType.APPLICATION_JSON)
//                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
//                    .get(new GenericType<List<WsCampaign>>() {
//                    });
            List<WsCampaign> activeCampaigns = null;
            Response getResponse = campaignsTarget
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .get();

            if (getResponse.getStatus() == Status.OK.getStatusCode()) {
                activeCampaigns = getResponse.readEntity(new GenericType<List<WsCampaign>>() {
                });
            } else {
                throw new ServerException(getResponse);
            }
            return activeCampaigns;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
