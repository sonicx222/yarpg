package de.pho.descent.web.action;

import de.pho.descent.shared.dto.WsAction;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.map.MapField;
import de.pho.descent.shared.model.message.Message;
import de.pho.descent.shared.model.message.MessageType;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.shared.service.MapRangeService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author pho
 */
public class MoveHandler {

    public static Message handle(Campaign campaign, Player player, WsAction wsAction) throws ActionException {
        StringBuilder sbLog = new StringBuilder();
        GameHero activeHero = null;
        GameMonster activeMonster = null;
        int range = 0;

        if (wsAction.isHeroAction()) {
            activeHero = campaign.getActiveHero();

            // check if correct hero moves
            if (activeHero == null || !activeHero.equals(wsAction.getSourceHero())) {
                throw new ActionException("Wrong active hero for movement");
            }

            // action count check
            if (activeHero.getActions() == 0) {
                throw new ActionException("No action points left");
            }

            // src field check
            if (!activeHero.getCurrentLocation().equals(wsAction.getSourceField())) {
                throw new ActionException("Invalid starting position for movement");
            }
            sbLog.append("Hero '").append(activeHero.getName());
            range = wsAction.getSourceHero().getMovementPoints();
        } else {
            activeMonster = campaign.getActiveQuest().getActiveMonster();

            // check if correct monster moves
            if (activeMonster == null || !activeMonster.equals(wsAction.getSourceMonster())) {
                throw new ActionException("Wrong active monster for movement");
            }

            // action count check
            if (activeMonster.getActions() == 0) {
                throw new ActionException("No action points left");
            }

            // src field check
            if (!activeMonster.getCurrentLocation().equals(wsAction.getSourceField())) {
                throw new ActionException("Invalid starting position for movement");
            }
            sbLog.append("Monster '").append(activeHero.getName());
            range = wsAction.getSourceMonster().getMovementPoints();
        }

        List<MapField> unpassableFields = campaign.getHeroes().stream()
                .map(GameHero::getCurrentLocation).collect(Collectors.toList());
        unpassableFields.addAll(campaign.getActiveQuest().getMonsters().stream()
                .map(GameMonster::getCurrentLocation).collect(Collectors.toList()));
        Set<MapField> fieldsInRange = MapRangeService.getFieldsInRange(
                wsAction.getSourceField(), range,
                campaign.getActiveQuest().getMap().getMapFields(),
                unpassableFields);

        if (!fieldsInRange.contains(wsAction.getTargetField())) {
            throw new ActionException("Invalid target position for movement");
        }

        // move unit
        if (activeHero != null) {
            activeHero.setCurrentLocation(wsAction.getTargetField());
            activeHero.setActions(activeHero.getActions() - 1);
        } else if (activeMonster != null) {
            activeMonster.setCurrentLocation(wsAction.getTargetField());
            activeMonster.setActions(activeMonster.getActions() - 1);
        }

        //create log message
        sbLog.append("' moved from field ").append(wsAction.getSourceField().getId())
                .append(" to field ").append(wsAction.getTargetField().getId());

        return new Message(campaign, MessageType.GAME, player, sbLog.toString());
    }

}
