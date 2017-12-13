package de.pho.descent.shared.model.hero;

import de.pho.descent.shared.model.GameUnit;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

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

}
