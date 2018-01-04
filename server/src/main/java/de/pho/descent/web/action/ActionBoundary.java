package de.pho.descent.web.action;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.dto.WsAction;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.web.auth.UserValidationException;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.player.PlayerController;
import de.pho.descent.web.quest.QuestValidationException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Restful Service for administrative tasks
 *
 * @author pho
 */
@Stateless
@Path(ParamValue.SECURED_URL + "/action")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class ActionBoundary {

    @Inject
    private PlayerController playerController;

    @Inject
    private ActionController actionController;

    @POST
    public Response handleAction(
            @HeaderParam(ParamValue.AUTHORIZATION_HEADER_KEY) String authToken,
            WsAction wsAction) throws UserValidationException, NotFoundException, QuestValidationException, ActionException {
        Player player = playerController.getPlayerByToken(authToken);
        Campaign campaign = actionController.handleAction(player, wsAction);

        return Response.ok().entity(WsCampaign.createInstance(campaign)).build();
    }
}
