package de.pho.descent.shared.model.hero;

import de.pho.descent.shared.model.dice.DefenseDice;
import static de.pho.descent.shared.model.dice.DefenseDice.*;
import static de.pho.descent.shared.model.hero.Archetype.*;

/**
 *
 * @author pho
 */
public enum HeroTemplate {

    GRISBAN("Grisban the Thirsty", WARRIOR, 3, 14, 4, GRAY, 5, 2, 3, 1, "Grisban"),
    SYNDRAEL("Syndrael", WARRIOR, 4, 12, 4, GRAY, 4, 3, 2, 2, "Syndrael"),
    JAINFAIRWOOD("Jain Fairwood", SCOUT, 5, 8, 5, GRAY, 2, 3, 2, 4, "JainFairwood"),
    TOMBLEBURROWELL("Tomble Burrowell", SCOUT, 4, 8, 5, GRAY, 1, 2, 3, 5, "TombleBurrowell"),
    ASHRIAN("Ashrian", HEALER, 5, 10, 4, GRAY, 2, 2, 3, 4, "Ashrian"),
    AVRICALBRIGHT("Avric Albright", HEALER, 4, 12, 4, GRAY, 2, 3, 4, 2, "AvricAlbright"),
    WIDOWTARHA("Widow Tarha", MAGE, 4, 10, 4, GRAY, 2, 4, 3, 2, "WidowTarha"),
    LEORICOFTHEBOOK("Leoric of the Book", MAGE, 4, 8, 5, GRAY, 1, 5, 2, 3, "LeoricoftheBook");

    private final String name;

    private final int health;

    private final int speed;

    private final int stamina;

    private final DefenseDice defense;

    private final Archetype heroArchetype;

    private final int might;

    private final int knowledge;

    private final int willpower;

    private final int awareness;

    private final String imageName;

    private HeroTemplate(String name, Archetype heroArchetype, int speed, int health, int stamina, DefenseDice defense, int might, int knowledge, int willpower, int awareness, String imageName) {
        this.name = name;
        this.heroArchetype = heroArchetype;
        this.health = health;
        this.speed = speed;
        this.stamina = stamina;
        this.defense = defense;
        this.might = might;
        this.knowledge = knowledge;
        this.willpower = willpower;
        this.awareness = awareness;
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getSpeed() {
        return speed;
    }

    public int getStamina() {
        return stamina;
    }

    public DefenseDice getDefense() {
        return defense;
    }

    public Archetype getHeroArchetype() {
        return heroArchetype;
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

    public String getImageName() {
        return imageName;
    }

    @Override
    public String toString() {
        return name;
    }
}
