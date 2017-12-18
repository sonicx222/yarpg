 package de.pho.descent.shared.model.hero;

import de.pho.descent.shared.model.dice.DefenseDice;
import static de.pho.descent.shared.model.dice.DefenseDice.*;
import static de.pho.descent.shared.model.hero.Archetype.*;
import static de.pho.descent.shared.model.hero.HeroClass.*;
import de.pho.descent.shared.model.hero.skill.HeroSkill;
import static de.pho.descent.shared.model.hero.skill.HeroSkill.*;
import de.pho.descent.shared.model.item.Item;
import static de.pho.descent.shared.model.item.Item.*;

/**
 *
 * @author pho
 */
public enum HeroTemplate {

    GRISBAN("Grisban the Thirsty", WARRIOR, BERSERKER, 3, 14, 4, GRAY, 5, 2, 3, 1, 6, "grisban", "All this killing is thirsty work. Drink with me!", "Each time you perform a rest action, you may immediately discard 1 Condition card from yourself.", "Use during your turn to perform 1 attack action. This is in addition to your 2 actions on your turn.", START_WARRIOR_BERSERKER, START_BERSERKER_WEAPON, null),
    SYNDRAEL("Syndrael", WARRIOR, KNIGHT, 4, 12, 4, GRAY, 4, 3, 2, 2, 6, "syndrael", "You swear undying loyalty, yet you are mortal. What can you know of commitment?", "If you have not moved during this turn, you recover 2 Fatigue at the end of your turn.", "Use during your turn to choose a hero within 3 spaces of you. You and that hero may each immediately perform a move action. This is in addition to the 2 actions each hero receives on his turn.", START_WARRIOR_KNIGHT, START_KNIGHT_WEAPON, START_KNIGHT_SHIELD),
    JAINFAIRWOOD("Jain Fairwood", SCOUT, WILDLANDER, 5, 8, 5, GRAY, 2, 3, 2, 4, 2, "jain_fairwood", "Out here in the wilds, I protect those who have misjudged me.", "When you suffer any amount of Health from an attack, you may choose to suffer some or all of that amount as Fatigue instead; you cannot suffer Fatigue in excess of your stamina.", "Action: You may move double your Speed and perform an attack. This attack may be performed before, after, or during this movement.", START_SCOUT_WILDLANDER, START_WILDLANDER_WEAPON, null),
    TOMBLEBURROWELL("Tomble Burrowell", SCOUT, THIEF, 4, 8, 5, GRAY, 1, 2, 3, 5, 2, "tomble_burrowell", "Don't think of this as you being robbed. Instead, think of it as you donating to a worthy cause.", "If you are attacked while adjacent to at least one other hero, you may choose an adjacent hero and add the defense pool of that hero to your own.", "Action: Remove your figure from the map and place a hero token in your space. At the start of your next turn, place your figure in any empty space within 4 spaces of your hero token.", START_SCOUT_THIEF, START_THIEF_WEAPON, START_THIEF_TRINKET),
    ASHRIAN("Ashrian", HEALER, SPIRITSPEAKER, 5, 10, 4, GRAY, 2, 2, 3, 4, 0, "ashrian", "I no more understand why I can hear the spirits speak than I understand why you cannot.", "When a minion monster begins its activation adjacent to you, it is Stunned.", "Action: Choose a monster within 3 spaces of you. Each monster in that monster group is Stunned.", START_HEALER_SPIRITSPEAKER, START_SPIRITSPEAKER_WEAPON, null),
    AVRICALBRIGHT("Avric Albright", HEALER, DISCIPLE, 4, 12, 4, GRAY, 2, 3, 4, 2, 0, "avric_albright", "I pledge myself to those in need. I will be their shield, their light in the darkness.", "Each hero within 3 spaces of you (including yourself) gains \"Surge: Recover 1 Health\" on all attack rolls.", "Action: Roll 2 red power dice. Each hero within 3 spaces of you (including yourself) may recover Health equal to the Health rolled.", START_HEALER_DISCIPLE, START_DISCIPLE_WEAPON, START_DISCIPLE_SHIELD),
    WIDOWTARHA("Widow Tarha", MAGE, NECROMANCER, 4, 10, 4, GRAY, 2, 4, 3, 2, 4, "widow_tarha", "I have nothing left to lose. Yet, there is so much more power for me to gain.", "Once per round, after you roll a dice for an attack, you may reroll 1 attack or power die. You must keep the new result.", "Action: Perform an attack. This attack affects 2 different monsters in your line of sight. 1 attack roll is made but each monster rolls defense dice separately. Both monsters are considered targets of your attack.", START_MAGE_NECROMANCER, START_NECROMANCER_WEAPON, null),
    LEORICOFTHEBOOK("Leoric of the Book", MAGE, RUNEMASTER, 4, 8, 5, GRAY, 1, 5, 2, 3, 4, "leoric", "If my years of study have taught me anything, it is that I am worthy of the knowledge I possess.", "Each monster within 3 spaces of you receives -1 damage on all attack rolls (to a minimum of 1).", "	Action: Perform an attack with a Magic weapon. This attack ignores range and targets each figure adjacent to you. 1 attack roll is made but each figure rolls defense dice separately.", START_MAGE_RUNEMASTER, START_RUNEMASTER_WEAPON, null);

    private final String name;

    private final int health;

    private final int speed;

    private final int stamina;

    private final DefenseDice defense;

    private final Archetype archetype;

    private final HeroClass heroClass;

    private final int might;

    private final int knowledge;

    private final int willpower;

    private final int awareness;
    
    private final int initiative;

    private final String imageName;

    private final String announce;

    private final String heroAbilityText;

    private final String heroicFeatText;

    private final HeroSkill startSkill;

    private final Item startItem1;

    private final Item startItem2;

    private HeroTemplate(String name, Archetype archetype, HeroClass heroClass, int speed, int health, int stamina, DefenseDice defense, int might, int knowledge, int willpower, int awareness, int initiative, String imageName, String announce, String heroAbilityText, String heroicFeatText, HeroSkill startSkill, Item startItem1, Item startItem2) {
        this.name = name;
        this.health = health;
        this.speed = speed;
        this.stamina = stamina;
        this.defense = defense;
        this.archetype = archetype;
        this.heroClass = heroClass;
        this.might = might;
        this.knowledge = knowledge;
        this.willpower = willpower;
        this.awareness = awareness;
        this.initiative = initiative;
        this.imageName = imageName;
        this.announce = announce;
        this.heroAbilityText = heroAbilityText;
        this.heroicFeatText = heroicFeatText;
        this.startSkill = startSkill;
        this.startItem1 = startItem1;
        this.startItem2 = startItem2;
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

    public Archetype getArchetype() {
        return archetype;
    }

    public HeroClass getHeroClass() {
        return heroClass;
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

    public int getInitiative() {
        return initiative;
    }

    public String getImageName() {
        return imageName;
    }

    public String getAnnounce() {
        return announce;
    }

    public String getHeroAbilityText() {
        return heroAbilityText;
    }

    public String getHeroicFeatText() {
        return heroicFeatText;
    }

    public HeroSkill getStartSkill() {
        return startSkill;
    }

    public Item getStartItem1() {
        return startItem1;
    }

    public Item getStartItem2() {
        return startItem2;
    }

//    @Override
//    public String toString() {
//        return name;
//    }
}
