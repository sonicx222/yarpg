package de.pho.descent.shared.model.monster;

import static de.pho.descent.shared.model.dice.AttackDice.*;
import de.pho.descent.shared.model.SurgeAction;
import static de.pho.descent.shared.model.SurgeAction.*;
import de.pho.descent.shared.model.dice.AttackDice;
import de.pho.descent.shared.model.dice.DefenseDice;
import static de.pho.descent.shared.model.dice.DefenseDice.*;
import static de.pho.descent.shared.model.monster.MonsterGroup.*;
import de.pho.descent.shared.model.monster.ability.MonsterAbility;
import static de.pho.descent.shared.model.monster.ability.MonsterAbility.*;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author pho
 */
public enum MonsterTemplate {

    ZOMBIE_NORMAL_ACT1(ZOMBIE, 3, 3, Arrays.asList(BLUE, YELLOW), Arrays.asList(BROWN), Arrays.asList(SHAMBLING, DISEASE), Arrays.asList(ONE_DAMAGE), 1, "zombie"),
    ZOMBIE_ELITE_ACT1(ZOMBIE, 3, 6, Arrays.asList(BLUE, YELLOW), Arrays.asList(BROWN), Arrays.asList(SHAMBLING, DISEASE, GRAB), Arrays.asList(ONE_DAMAGE), 1, "zombie_elite"),
    ZOMBIE_NORMAL_ACT2(ZOMBIE, 3, 5, Arrays.asList(BLUE, YELLOW), Arrays.asList(BROWN), Arrays.asList(SHAMBLING, DISEASE), Arrays.asList(TWO_DAMAGE), 1, "zombie"),
    ZOMBIE_ELITE_ACT2(ZOMBIE, 3, 9, Arrays.asList(BLUE, YELLOW, RED), Arrays.asList(BROWN), Arrays.asList(SHAMBLING, DISEASE, GRAB), Arrays.asList(TWO_DAMAGE), 1, "zombie_elite"),
    GOBLIN_ARCHER_NORMAL_ACT1(GOBLIN_ARCHER, 5, 2, Arrays.asList(BLUE, YELLOW), Arrays.asList(GRAY), Arrays.asList(SCAMPER, COWARDLY), Arrays.asList(ONE_RANGE, ONE_DAMAGE), 1, "goblin"),
    GOBLIN_ARCHER_ELITE_ACT1(GOBLIN_ARCHER, 5, 4, Arrays.asList(BLUE, YELLOW), Arrays.asList(GRAY), Arrays.asList(SCAMPER), Arrays.asList(TWO_RANGE, TWO_DAMAGE), 1, "goblin_elite"),
    GOBLIN_ARCHER_NORMAL_ACT2(GOBLIN_ARCHER, 5, 4, Arrays.asList(BLUE, YELLOW), Arrays.asList(GRAY), Arrays.asList(SCAMPER, COWARDLY), Arrays.asList(TWO_RANGE, TWO_DAMAGE), 1, "goblin"),
    GOBLIN_ARCHER_ELITE_ACT2(GOBLIN_ARCHER, 5, 6, Arrays.asList(BLUE, YELLOW, YELLOW), Arrays.asList(GRAY), Arrays.asList(SCAMPER), Arrays.asList(THREE_RANGE, TWO_DAMAGE), 1, "goblin_elite"),
    //    CAVE_SPIDER_NORMAL_ACT1("Cave Spider"),
    //    CAVE_SPIDER_ELITE_ACT1("Cave Spider"),
    //    CAVE_SPIDER_NORMAL_ACT2("Cave Spider"),
    //    CAVE_SPIDER_ELITE_ACT2("Cave Spider"),
    //    FLESH_MOULDER_NORMAL_ACT1("Flesh Moulder"),
    //    FLESH_MOULDER_ELITE_ACT1("Flesh Moulder"),
    //    FLESH_MOULDER_NORMAL_ACT2("Flesh Moulder"),
    //    FLESH_MOULDER_ELITE_ACT2("Flesh Moulder"),
    //    BARGHEST_NORMAL_ACT1("Barghest"),
    //    BARGHEST_ELITE_ACT1("Barghest"),
    //    BARGHEST_NORMAL_ACT2("Barghest"),
    //    BARGHEST_ELITE_ACT2("Barghest"),
    ETTIN_NORMAL_ACT1(ETTIN, 3, 5, Arrays.asList(BLUE, RED), Arrays.asList(GRAY, GRAY), Arrays.asList(REACH), Arrays.asList(TWO_DAMAGE), 2, "ettin"),
    ETTIN_ELITE_ACT1(ETTIN, 3, 8, Arrays.asList(BLUE, RED), Arrays.asList(GRAY, GRAY), Arrays.asList(REACH, THROW), Arrays.asList(THREE_DAMAGE), 2, "ettin_elite"),
    ETTIN_NORMAL_ACT2(ETTIN, 3, 7, Arrays.asList(BLUE, RED, RED), Arrays.asList(BLACK, GRAY), Arrays.asList(REACH), Arrays.asList(ONE_DAMAGE), 2, "ettin"),
    ETTIN_ELITE_ACT2(ETTIN, 3, 9, Arrays.asList(BLUE, RED, RED), Arrays.asList(BLACK, GRAY), Arrays.asList(REACH, THROW), Arrays.asList(TWO_DAMAGE), 2, "ettin_elite");
//    ELEMENTAL_NORMAL_ACT1("Elemental"),
//    ELEMENTAL_ELITE_ACT1("Elemental"),
//    ELEMENTAL_NORMAL_ACT2("Elemental"),
//    ELEMENTAL_ELITE_ACT2("Elemental"),
//    MERRIOD_NORMAL_ACT1("Merriod"),
//    MERRIOD_ELITE_ACT1("Merriod"),
//    MERRIOD_NORMAL_ACT2("Merriod"),
//    MERRIOD_ELITE_ACT2("Merriod"),
//    SHADOW_DRAGON_NORMAL_ACT1("Shadow Dragon"),
//    SHADOW_DRAGON_ELITE_ACT1("Shadow Dragon"),
//    SHADOW_DRAGON_NORMAL_ACT2("Shadow Dragon"),
//    SHADOW_DRAGON_ELITE_ACT2("Shadow Dragon");

    private final MonsterGroup group;

    private final int health;

    private final int speed;

    private final List<AttackDice> attack;

    private final List<DefenseDice> defense;

    private final List<MonsterAbility> abilities;

    private final List<SurgeAction> surgeActions;

    private final int fieldSize;

    private final String imageName;

    private MonsterTemplate(MonsterGroup group, int speed, int health, List<AttackDice> attack, List<DefenseDice> defense, List<MonsterAbility> abilities, List<SurgeAction> surgeActions, int fieldSize, String imagePath) {
        this.group = group;
        this.health = health;
        this.speed = speed;
        this.attack = attack;
        this.defense = defense;
        this.abilities = abilities;
        this.surgeActions = surgeActions;
        this.fieldSize = fieldSize;
        this.imageName = imagePath;
    }

    public MonsterGroup getGroup() {
        return group;
    }

    public int getHealth() {
        return health;
    }

    public int getSpeed() {
        return speed;
    }

    public List<AttackDice> getAttack() {
        return attack;
    }

    public List<DefenseDice> getDefense() {
        return defense;
    }

    public List<MonsterAbility> getAbilities() {
        return abilities;
    }

    public List<SurgeAction> getSurgeActions() {
        return surgeActions;
    }

    public int getFieldSize() {
        return fieldSize;
    }

    public String getImageName() {
        return imageName;
    }
}
