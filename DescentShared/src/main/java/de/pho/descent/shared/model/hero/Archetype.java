package de.pho.descent.shared.model.hero;


import static de.pho.descent.shared.model.hero.Class.*;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author pho
 */
public enum Archetype {
    WARRIOR("Warrior", Arrays.asList(BEASTMASTER, BERSERKER, CHAMPION, KNIGHT, MARSHAL, SKIRMISHER, STEELCASTER)),
    MAGE("Mage", Arrays.asList(BATTLEMAGE, CONJURER, GEOMANCER, HEXER, NECROMANCER, RUNEMASTER)),
    SCOUT("Scout", Arrays.asList(BOUNTYHUNTER, MONK, SHADOWWALKER, STALKER, THIEF, TREASUREHUNTER, TREASUREHUNTER, WILDLANDER)),
    HEALER("Healer", Arrays.asList(APOTHECARY, BARD, DISCIPLE, PROPHET, SPIRITSPEAKER, WATCHMAN));

    private final String text;
    private final List<Class> classes;

    private Archetype(String text, List<Class> classes) {
        this.text = text;
        this.classes = classes;
    }
}
