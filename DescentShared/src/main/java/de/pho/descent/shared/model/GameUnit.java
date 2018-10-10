package de.pho.descent.shared.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.pho.descent.shared.model.map.MapField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author pho
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class GameUnit extends GameEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Player playedBy;
    
    @OneToMany(mappedBy = "gameUnit")
    @JsonManagedReference(value="unit-location")
    private List<MapField> currentLocation = new ArrayList<>();

    @Temporal(TemporalType.DATE)
    private final Date createdOn = new Date();

    private int actions;

    private int totalLife;

    private int currentLife;

    private int movementPoints;

    private int movementSpent;

    private boolean active;

    public List<MapField> getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(List<MapField> currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Player getPlayedBy() {
        return playedBy;
    }

    public void setPlayedBy(Player playedBy) {
        this.playedBy = playedBy;
    }

    public int getActions() {
        return actions;
    }

    public void setActions(int actions) {
        this.actions = actions;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
}
