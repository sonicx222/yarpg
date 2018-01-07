package de.pho.descent.fxclient.presentation.game.hero;

import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javax.annotation.PostConstruct;

/**
 *
 * @author pho
 */
public class HeroGameModel {

    private static final Logger LOGGER = Logger.getLogger(HeroGameModel.class.getName());

    // hero stats
    private StringProperty movementLeft;
    private StringProperty health;
    private StringProperty exhaustion;
    private StringProperty actionsLeft;

    @PostConstruct
    public void init() {
    }

    public StringProperty getMovementLeft() {
        return movementLeft;
    }

    public void setMovementLeft(StringProperty movementLeft) {
        this.movementLeft = movementLeft;
    }

    public StringProperty getHealth() {
        return health;
    }

    public void setHealth(StringProperty health) {
        this.health = health;
    }

    public StringProperty getExhaustion() {
        return exhaustion;
    }

    public void setExhaustion(StringProperty exhaustion) {
        this.exhaustion = exhaustion;
    }

    public StringProperty getActionsLeft() {
        return actionsLeft;
    }

    public void setActionsLeft(StringProperty actionsLeft) {
        this.actionsLeft = actionsLeft;
    }


    
}
