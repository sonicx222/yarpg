package de.pho.descent.web.campaign;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.web.auth.UserValidationException;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.player.PlayerController;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author pho
 */
@Stateless
@Path(ParamValue.SECURED_URL + "/campaigns")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class CampaignBoundary {

    private static final Logger LOG = Logger.getLogger(CampaignBoundary.class.getName());

    @Inject
    private CampaignController campaignController;

    @Inject
    private PlayerController playerController;

    @GET
    public Response getActiveCampaigns(
            @HeaderParam(ParamValue.AUTHORIZATION_HEADER_KEY) String authToken)
            throws UserValidationException, NotFoundException {

        Player player = playerController.getPlayerByToken(authToken);
        LOG.log(Level.INFO, "Calling getActiveCampaigns for Player {0}", player.getUsername());

        List<WsCampaign> wsCampaigns = new ArrayList<>();
        campaignController.getPlayableCampaigns(player).forEach(
                campaign -> {
                    // prevent lazy load exception
                    campaign.getHeroes().forEach(hero -> hero.getCurrentLocation().size());
                    // create DTO
                    wsCampaigns.add(WsCampaign.createInstance(campaign));
                });

        // wrap WsCampaigns in special List for REST response
        GenericEntity<List<WsCampaign>> list = new GenericEntity<List<WsCampaign>>(wsCampaigns) {
        };

        return Response.ok().entity(list).build();
    }

    @GET
    @Path("/{" + ParamValue.CAMPAIGN_ID + "}")
    public Response getCampaignById(
            @PathParam(ParamValue.CAMPAIGN_ID) String campaignId)
            throws NotFoundException {
        LOG.log(Level.INFO, "Calling getCampaignById for Id {0}", campaignId);

        Campaign campaign = campaignController.getCampaignById(Long.valueOf(campaignId));

        // prevent lazy load exception
        campaign.getHeroes().forEach(hero -> hero.getCurrentLocation().size());

        return Response.ok().entity(WsCampaign.createInstance(campaign)).build();
    }

    @POST
    public Response createCampaign(
            @HeaderParam(ParamValue.AUTHORIZATION_HEADER_KEY) String authToken,
            @Context UriInfo uriInfo,
            WsCampaign wsCampaign)
            throws URISyntaxException, UserValidationException, IOException, NotFoundException {
        Player player = playerController.getPlayerByToken(authToken);
        LOG.log(Level.INFO, "Calling createNewCampaign with Player {0}", player.getUsername());

        Campaign campaign = campaignController.createCampaign(wsCampaign);
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(campaign.getId())).build();

        return Response.created(uri).entity(WsCampaign.createInstance(campaign)).build();
    }

    @POST
    @Path("/new")
    public Response newCampaign(
            @HeaderParam(ParamValue.AUTHORIZATION_HEADER_KEY) String authToken,
            @Context UriInfo uriInfo)
            throws URISyntaxException, UserValidationException, NotFoundException {
        Player player = playerController.getPlayerByToken(authToken);
        LOG.log(Level.INFO, "Calling createNewCampaign with Player {0}", player.getUsername());

        Campaign campaign = campaignController.newCampaign(player);
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(campaign.getId())).build();

        return Response.created(uri).entity(WsCampaign.createInstance(campaign)).build();
    }

    @GET
    @Path("/{" + ParamValue.CAMPAIGN_ID + "}/start")
    public Response startCampaign(
            @HeaderParam(ParamValue.AUTHORIZATION_HEADER_KEY) String authToken,
            @PathParam(ParamValue.CAMPAIGN_ID) String campaignId)
            throws UserValidationException, HeroSelectionException, NotFoundException, IOException {
        Player overlordPlayer = playerController.getPlayerByToken(authToken);
        LOG.log(Level.INFO, "Calling startCampaign with Player {0}", overlordPlayer.getUsername());

        Campaign campaign = campaignController.startCampaign(overlordPlayer, campaignId);

        return Response.ok().entity(WsCampaign.createInstance(campaign)).build();
    }

}
