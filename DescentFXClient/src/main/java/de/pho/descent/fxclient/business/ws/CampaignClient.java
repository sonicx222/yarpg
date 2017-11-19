package de.pho.descent.fxclient.business.ws;

import de.pho.descent.fxclient.business.auth.Credentials;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.exception.ErrorMessage;
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

    public static WsCampaign createCampaign(Credentials credentials, WsCampaign campaign) throws ServerException {
        return createCampaign(credentials.getPlayer().getUsername(), credentials.getPlayer().getPassword(), campaign);
    }

    public static WsCampaign createCampaign(Player player, WsCampaign campaign) throws ServerException {
        return createCampaign(player.getUsername(), player.getPassword(), campaign);
    }

    public static WsCampaign createCampaign(String username, String pwdHash, WsCampaign campaign) throws ServerException {
        LOG.log(Level.INFO, "User {0} created campaign", username);

        Client client = null;

        try {
            client = ClientBuilder.newClient();
            WebTarget securedTarget = client.target(getSecuredBaseUri());
            WebTarget campaignsTarget = securedTarget.path("campaigns");

            String uriPath = campaignsTarget.getUri().getPath();
            String authToken = SecurityTools.createAuthenticationToken(username, pwdHash, HttpMethod.POST, uriPath);

            Response postResponse = campaignsTarget
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .post(Entity.json(campaign), Response.class);

            WsCampaign wsCampaign = null;
            if (postResponse.getStatus() == Status.CREATED.getStatusCode()) {
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

    public static WsCampaign newCampaign(Credentials credentials) throws ServerException {
        return newCampaign(credentials.getPlayer().getUsername(), credentials.getPlayer().getPassword());
    }

    public static WsCampaign newCampaign(Player player) throws ServerException {
        return newCampaign(player.getUsername(), player.getPassword());
    }

    public static WsCampaign newCampaign(String username, String pwdHash) throws ServerException {
        LOG.log(Level.INFO, "User {0} started new campaign", username);

        Client client = null;

        try {
            client = ClientBuilder.newClient();
            WebTarget securedTarget = client.target(getSecuredBaseUri());
            WebTarget newCampaignTarget = securedTarget.path("campaigns/new");

            String uriPath = newCampaignTarget.getUri().getPath();
            String authToken = SecurityTools.createAuthenticationToken(username, pwdHash, HttpMethod.POST, uriPath);

            WsCampaign wsCampaign = null;
            Response postResponse = newCampaignTarget
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .post(Entity.json(null), Response.class);

            if (postResponse.getStatus() == Status.CREATED.getStatusCode()) {
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

    public static List<WsCampaign> getActiveCampaigns(String username, String pwdHash) throws ServerException {
        Client client = null;

        try {
            client = ClientBuilder.newClient();
            WebTarget securedTarget = client.target(getSecuredBaseUri());
            WebTarget campaignsTarget = securedTarget.path("campaigns");

            String uriPath = campaignsTarget.getUri().getPath();
            String authToken = SecurityTools.createAuthenticationToken(username, pwdHash, HttpMethod.GET, uriPath);

            List<WsCampaign> activeCampaigns = null;
            Response getResponse = campaignsTarget
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .get();

            if (getResponse.getStatus() == Status.OK.getStatusCode()) {
                activeCampaigns = getResponse.readEntity(new GenericType<List<WsCampaign>>() {
                });
            } else {
                ErrorMessage errorMsg = getResponse.readEntity(ErrorMessage.class);
                throw new ServerException(errorMsg.getErrorMessage());
            }
            return activeCampaigns;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
    
    public static WsCampaign startCampaign(String username, String pwdHash, WsCampaign wsCampaign) throws ServerException {
        Client client = null;

        try {
            client = ClientBuilder.newClient();
            WebTarget securedTarget = client.target(getSecuredBaseUri());
            WebTarget campaignsTarget = securedTarget.path("campaigns");
            WebTarget startCampaignTarget = campaignsTarget.path("{" + ParamValue.CAMPAIGN_ID + "}").path("start");

            String uriPath = startCampaignTarget.resolveTemplate(ParamValue.CAMPAIGN_ID, wsCampaign.getId()).getUri().getPath();
            String authToken = SecurityTools.createAuthenticationToken(username, pwdHash, HttpMethod.GET, uriPath);

            WsCampaign campaign = null;
            Response putResponse = startCampaignTarget
                    .resolveTemplate(ParamValue.CAMPAIGN_ID, wsCampaign.getId())
                    .request(MediaType.APPLICATION_JSON)
                    .header(ParamValue.AUTHORIZATION_HEADER_KEY, authToken)
                    .get();

            if (putResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                campaign = putResponse.readEntity(WsCampaign.class);
            } else {
                ErrorMessage errorMsg = putResponse.readEntity(ErrorMessage.class);
                throw new ServerException(errorMsg.getErrorMessage());
            }
            return campaign;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
