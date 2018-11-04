package de.pho.descent.web.quest;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.dto.WsQuestEncounter;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.message.Message;
import de.pho.descent.shared.model.message.MessageType;
import de.pho.descent.shared.model.monster.MonsterGroup;
import de.pho.descent.shared.model.quest.LootBox;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.web.auth.UserValidationException;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.message.MessageController;
import de.pho.descent.web.player.PlayerController;
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
    private PlayerController playerController;

    @Inject
    private QuestController questController;

    @Inject
    private MessageController messageController;

    @GET
    @Path("/{" + ParamValue.QUEST_ID + "}")
    public Response getQuest(
            @PathParam(ParamValue.QUEST_ID) String questId) {
        LOG.log(Level.INFO, "Calling getQuest for id {0}", questId);
        QuestEncounter questEncounter = questController.getQuestEncounterById(Long.parseLong(questId));

        // prevent lazy load exception
        questEncounter.getHeroes().forEach(hero -> {
            hero.getCurrentLocation().size();
            hero.getInventory().size();
            hero.getSkills().size();
        });
        questEncounter.getMonsters().forEach(monster -> monster.getCurrentLocation().size());

        return Response.ok().entity(WsQuestEncounter.createInstance(questEncounter)).build();
    }

    @GET
    @Path("/{" + ParamValue.QUEST_ID + "}/reward")
    public Response getQuestReward(
            @PathParam(ParamValue.QUEST_ID) String questId) {
        LOG.log(Level.INFO, "Calling getQuestReward for id {0}", questId);
        QuestEncounter questEncounter = questController.getQuestEncounterById(Long.parseLong(questId));
        LootBox box = questController.getQuestReward(questEncounter);

        return Response.ok().entity(box).build();
    }

    @POST
    @Path("/{" + ParamValue.QUEST_ID + "}/endTurn")
    public Response endActiveUnitTurn(
            @HeaderParam(ParamValue.AUTHORIZATION_HEADER_KEY) String authToken,
            @PathParam(ParamValue.QUEST_ID) String questId) throws NotFoundException, UserValidationException, QuestValidationException {
        Player player = playerController.getPlayerByToken(authToken);
        LOG.log(Level.INFO, "Player {0} ending current turn for active quest id {1}", new Object[]{player.getUsername(), questId});
        QuestEncounter questEncounter = questController.getQuestEncounterById(Long.parseLong(questId));

        // end turn
        questController.endActiveUnitTurn(questEncounter);
        messageController.saveMessage(
                new Message(questEncounter.getCampaign(), MessageType.GAME,
                        player, "Player " + player.getUsername() + " ending current turn"));

        // prevent lazy load exception
        questEncounter.getHeroes().forEach(hero -> {
            hero.getCurrentLocation().size();
            hero.getInventory().size();
            hero.getSkills().size();
        });
        questEncounter.getMonsters().forEach(monster -> monster.getCurrentLocation().size());

        return Response.ok().entity(WsQuestEncounter.createInstance(questEncounter)).build();
    }

    @POST
    @Path("/{" + ParamValue.QUEST_ID + "}/activateMonsterGroup")
    public Response activateMonsterGroup(
            @HeaderParam(ParamValue.AUTHORIZATION_HEADER_KEY) String authToken,
            @PathParam(ParamValue.QUEST_ID) String questId, String monsterGroup)
            throws QuestValidationException, UserValidationException, NotFoundException {
        Player player = playerController.getPlayerByToken(authToken);

        LOG.log(Level.INFO, "Player {0} calling activateMonsterGroup for quest id {1} and group {2}",
                new Object[]{player.getUsername(), questId, monsterGroup});
        QuestEncounter questEncounter = questController.getQuestEncounterById(Long.parseLong(questId));
        questEncounter = questController.activateMonsterGroup(player, questEncounter, MonsterGroup.valueOf(monsterGroup));

        messageController.saveMessage(
                new Message(questEncounter.getCampaign(), MessageType.GAME,
                        player, "Player " + player.getUsername() + " activated monster group: " + monsterGroup));

        // prevent lazy load exception
        questEncounter.getHeroes().forEach(hero -> {
            hero.getCurrentLocation().size();
            hero.getInventory().size();
            hero.getSkills().size();
        });
        questEncounter.getMonsters().forEach(monster -> monster.getCurrentLocation().size());

        return Response.ok().entity(WsQuestEncounter.createInstance(questEncounter)).build();
    }

}
