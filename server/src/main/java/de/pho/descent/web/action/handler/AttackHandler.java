package de.pho.descent.web.action.handler;

import de.pho.descent.shared.dto.WsAction;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.message.Message;
import de.pho.descent.shared.model.message.MessageType;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.web.action.ActionException;

/**
 *
 * @author pho
 */
public class AttackHandler {

    public static Message handle(Campaign campaign, Player player, WsAction wsAction) throws ActionException {
        StringBuilder sbLog = new StringBuilder();
        GameHero activeHero = null;
        GameMonster activeMonster = null;

        // range check
        // TODO
        return new Message(campaign, MessageType.GAME, player, sbLog.toString());
    }

}
