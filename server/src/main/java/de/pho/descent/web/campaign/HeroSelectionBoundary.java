package de.pho.descent.web.campaign;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.dto.WsHeroSelection;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.hero.HeroSelection;

import de.pho.descent.web.auth.UserValidationException;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.player.PlayerController;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
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
@Path(ParamValue.SECURED_URL + "/campaigns/{" + ParamValue.CAMPAIGN_ID + "}/heroselections")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class HeroSelectionBoundary {

    private static final Logger LOG = Logger.getLogger(HeroSelectionBoundary.class.getName());

    @EJB
    private CampaignController campaignController;

    @EJB
    private PlayerController playerController;

    @GET
    public Response getCurrentSelections(
            @HeaderParam(ParamValue.AUTHORIZATION_HEADER_KEY) String authToken,
            @PathParam(ParamValue.CAMPAIGN_ID) String campaignId)
            throws NotFoundException, CampaignValidationException, UserValidationException {

        Player player = playerController.getPlayerByToken(authToken);
        LOG.log(Level.INFO, "Calling getCurrentSelections for Player {0}", player.getUsername());

        List<HeroSelection> selectionList = campaignController.getCurrentSelection(campaignId);
        List<WsHeroSelection> dtoList = new ArrayList<>();

        for (HeroSelection selection : selectionList) {
            dtoList.add(WsHeroSelection.createInstance(selection));
        }
        GenericEntity<List<WsHeroSelection>> list = new GenericEntity<List<WsHeroSelection>>(dtoList) {
        };

        return Response.ok().entity(list).build();
    }

    @POST
    public Response saveSelection(@PathParam(ParamValue.CAMPAIGN_ID) String campaignId,
            @Context UriInfo uriInfo,
            WsHeroSelection wsSelection)
            throws HeroSelectionException, NotFoundException, CampaignValidationException {
        Player player = playerController.getPlayerByName(wsSelection.getUsername());
        LOG.log(Level.INFO, "Calling saveSelection for Player {0}", player.getUsername());

        WsHeroSelection dtoSelection = WsHeroSelection.createInstance(campaignController.saveSelection(campaignId, player, wsSelection));

        return Response.created(uriInfo.getRequestUri()).entity(dtoSelection).build();
    }
}
