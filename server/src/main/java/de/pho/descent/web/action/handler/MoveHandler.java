package de.pho.descent.web.action.handler;

import de.pho.descent.shared.dto.WsAction;
import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.map.MapField;
import de.pho.descent.shared.model.message.Message;
import de.pho.descent.shared.model.message.MessageType;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.shared.service.MapRangeService;
import de.pho.descent.web.action.ActionException;
import de.pho.descent.web.exception.Messages;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author pho
 */
public class MoveHandler {

    public static Message handle(Campaign campaign, Player player, GameUnit activeUnit, WsAction wsAction) throws ActionException {
        StringBuilder sbLog = new StringBuilder();

        // treat GameUnit locations as unpassable fields for range check
        List<MapField> unpassableFields = new ArrayList<>();

        // add hero locations
        for (GameHero hero : campaign.getHeroes()) {
            unpassableFields.addAll(hero.getCurrentLocation());
        }
        // add monster locations
        for (GameMonster monster : campaign.getActiveQuest().getMonsters()) {
            unpassableFields.addAll(monster.getCurrentLocation());
        }

        // calculate possible fields based on movement range and unpassable ingame fields
        Set<MapField> fieldsInRange = MapRangeService.getFieldsInMovementRange(
                activeUnit.getCurrentLocation(), activeUnit.getMovementPoints(),
                campaign.getActiveQuest().getMap().getMapFields(),
                unpassableFields);

        // range check
        if (!fieldsInRange.containsAll(wsAction.getTargetFields())) {
            throw new ActionException(Messages.INVALID_TARGET_POS_FOR_ACTION);
        }

        // move unit
        List<MapField> targetLocation = new ArrayList<>();
        activeUnit.getCurrentLocation().forEach(field -> field.setGameUnit(null));
        // use loaded map fields from persistence context to rearrange unit location
        for (MapField wsField : wsAction.getTargetFields()) {
            MapField targetLocationField = campaign.getActiveQuest().getMap().getField(wsField.getxPos(), wsField.getyPos());
            targetLocationField.setGameUnit(activeUnit);
            targetLocation.add(targetLocationField);
        }
        activeUnit.setCurrentLocation(targetLocation);
        
        // used one action point
        activeUnit.setActions(activeUnit.getActions() - 1);

        //create log message
        sbLog.append("Unit '").append(activeUnit.getName()).append("' moved from field(s): ");
        wsAction.getSourceFields().forEach(field -> sbLog.append(field.getId()).append(" "));
        sbLog.append("to field(s): ");
        wsAction.getTargetFields().forEach(field -> sbLog.append(field.getId()).append(" "));
        return new Message(campaign, MessageType.GAME, player, sbLog.toString());
    }

}
