package de.pho.descent.shared.model.hero.skill;

/**
 *
 * @author pho
 */
public enum HeroSkill {
    START_WARRIOR_KNIGHT("Oath of Honor", "0_warrior_knight_oath_of_honor"),
    START_WARRIOR_BERSERKER("Rage", "0_warrior_berserker_rage"),
    START_HEALER_DISCIPLE("Prayer of Healing", "0_healer_disciple_prayer_of_healing"),
    START_HEALER_SPIRITSPEAKER("Stoneskin", "0_healer_spiritspeaker_stoneskin"),
    START_SCOUT_THIEF("Greedy", "0_scout_thief_greedy"),
    START_SCOUT_WILDLANDER("Nimble", "0_scout_wildlander_nimble"),
    START_MAGE_RUNEMASTER("Runic Knowledge", "0_mage_runemaster_runic_knowledge"),
    START_MAGE_NECROMANCER("Raise Dead", "0_mage_necromancer_raise_dead");

    private final String name;

    private final String imagePath;

    private HeroSkill(String name, String imagePath) {
        this.name = name;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }
}
