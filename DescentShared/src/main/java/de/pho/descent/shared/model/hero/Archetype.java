package de.pho.descent.shared.model.hero;

import static de.pho.descent.shared.model.hero.HeroClass.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author pho
 */
public enum Archetype {
    WARRIOR("Warrior", BERSERKER, KNIGHT),
    MAGE("Mage", NECROMANCER, RUNEMASTER),
    SCOUT("Scout", THIEF, WILDLANDER),
    HEALER("Healer", DISCIPLE, SPIRITSPEAKER);

    private final String text;
    private final List<HeroClass> classes;

    private Archetype(String text, HeroClass... classes) {
        this.text = text;
        this.classes = Collections.unmodifiableList(Arrays.asList(classes));
    }

    public String getText() {
        return text;
    }

    public List<HeroClass> getClasses() {
        return classes;
    }
    
    
}
