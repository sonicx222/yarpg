package de.pho.descent.fxclient.presentation.game.overlord;

import static de.pho.descent.fxclient.MainApp.showError;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.QuestClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.shared.dto.WsQuestEncounter;
import de.pho.descent.shared.model.monster.MonsterGroup;
import de.pho.descent.shared.model.quest.QuestPhase;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class OverlordGameService {

    @Inject
    private OverlordGameModel overlordGameModel;

    @Inject
    private GameDataModel gameDataModel;

    @Inject
    private Credentials credentials;

    @PostConstruct
    public void init() {
    }

    public void updateMonsterStats() {
        overlordGameModel.getMonsters().clear();
        overlordGameModel.getMonsters().addAll(gameDataModel.getCurrentQuestEncounter().getGameMonsters());
    }

    public void updateUIControls() {
        // round label
        gameDataModel.getRound().set("Round " + gameDataModel.getCurrentQuestEncounter().getRound());
        WsQuestEncounter questEncounter = gameDataModel.getCurrentQuestEncounter();

        // disable controls when it's not players turn
        if (questEncounter.getActiveMonster() != null
                && questEncounter.getActiveMonster().getPlayedBy().getUsername().equals(credentials.getUsername())
                && questEncounter.getActiveMonster().getActions() > 0) {
            gameDataModel.getMoveButton().setDisable(false);
            gameDataModel.getAttackButton().setDisable(false);
            gameDataModel.getTurnButton().setDisable(false);
        } else {
            gameDataModel.getMoveButton().setDisable(true);
            gameDataModel.getAttackButton().setDisable(true);
            gameDataModel.getTurnButton().setDisable(true);
        }

        if (questEncounter.getPhase() == QuestPhase.MONSTER_ACTIVATION) {
            overlordGameModel.getActivateButton().setDisable(false);
        } else {
            overlordGameModel.getActivateButton().setDisable(true);
        }
    }

    public void activateMonsterGroup(MonsterGroup group) {
        Objects.requireNonNull(gameDataModel.getCurrentQuestEncounter());
        WsQuestEncounter wsQuestEncounter = null;
        try {
            wsQuestEncounter = QuestClient.activateMonsterGroup(
                    credentials.getUsername(), credentials.getPassword(),
                    gameDataModel.getCurrentCampaign().getQuestEncounterId(), group);
        } catch (ServerException ex) {
            showError(ex);
        }
        if (wsQuestEncounter != null) {
            gameDataModel.setCurrentQuestEncounter(wsQuestEncounter);
        }
    }
}
