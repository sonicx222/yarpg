package de.pho.descent.shared.model.hero;

import de.pho.descent.shared.model.GameUnit;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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

    private int exhaustion;

    private int might;

    private int knowledge;

    private int willpower;

    private int awareness;

    private int initiative;

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
    }

    public int rollInitiative() {
        return ThreadLocalRandom.current().nextInt(0, 20) + initiative;
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

    public int getExhaustion() {
        return exhaustion;
    }

    public void setExhaustion(int exhaustion) {
        this.exhaustion = exhaustion;
    }

    public int getInitiative() {
        return initiative;
    }

    public void setInitiative(int initiative) {
        this.initiative = initiative;
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
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.heroTemplate);
        hash = 59 * hash + this.stamina;
        hash = 59 * hash + this.exhaustion;
        hash = 59 * hash + this.might;
        hash = 59 * hash + this.knowledge;
        hash = 59 * hash + this.willpower;
        hash = 59 * hash + this.awareness;
        hash = 59 * hash + this.initiative;
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
        if (!Objects.equals(this.getId(), other.getId())) {
            return false;
        }
        if (!Objects.equals(this.getPlayedBy().getUsername(), getPlayedBy().getUsername())) {
            return false;
        }
        if (this.stamina != other.stamina) {
            return false;
        }
        if (this.exhaustion != other.exhaustion) {
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
        if (this.heroTemplate != other.heroTemplate) {
            return false;
        }
        return true;
    }

}
