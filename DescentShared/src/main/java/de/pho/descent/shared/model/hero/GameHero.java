package de.pho.descent.shared.model.hero;

import de.pho.descent.shared.model.GameEntity;
import de.pho.descent.shared.model.Player;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author pho
 */
@Entity
public class GameHero extends GameEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Player playedBy;

    @Temporal(TemporalType.DATE)
    private final Date createdOn = new Date();

    @Enumerated(EnumType.STRING)
    private Archetype archetype;

    private int turnsLeft;

    private int totalLife;

    private int currentLife;

    private int stamina;

    private int exhaustion;

    private int movementPoints;

    private int movementSpent;

    private int might;

    private int knowledge;

    private int willpower;

    private int awareness;

    private int initiative;

    private boolean isActive;

    public GameHero() {
    }

    public GameHero(HeroTemplate template) {
        super.setName(template.getName());
        this.archetype = template.getArchetype();
        this.movementPoints = template.getSpeed();
        this.totalLife = template.getHealth();
        this.stamina = template.getStamina();
        this.might = template.getMight();
        this.knowledge = template.getKnowledge();
        this.willpower = template.getWillpower();
        this.awareness = template.getAwareness();
    }

    public Player getPlayedBy() {
        return playedBy;
    }

    public void setPlayedBy(Player playedBy) {
        this.playedBy = playedBy;
    }

    public Archetype getArchetype() {
        return archetype;
    }

    public void setArchetype(Archetype archetype) {
        this.archetype = archetype;
    }

    public void setMight(int might) {
        this.might = might;
    }

    public void setKnowledge(int knowledge) {
        this.knowledge = knowledge;
    }

    public void setWillpower(int willpower) {
        this.willpower = willpower;
    }

    public void setAwareness(int awareness) {
        this.awareness = awareness;
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

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getExhaustion() {
        return exhaustion;
    }

    public void setExhaustion(int exhaustion) {
        this.exhaustion = exhaustion;
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

    public int getInitiative() {
        return initiative;
    }

    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public int getMight() {
        return might;
    }

    public int getKnowledge() {
        return knowledge;
    }

    public int getWillpower() {
        return willpower;
    }

    public int getAwareness() {
        return awareness;
    }

}
