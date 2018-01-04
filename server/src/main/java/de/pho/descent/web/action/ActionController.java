package de.pho.descent.web.action;

import de.pho.descent.shared.dto.WsAction;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.message.Message;
import de.pho.descent.web.campaign.CampaignController;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.message.MessageController;
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
    private MessageController messageController;

    public Campaign handleAction(Player actionPlayer, WsAction wsAction) throws NotFoundException, QuestValidationException, ActionException {

        Campaign campaign = campaignController.getCampaignById(wsAction.getCampaignId());

        // check correct encounter
        if (wsAction.getQuestEncounterId() != campaign.getActiveQuest().getId()) {
            throw new QuestValidationException("Wrong quest encounter id");
        }

        // check active player
        GameHero currentActiveHero = campaign.getActiveHero();
        if (!currentActiveHero.getPlayedBy().equals(actionPlayer)) {
            throw new QuestValidationException("Not your turn! Currently active hero: " + currentActiveHero.getName());
        }

        switch (wsAction.getType()) {
            case MOVE:
                Message logMsg = MoveHandler.handle(campaign, actionPlayer, wsAction);
                messageController.saveMessage(logMsg);
                break;
            case ATTACK:
                break;
        }

        return campaign;
    }
}
