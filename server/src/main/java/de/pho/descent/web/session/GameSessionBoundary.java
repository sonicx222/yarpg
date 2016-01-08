package de.pho.descent.web.session;

import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.model.Campaign;
import de.pho.descent.web.model.Encounter;
import java.net.URI;
import java.net.URISyntaxException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author pho
 */
@Stateless
@Path("/gamesession")
@Produces({MediaType.APPLICATION_JSON})
@Consumes(MediaType.APPLICATION_JSON)
public class GameSessionBoundary {

    @Context
    private UriInfo ui;

    @EJB
    private GameSessionController sessionController;

    @POST
    public Response createGameSession() throws URISyntaxException {
        Campaign c = sessionController.createNewCampaign();

        URI msgURI = ui.getRequestUriBuilder().path(Long.toString(c.getId())).build();

        return Response.created(msgURI).build();
    }
    
    @Path("{encounterID}")
    @GET
    public Encounter getMessage(@PathParam("encounterID") int encounterID) throws NotFoundException {
        Encounter encounter = sessionController.getEncounter(encounterID);

        if (encounter == null) {
            throw new NotFoundException();
        }

        return encounter;
    }

}
