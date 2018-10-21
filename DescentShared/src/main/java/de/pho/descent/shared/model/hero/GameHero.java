package de.pho.descent.shared.model.hero;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.dice.DefenseDice;
import de.pho.descent.shared.model.item.Item;
import de.pho.descent.shared.model.map.MapField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author pho
 */
@Entity
public class GameHero extends GameUnit implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Enumerated(EnumType.STRING)
    private HeroTemplate heroTemplate;

    private int stamina;

    private int fatigue;

    private int might;

    private int knowledge;

    private int willpower;

    private int awareness;

    private int initiative;
    
    private boolean knockedOut;

    @Enumerated(EnumType.STRING)
    private Item weapon;

    @Enumerated(EnumType.STRING)
    private Item shield;

    @ElementCollection(targetClass = Item.class, fetch = FetchType.EAGER)
    @Column(name = "item", nullable = false)
    @CollectionTable(name = "hero_inventory")
    @Enumerated(EnumType.STRING)
    private Collection<Item> inventory = new ArrayList<>();

    private int xp;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastMessageUpdate;

    public GameHero() {
    }

    public GameHero(HeroTemplate template) {
        super.setName(template.getName());
        super.setMovementPoints(template.getSpeed());
        super.setTotalLife(template.getHealth());
        super.setCurrentLife(template.getHealth());
        this.heroTemplate = template;
        this.stamina = template.getStamina();
        this.might = template.getMight();
        this.knowledge = template.getKnowledge();
        this.willpower = template.getWillpower();
        this.awareness = template.getAwareness();
        this.initiative = template.getInitiative();

        // starting gear
        this.weapon = template.getStartWeapon();
        this.shield = template.getStartShield();
        if (template.getStartTrinket() != null) {
            this.inventory.add(template.getStartTrinket());
        }

        this.xp = 0;
    }

    public int rollInitiative() {
        return ThreadLocalRandom.current().nextInt(0, 20) + initiative;
    }
    
    @Transient
    @JsonIgnore
    public List<DefenseDice> getDefense() {
        List<DefenseDice> defense = new ArrayList<>();
        defense.add(heroTemplate.getDefense());
        if ((shield != null) && (shield.getDefenseDice() != null) ) {
            defense.addAll(shield.getDefenseDice());
        }
        
        return defense;
    }
    
    public void addFatigue(int moreFatigue) {
        if ((fatigue + moreFatigue) > stamina) {
            int damage = (fatigue + moreFatigue) - stamina;
            setCurrentLife(Math.max(getCurrentLife() - damage, 0));
            if (getCurrentLife() == 0) {
                knockOutHero();
            }
        } else {
            fatigue += moreFatigue;
        }
    }
    
    /**
     * 1. full fatigue
     * 2. full damage / no hp
     * 3. remove all old conditions
     */
    public void knockOutHero() {
        knockedOut = true;
        fatigue = stamina;
        setCurrentLife(0);
        // TODO: remove all conditions
    }

    public HeroTemplate getHeroTemplate() {
        return heroTemplate;
    }

    public void setHeroTemplate(HeroTemplate heroTemplate) {
        this.heroTemplate = heroTemplate;
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

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getFatigue() {
        return fatigue;
    }

    public void setFatigue(int fatigue) {
        this.fatigue = fatigue;
    }

    public int getInitiative() {
        return initiative;
    }

    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }

    public boolean isKnockedOut() {
        return knockedOut;
    }

    public void setKnockedOut(boolean knockedOut) {
        this.knockedOut = knockedOut;
    }

    public Item getWeapon() {
        return weapon;
    }

    public void setWeapon(Item weapon) {
        this.weapon = weapon;
    }

    public Item getShield() {
        return shield;
    }

    public void setShield(Item shield) {
        this.shield = shield;
    }

    public Collection<Item> getInventory() {
        return inventory;
    }

    public void setInventory(List<Item> inventory) {
        this.inventory = inventory;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void addXp(int amount) {
        this.xp = this.xp + amount;
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

    public Date getLastMessageUpdate() {
        return lastMessageUpdate;
    }

    public void setLastMessageUpdate(Date lastMessageUpdate) {
        this.lastMessageUpdate = lastMessageUpdate;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.heroTemplate);
        hash = 53 * hash + this.stamina;
        hash = 53 * hash + this.fatigue;
        hash = 53 * hash + this.might;
        hash = 53 * hash + this.knowledge;
        hash = 53 * hash + this.willpower;
        hash = 53 * hash + this.awareness;
        hash = 53 * hash + this.initiative;
        hash = 53 * hash + Objects.hashCode(this.weapon);
        hash = 53 * hash + Objects.hashCode(this.shield);
        hash = 53 * hash + Objects.hashCode(this.inventory);
        hash = 53 * hash + this.xp;
        hash = 53 * hash + Objects.hashCode(this.lastMessageUpdate);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GameHero other = (GameHero) obj;
        if (this.stamina != other.stamina) {
            return false;
        }
        if (this.fatigue != other.fatigue) {
            return false;
        }
        if (this.might != other.might) {
            return false;
        }
        if (this.knowledge != other.knowledge) {
            return false;
        }
        if (this.willpower != other.willpower) {
            return false;
        }
        if (this.awareness != other.awareness) {
            return false;
        }
        if (this.initiative != other.initiative) {
            return false;
        }
        if (this.xp != other.xp) {
            return false;
        }
        if (this.heroTemplate != other.heroTemplate) {
            return false;
        }
        if (this.weapon != other.weapon) {
            return false;
        }
        if (this.shield != other.shield) {
            return false;
        }
        if (!Objects.equals(this.inventory, other.inventory)) {
            return false;
        }
        if (!Objects.equals(this.lastMessageUpdate, other.lastMessageUpdate)) {
            return false;
        }
        return true;
    }

}
