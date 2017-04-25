package de.pho.descent.web.session;

import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.web.campaign.CampaignController;
import de.pho.descent.web.exception.NotFoundException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author pho
 */
@Stateless
@Path("/gamesession")
public class GameSessionBoundary {

    @Context
    private UriInfo ui;

    @EJB
    private CampaignController sessionController;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getMessage() {
        return "Hello World";
    }

    @GET
    @Path("{encounterID}")
    @Produces({MediaType.APPLICATION_JSON})
    public QuestEncounter getEncounter(@PathParam("encounterID") int encounterID) throws NotFoundException {
        QuestEncounter encounter = sessionController.getEncounter(encounterID);

        if (encounter == null) {
            throw new NotFoundException();
        }

        return encounter;
    }

}
