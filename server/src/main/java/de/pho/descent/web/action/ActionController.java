package de.pho.descent.web.action;

import de.pho.descent.web.action.handler.MoveHandler;
import de.pho.descent.shared.dto.WsAction;
import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.message.Message;
import de.pho.descent.web.action.handler.AttackHandler;
import de.pho.descent.web.campaign.CampaignController;
import de.pho.descent.web.exception.Messages;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.message.MessageController;
import de.pho.descent.web.quest.QuestController;
import de.pho.descent.web.quest.QuestValidationException;
import java.util.Objects;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
@Stateless
public class ActionController {

    @Inject
    private CampaignController campaignController;

    @Inject
    private QuestController questController;

    @Inject
    private MessageController messageController;

    /**
     * Does prechecks and handles then the submitted action
     *
     * 1. Check if action is submitted against correct quest 2. Check if it's
     * the players turn 3. Check if action unit is active 4. Check remaining
     * action points for active unit 5. Check if action source field is the same
     * as active units location
     *
     * @param actionPlayer
     * @param wsAction
     * @return
     * @throws NotFoundException
     * @throws QuestValidationException
     * @throws ActionException
     */
    public Campaign handleAction(Player actionPlayer, WsAction wsAction) throws NotFoundException, QuestValidationException, ActionException {
        boolean questIsFinished = false;
        Campaign campaign = campaignController.getCampaignById(wsAction.getCampaignId());

        // check correct encounter
        if (wsAction.getQuestEncounterId() != campaign.getActiveQuest().getId()) {
            throw new QuestValidationException(Messages.WRONG_QUEST_ID);
        }

        GameUnit currentActiveUnit = null;
        GameUnit sourceUnit = questController.getGamUnit(wsAction.getSourceUnitId());

        // check if player is allowed to do a turn
        if (wsAction.isHeroAction()) {
            currentActiveUnit = campaign.getActiveHero();
        } else {
            currentActiveUnit = campaign.getActiveQuest().getActiveMonster();
        }
        if (!currentActiveUnit.getPlayedBy().equals(actionPlayer)) {
            throw new QuestValidationException("Not your turn! Currently active unit: " + currentActiveUnit.getName());
        }
        Objects.requireNonNull(currentActiveUnit, "Current active unit cannot be determined!");

        // check if action unit is active
        if (sourceUnit != null && !currentActiveUnit.equals(sourceUnit)) {
            throw new ActionException(Messages.WRONG_UNIT_FOR_ACTION);
        }

        // check remaining actions for active unit
        if (currentActiveUnit.getActions() == 0) {
            throw new ActionException(Messages.NO_ACTIONS_LEFT);
        }

        // check if action source is the same as active unit
        if (!currentActiveUnit.getCurrentLocation().containsAll(wsAction.getSourceFields())) {
            throw new ActionException(Messages.INVALID_START_POS_FOR_ACTION);
        }

        // handle different action types & check if action leads to quest ending
        questIsFinished = handleActionType(actionPlayer, wsAction, currentActiveUnit, campaign);

        // handle end of quest or set next unit active if needed
        if (questIsFinished) {
            campaign = campaignController.endActiveQuest(campaign);
        } else if (currentActiveUnit.getActions() == 0) {
            questController.setNextActiveUnit(campaign);
        }

        return campaign;
    }

    private boolean handleActionType(Player actionPlayer, WsAction wsAction, GameUnit activeUnit, Campaign campaign) throws ActionException, QuestValidationException {
        Message logMsg = null;
        switch (wsAction.getType()) {
            case MOVE:
                logMsg = MoveHandler.handle(campaign, actionPlayer, activeUnit, wsAction);
                messageController.saveMessage(logMsg);
                break;
            case ATTACK:
                logMsg = AttackHandler.handle(campaign, actionPlayer, wsAction);
                messageController.saveMessage(logMsg);
                break;
        }

        // update quest based on actions
        questController.updateQuestTrigger(campaign.getActiveQuest());

        // check post action victory conditions for quest
        return questController.isActiveQuestFinished(campaign);
    }
}
