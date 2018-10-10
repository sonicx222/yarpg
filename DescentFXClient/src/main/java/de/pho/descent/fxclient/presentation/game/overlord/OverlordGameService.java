package de.pho.descent.fxclient.presentation.game.overlord;

import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.presentation.game.hero.HeroGameModel;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.shared.model.hero.GameHero;
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
        gameDataModel.getRound().set("Round "  + gameDataModel.getCurrentQuestEncounter().getRound());
            
        // disable controls when it's not players turn
        if (gameDataModel.getCurrentQuestEncounter().getActiveMonster() != null
                && gameDataModel.getCurrentQuestEncounter().getActiveMonster().getPlayedBy().getUsername().equals(credentials.getUsername())
                && gameDataModel.getCurrentQuestEncounter().getActiveMonster().getActions() > 0) {
            gameDataModel.getMoveButton().setDisable(false);
            gameDataModel.getAttackButton().setDisable(false);
        } else {
            gameDataModel.getMoveButton().setDisable(true);
            gameDataModel.getAttackButton().setDisable(true);
        }
    }
}
