package de.pho.descent.web.quest;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.dto.WsQuestEncounter;
import de.pho.descent.shared.model.quest.QuestEncounter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author pho
 */
@Stateless
@Path(ParamValue.SECURED_URL + "/quests")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class QuestBoundary {

    private static final Logger LOG = Logger.getLogger(QuestBoundary.class.getName());

    @Inject
    private QuestController questController;

    @GET
    @Path("/{" + ParamValue.QUEST_ID + "}")
    public Response getQuest(
            @HeaderParam(ParamValue.AUTHORIZATION_HEADER_KEY) String authToken,
            @PathParam(ParamValue.QUEST_ID) String questId) {
        LOG.log(Level.INFO, "Calling getQuest for id {0}", questId);
        QuestEncounter questEncounter = questController.getQuestEncounterById(Long.parseLong(questId));
        
        // prevent lazy load exception
        questEncounter.getMonsters().forEach(monster -> monster.getCurrentLocation().size());

        return Response.ok().entity(WsQuestEncounter.createInstance(questEncounter)).build();
    }
}
