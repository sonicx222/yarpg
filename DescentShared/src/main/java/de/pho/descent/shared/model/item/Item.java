package de.pho.descent.shared.model.item;

import de.pho.descent.shared.model.AttackType;
import static de.pho.descent.shared.model.item.ItemEffect.*;
import de.pho.descent.shared.model.SurgeAction;
import static de.pho.descent.shared.model.SurgeAction.*;
import de.pho.descent.shared.model.dice.AttackDice;
import static de.pho.descent.shared.model.dice.AttackDice.*;
import static de.pho.descent.shared.model.hero.Archetype.*;
import static de.pho.descent.shared.model.hero.HeroClass.*;
import static de.pho.descent.shared.model.item.ItemCategory.*;
import static de.pho.descent.shared.model.AttackType.*;
import static de.pho.descent.shared.model.item.Worn.*;
import static de.pho.descent.shared.model.item.Tier.*;
import de.pho.descent.shared.model.dice.DefenseDice;
import de.pho.descent.shared.model.hero.Archetype;
import de.pho.descent.shared.model.hero.HeroClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author pho
 */
public enum Item {

    START_KNIGHT_WEAPON("Iron Longsword", WARRIOR, KNIGHT, ZERO, Arrays.asList(BLUE, RED), null, null, Arrays.asList(TARGET_REROLL_ONE_DEFENSE_DICE), MELEE, BLADE, ONE_HAND, 0, 25, "warrior_knight_blade"),
    START_KNIGHT_SHIELD("Wooden Shield", WARRIOR, KNIGHT, ZERO, null, null, ADD_ONE_DEFENSE_AFTER_ROLL, null, null, SHIELD, ONE_HAND, 0, 25, "warrior_knight_shield"),
    START_BERSERKER_WEAPON("Chipped Greataxe", WARRIOR, BERSERKER, ZERO, Arrays.asList(BLUE, RED), null, null, Arrays.asList(ONE_DAMAGE, ONE_DAMAGE), MELEE, AXE, TWO_HAND, 0, 25, "warrior_berserker_axe"),
    START_DISCIPLE_WEAPON("Iron Mace", HEALER, DISCIPLE, ZERO, Arrays.asList(BLUE, YELLOW), null, null, Arrays.asList(STUN), MELEE, HAMMER, ONE_HAND, 0, 25, "healer_disciple_hammer"),
    START_DISCIPLE_SHIELD("Wooden Shield", HEALER, DISCIPLE, ZERO, null, null, ADD_ONE_DEFENSE_AFTER_ROLL, null, null, SHIELD, ONE_HAND, 0, 25, "warrior_knight_shield"),
    START_SPIRITSPEAKER_WEAPON("Oak Staff", HEALER, SPIRITSPEAKER, ZERO, Arrays.asList(BLUE, YELLOW), null, null, Arrays.asList(ONE_DAMAGE), RANGED_MELEE, STAFF, TWO_HAND, 0, 25, "healer_spiritspeaker_staff"),
    START_THIEF_WEAPON("Throwing Knives", SCOUT, THIEF, ZERO, Arrays.asList(BLUE, YELLOW), null, ADJACENT_ONE_DAMAGE, Arrays.asList(ONE_RANGE), RANGED, BLADE, ONE_HAND, 0, 25, "scout_thief_blade"),
    START_THIEF_TRINKET("Lucky Charm", SCOUT, THIEF, ZERO, null, null, REROLL_ATTRIBUTE_TEST, null, null, TRINKET, OTHER, 0, 25, "scout_thief_trinket"),
    START_WILDLANDER_WEAPON("Yew Shortbow", SCOUT, WILDLANDER, ZERO, Arrays.asList(BLUE, YELLOW), null, null, Arrays.asList(TWO_RANGE, ONE_DAMAGE), RANGED, BOW, TWO_HAND, 0, 25, "scout_wildlander_bow"),
    START_NECROMANCER_WEAPON("Reapers Scythe", MAGE, NECROMANCER, ZERO, Arrays.asList(BLUE, YELLOW), null, GAIN_ONE_HEALTH_PER_KILL, Arrays.asList(ONE_RANGE), RANGED, STAFF, TWO_HAND, 0, 25, "mage_necromancer_staff"),
    START_RUNEMASTER_WEAPON("Arcane Bolt", MAGE, RUNEMASTER, ZERO, Arrays.asList(BLUE, YELLOW), null, null, Arrays.asList(ONE_RANGE, TWO_PIERCE), RANGED, RUNE, TWO_HAND, 0, 25, "mage_runemaster_rune"),
    
    IRON_BATTLEAXE("Iron Battleaxe", null, null, Tier.ONE, Arrays.asList(BLUE, RED), null, ItemEffect.ONE_PIERCE, Arrays.asList(SurgeAction.ONE_DAMAGE, SurgeAction.ONE_PIERCE), MELEE, AXE, TWO_HAND, 100, 75, "act1_iron_battleaxe");

    private final String name;

    private final Archetype archetype;

    private final HeroClass heroClass;

    private final Tier itemTier;

    private final List<AttackDice> attackDice;

    private final List<DefenseDice> defenseDice;

    private final ItemEffect itemEffect;

    private final List<SurgeAction> surgeActions;

    private final AttackType attackType;

    private final ItemCategory trait;

    private final Worn worn;

    private final int value;

    private final int sell;

    private final String imagePath;

    private Item(String name, Archetype archetype, HeroClass heroClass, Tier itemTier, List<AttackDice> attackDice, List<DefenseDice> defenseDice, ItemEffect itemEffect, List<SurgeAction> surgeActions, AttackType range, ItemCategory trait, Worn worn, int value, int sell, String imagePath) {
        this.name = name;
        this.archetype = archetype;
        this.heroClass = heroClass;
        this.itemTier = itemTier;
        this.attackDice = attackDice;
        this.defenseDice = defenseDice;
        this.itemEffect = itemEffect;
        this.surgeActions = surgeActions;
        this.attackType = range;
        this.trait = trait;
        this.worn = worn;
        this.value = value;
        this.sell = sell;
        this.imagePath = imagePath;
    }

    public static List<Item> getStartItems(HeroClass heroClass) {
        List<Item> startItems = new ArrayList<>();
        for (Item item : Item.values()) {
            if ((item.getItemTier() == ZERO) && (item.getHeroClass() == heroClass)) {
                startItems.add(item);
            }
        }

        return startItems;
    }

    public String getName() {
        return name;
    }

    public Archetype getArchetype() {
        return archetype;
    }

    public HeroClass getHeroClass() {
        return heroClass;
    }

    public Tier getItemTier() {
        return itemTier;
    }

    public List<AttackDice> getAttackDice() {
        return attackDice;
    }

    public List<DefenseDice> getDefenseDice() {
        return defenseDice;
    }

    public ItemEffect getItemEffect() {
        return itemEffect;
    }

    public List<SurgeAction> getSurgeActions() {
        return surgeActions;
    }

    public AttackType getAttackType() {
        return attackType;
    }

    public ItemCategory getTrait() {
        return trait;
    }

    public Worn getWorn() {
        return worn;
    }

    public int getValue() {
        return value;
    }

    public int getSell() {
        return sell;
    }

    public String getImagePath() {
        return imagePath;
    }

}
