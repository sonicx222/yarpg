package de.pho.descent.web.action;

import de.pho.descent.web.action.handler.MoveHandler;
import de.pho.descent.shared.dto.WsAction;
import de.pho.descent.shared.model.PlaySide;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.message.Message;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.web.action.handler.AttackHandler;
import de.pho.descent.web.campaign.CampaignController;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.message.MessageController;
import de.pho.descent.web.quest.QuestController;
import de.pho.descent.web.quest.QuestValidationException;
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

    public Campaign handleAction(Player actionPlayer, WsAction wsAction) throws NotFoundException, QuestValidationException, ActionException {

        Campaign campaign = campaignController.getCampaignById(wsAction.getCampaignId());

        // check correct encounter
        if (wsAction.getQuestEncounterId() != campaign.getActiveQuest().getId()) {
            throw new QuestValidationException("Wrong quest encounter id");
        }

        // check active player
        if (campaign.getActiveQuest().getCurrentTurn() == PlaySide.HEROES) {
            GameHero currentActiveHero = campaign.getActiveHero();
            if (!currentActiveHero.getPlayedBy().equals(actionPlayer)) {
                throw new QuestValidationException("Not your turn! Currently active hero: " + currentActiveHero.getName());
            }

            // handle different types
            handleActionType(actionPlayer, wsAction, campaign);

            if (currentActiveHero.getActions() == 0) {
                questController.setNextActiveUnit(campaign);
            }
        } else {
            GameMonster currentActiveMonster = campaign.getActiveQuest().getActiveMonster();
            if (!currentActiveMonster.getPlayedBy().equals(actionPlayer)) {
                throw new QuestValidationException("Not your turn! Currently active monster: " + currentActiveMonster.getName());
            }

            // handle different types
            handleActionType(actionPlayer, wsAction, campaign);

            if (currentActiveMonster.getActions() == 0) {
                questController.setNextActiveUnit(campaign);
            }
        }

        return campaign;
    }

    private void handleActionType(Player actionPlayer, WsAction wsAction, Campaign campaign) throws ActionException {
        Message logMsg = null;
        switch (wsAction.getType()) {
            case MOVE:
                logMsg = MoveHandler.handle(campaign, actionPlayer, wsAction);
                messageController.saveMessage(logMsg);
                break;
            case ATTACK:
                logMsg = AttackHandler.handle(campaign, actionPlayer, wsAction);
                messageController.saveMessage(logMsg);
                break;
        }
    }
}
