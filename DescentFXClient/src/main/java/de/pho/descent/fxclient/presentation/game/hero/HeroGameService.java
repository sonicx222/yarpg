package de.pho.descent.fxclient.presentation.game.hero;

import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.shared.model.hero.GameHero;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class HeroGameService {

    @Inject
    private HeroGameModel heroGameModel;

    @Inject
    private GameDataModel gameDataModel;

    @Inject
    private Credentials credentials;

    @PostConstruct
    public void init() {
    }

    public void updateHeroStats() {
        GameHero hero = gameDataModel.getCurrentCampaign().getGameHeroes().stream()
                .filter(h -> h.getPlayedBy().getUsername().equals(credentials.getUsername()))
                .findAny()
                .orElse(null);

        heroGameModel.getHealth().set(String.valueOf(hero.getCurrentLife()));
        heroGameModel.getMovementLeft().set(String.valueOf(hero.getMovementPoints() - hero.getMovementSpent()));
        heroGameModel.getExhaustion().set(String.valueOf(hero.getExhaustion()));
        heroGameModel.getActionsLeft().set(String.valueOf(hero.getActions()));
    }

    public void updateUIControls() {
        // disable controls when it's not players turn
        if (gameDataModel.getCurrentCampaign().getActiveHero() != null
                && gameDataModel.getCurrentCampaign().getActiveHero().getPlayedBy().getUsername().equals(credentials.getUsername())
                && gameDataModel.getCurrentCampaign().getActiveHero().getActions() > 0) {
            gameDataModel.getMoveButton().setDisable(false);
            gameDataModel.getAttackButton().setDisable(false);
        } else {
            gameDataModel.getMoveButton().setDisable(true);
            gameDataModel.getAttackButton().setDisable(true);
        }
    }
}