package de.pho.descent.fxclient.presentation.general;

import static de.pho.descent.fxclient.MainApp.showError;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.ActionClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.game.map.MapDataModel;
import de.pho.descent.shared.dto.WsAction;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsGameMap;
import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.PlaySide;
import de.pho.descent.shared.model.action.ActionType;
import de.pho.descent.shared.model.map.MapField;
import de.pho.descent.shared.model.monster.GameMonster;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class ActionService {

    @Inject
    private Credentials credentials;

    @Inject
    private GameService gameService;

    @Inject
    private GameDataModel gameDataModel;

    @Inject
    private MapDataModel mapDataModel;

    @PostConstruct
    public void init() {
    }

    public void handleMove(Rectangle targetRect) {
        WsCampaign updatedCampaign = null;
        WsAction moveAction = new WsAction();
        moveAction.setCampaignId(gameDataModel.getCurrentCampaign().getId());
        moveAction.setQuestEncounterId(gameDataModel.getCurrentQuestEncounter().getId());
        moveAction.setType(ActionType.MOVE);
        moveAction.setSourceFields(mapDataModel.getActiveUnit().getKey().getCurrentLocation());
        
        GameUnit activeUnit = mapDataModel.getActiveUnit().getKey();
        MapField referenceTargetField  = (MapField) targetRect.getUserData();
        moveAction.getTargetFields().add(referenceTargetField);
        if (activeUnit instanceof GameMonster
                && ((GameMonster) activeUnit).getMonsterTemplate().getFieldSize() == 2) {
            WsGameMap map = gameDataModel.getCurrentQuestMap();
            MapField east = map.getField(referenceTargetField.getxPos() + 1, referenceTargetField.getyPos());
            moveAction.getTargetFields().add(east);
            MapField southeast = map.getField(referenceTargetField.getxPos() + 1, referenceTargetField.getyPos() + 1);
            moveAction.getTargetFields().add(southeast);
            MapField south = map.getField(referenceTargetField.getxPos(), referenceTargetField.getyPos() + 1);
            moveAction.getTargetFields().add(south);
        }       

        if (gameDataModel.getCurrentQuestEncounter().getCurrentTurn() == PlaySide.HEROES) {
            moveAction.setHeroAction(true);
        } else {
            moveAction.setHeroAction(false);
        }
        moveAction.setSourceUnitId(mapDataModel.getActiveUnit().getKey().getId());

        try {
            updatedCampaign = ActionClient.sendAction(credentials, moveAction);
        } catch (ServerException ex) {
            showError(ex);
        }
        if (updatedCampaign != null) {
            // update
            gameService.updateGameState(updatedCampaign);

            // UI controls
//            gameService.enableOtherButtons();
            gameDataModel.getMoveButton().setText("Move");
            gameDataModel.setCurrentAction(null);
        }
    }

    public void handleAttack(Button targetBtn) {
        WsCampaign updatedCampaign = null;
        WsAction attackAction = new WsAction();
        GameUnit unit = (GameUnit) targetBtn.getUserData();

        attackAction.setCampaignId(gameDataModel.getCurrentCampaign().getId());
        attackAction.setQuestEncounterId(gameDataModel.getCurrentQuestEncounter().getId());
        attackAction.setType(ActionType.ATTACK);
        attackAction.setSourceUnitId(mapDataModel.getActiveUnit().getKey().getId());
        attackAction.setSourceFields(mapDataModel.getActiveUnit().getKey().getCurrentLocation());
        if (unit == null) {
            attackAction.setTargetUnitId(0l);
        } else {
            attackAction.setTargetUnitId(unit.getId());
        }
        attackAction.setTargetFields(unit.getCurrentLocation());

        if (gameDataModel.getCurrentQuestEncounter().getCurrentTurn() == PlaySide.HEROES) {
            attackAction.setHeroAction(true);
        } else {
            attackAction.setHeroAction(false);
        }

        try {
            updatedCampaign = ActionClient.sendAction(credentials, attackAction);
        } catch (ServerException ex) {
            showError(ex);
        }
        if (updatedCampaign != null) {
            // update
            gameService.updateGameState(updatedCampaign);

            // UI controls
//            gameService.enableOtherButtons();
            gameDataModel.getAttackButton().setText("Attack");
            gameDataModel.setCurrentAction(null);
        }
    }
}
