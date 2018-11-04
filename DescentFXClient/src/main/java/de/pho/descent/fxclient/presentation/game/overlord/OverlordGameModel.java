package de.pho.descent.fxclient.presentation.game.overlord;

import de.pho.descent.shared.model.monster.GameMonster;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javax.annotation.PostConstruct;

/**
 *
 * @author pho
 */
public class OverlordGameModel {

    private static final Logger LOGGER = Logger.getLogger(OverlordGameModel.class.getName());

    private ObservableList<GameMonster> monsters = FXCollections.observableArrayList();
    private Button activateButton;

    @PostConstruct
    public void init() {
    }

    public ObservableList<GameMonster> getMonsters() {
        return monsters;
    }

    public void setMonsters(ObservableList<GameMonster> monsters) {
        this.monsters = monsters;
    }

    public Button getActivateButton() {
        return activateButton;
    }

    public void setActivateButton(Button activateButton) {
        this.activateButton = activateButton;
    }

}
