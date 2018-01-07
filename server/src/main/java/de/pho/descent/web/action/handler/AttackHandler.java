package de.pho.descent.web.action.handler;

import de.pho.descent.shared.dto.WsAction;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.map.MapField;
import de.pho.descent.shared.model.message.Message;
import de.pho.descent.shared.model.message.MessageType;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.shared.service.MapRangeService;
import de.pho.descent.web.action.ActionException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author pho
 */
public class AttackHandler {

    public static Message handle(Campaign campaign, Player player, WsAction wsAction) throws ActionException {
        StringBuilder sbLog = new StringBuilder();
        GameHero activeHero = null;
        GameMonster activeMonster = null;

        if (wsAction.isHeroAction()) {
            activeHero = campaign.getActiveHero();

            // check if correct hero attacks
            if (activeHero == null || !activeHero.equals(wsAction.getSourceHero())) {
                throw new ActionException("Wrong active hero for attack action");
            }

            // action count check
            if (activeHero.getActions() == 0) {
                throw new ActionException("No action points left");
            }

            // src field check
            if (!activeHero.getCurrentLocation().equals(wsAction.getSourceField())) {
                throw new ActionException("Invalid position for attack");
            }

            
            // TODO
            
            
            
            
        } else {
            activeMonster = campaign.getActiveQuest().getActiveMonster();

            // check if correct monster attacks
            if (activeMonster == null || !activeMonster.equals(wsAction.getSourceMonster())) {
                throw new ActionException("Wrong active monster for attack action");
            }

            // action count check
            if (activeMonster.getActions() == 0) {
                throw new ActionException("No action points left");
            }

            // src field check
            if (!activeMonster.getCurrentLocation().equals(wsAction.getSourceField())) {
                throw new ActionException("Invalid position for attack");
            }

            // TODO
            
        }

        // TODO

        return new Message(campaign, MessageType.GAME, player, sbLog.toString());
    }

}
