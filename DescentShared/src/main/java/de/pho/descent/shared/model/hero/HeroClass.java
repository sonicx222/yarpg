package de.pho.descent.shared.model.hero;

/**
 *
 * @author pho
 */
public enum HeroClass {

    APOTHECARY("Apothecary", Archetype.HEALER),
    BARD("Bard", Archetype.HEALER),
    DISCIPLE("Disciple", Archetype.HEALER),
    PROPHET("Prophet", Archetype.HEALER),
    SPIRITSPEAKER("Spiritspeaker", Archetype.HEALER),
    WATCHMAN("Watchman", Archetype.HEALER),
    
    BEASTMASTER("Beastmaster", Archetype.WARRIOR),
    BERSERKER("Berserker", Archetype.WARRIOR),
    CHAMPION("Champion", Archetype.WARRIOR),
    KNIGHT("Knight", Archetype.WARRIOR),
    MARSHAL("Marshal", Archetype.WARRIOR),
    SKIRMISHER("Skirmisher", Archetype.WARRIOR),
    STEELCASTER("Steelcaster", Archetype.WARRIOR),
    
    BOUNTYHUNTER("Bounty Hunter", Archetype.SCOUT),
    MONK("Monk", Archetype.SCOUT),
    SHADOWWALKER("Shadow Walker", Archetype.SCOUT),
    STALKER("Stalker", Archetype.SCOUT),
    THIEF("Thief", Archetype.SCOUT),
    TREASUREHUNTER("Treasure Hunter", Archetype.SCOUT),
    WILDLANDER("Wildlander", Archetype.SCOUT),
    
    BATTLEMAGE("Battlemage", Archetype.MAGE),
    CONJURER("Conjurer", Archetype.MAGE),
    GEOMANCER("Geomancer", Archetype.MAGE),
    HEXER("Hexer", Archetype.MAGE),
    NECROMANCER("Necromancer", Archetype.MAGE),
    RUNEMASTER("Runemaster", Archetype.MAGE);

    private final String text;
    private final Archetype archetype;

    private HeroClass(String name, Archetype type) {
        this.text = name;
        this.archetype = type;
    }

    public String getText() {
        return text;
    }

    public Archetype getArchetype() {
        return archetype;
    }
    
    
}
