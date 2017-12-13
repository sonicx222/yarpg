package de.pho.descent.shared.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author pho
 */
@MappedSuperclass
public class GameUnit extends GameEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Player playedBy;

    @Temporal(TemporalType.DATE)
    private final Date createdOn = new Date();

    private int turnsLeft;

    private int totalLife;

    private int currentLife;

    private int movementPoints;

    private int movementSpent;

    private boolean isActive;

    public Player getPlayedBy() {
        return playedBy;
    }

    public void setPlayedBy(Player playedBy) {
        this.playedBy = playedBy;
    }

    public int getTurnsLeft() {
        return turnsLeft;
    }

    public void setTurnsLeft(int turnsLeft) {
        this.turnsLeft = turnsLeft;
    }

    public int getTotalLife() {
        return totalLife;
    }

    public void setTotalLife(int totalLife) {
        this.totalLife = totalLife;
    }

    public int getCurrentLife() {
        return currentLife;
    }

    public void setCurrentLife(int currentLife) {
        this.currentLife = currentLife;
    }

    public int getMovementPoints() {
        return movementPoints;
    }

    public void setMovementPoints(int movementPoints) {
        this.movementPoints = movementPoints;
    }

    public int getMovementSpent() {
        return movementSpent;
    }

    public void setMovementSpent(int movementSpent) {
        this.movementSpent = movementSpent;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    
}
