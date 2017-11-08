package de.pho.descent.web.campaign;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsHeroSelection;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.hero.HeroSelection;
import de.pho.descent.web.auth.UserValidationException;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.player.PlayerController;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
@Path("secured/campaigns")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class CampaignBoundary {

    @EJB
    private CampaignController campaignController;

    @EJB
    private PlayerController playerController;

    @GET
    public Response getActiveCampaigns(@HeaderParam(ParamValue.AUTHORIZATION_HEADER_KEY) String authToken) throws UserValidationException {
        Player player = playerController.getPlayerByToken(authToken);

        List<WsCampaign> campaigns = campaignController.getPlayableCampaigns(player);
        GenericEntity<List<WsCampaign>> list = new GenericEntity<List<WsCampaign>>(campaigns) {
        };

        return Response.ok().entity(list).build();
    }

    @POST
    public Response createNewCampaign(@HeaderParam(ParamValue.AUTHORIZATION_HEADER_KEY) String authToken, @Context UriInfo uriInfo) throws URISyntaxException, UserValidationException {
        Player player = playerController.getPlayerByToken(authToken);

        WsCampaign wscampaign = WsCampaign.createInstance(campaignController.createNewCampaign(player));
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(wscampaign.getId())).build();

        return Response.created(uri).entity(wscampaign).build();
    }

    @PUT
    public Response startCampaign(@HeaderParam(ParamValue.AUTHORIZATION_HEADER_KEY) String authToken, String campaignId) throws UserValidationException, HeroSelectionException, NotFoundException {
        Player overlordPlayer = playerController.getPlayerByToken(authToken);
        campaignController.startCampaign(overlordPlayer, campaignId);

        return Response.ok().build();
    }

    @GET
    @Path("/{" + ParamValue.CAMPAIGN_ID + "}/heroselection")
    public List<WsHeroSelection> getCurrentSelections(@PathParam(ParamValue.CAMPAIGN_ID) String campaignId) throws NotFoundException, CampaignValidationException {
        List<HeroSelection> selectionList = campaignController.getCurrentSelection(campaignId);
        List<WsHeroSelection> dtoList = new ArrayList<>();

        for (HeroSelection selection : selectionList) {
            dtoList.add(WsHeroSelection.createInstance(selection));
        }

        return dtoList;
    }

    @POST
    @Path("/{" + ParamValue.CAMPAIGN_ID + "}/heroselection")
    public Response saveSelection(@PathParam(ParamValue.CAMPAIGN_ID) String campaignId,
            @Context UriInfo uriInfo,
            WsHeroSelection wsSelection)
            throws HeroSelectionException, NotFoundException, CampaignValidationException {
        Player player = playerController.getPlayerByName(wsSelection.getUsername());
        WsHeroSelection dtoSelection = WsHeroSelection.createInstance(campaignController.saveSelection(campaignId, player, wsSelection));

        return Response.created(uriInfo.getRequestUri()).entity(dtoSelection).build();
    }
}
