package de.pho.descent.shared.model.monster;

import static de.pho.descent.shared.model.monster.MonsterGroup.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author pho
 */
public enum MonsterTrait {
    CIVILIZED(FLESH_MOULDER),
    DARK(BARGHEST, SHADOW_DRAGON),
    BUILDING(GOBLIN_ARCHER, ZOMBIE),
    MOUNTAIN(ETTIN),
    CURSED(FLESH_MOULDER, ZOMBIE),
    COLD(ELEMENTAL),
    HOT(ELEMENTAL),
    WATER(MERRIOD),
    WILDERNESS(BARGHEST, CAVE_SPIDER, MERRIOD),
    CAVE(CAVE_SPIDER, ETTIN, GOBLIN_ARCHER, SHADOW_DRAGON);

    private final List<MonsterGroup> monsterGroups;

    private MonsterTrait(MonsterGroup... monsterGroups) {
        this.monsterGroups = Collections.unmodifiableList(Arrays.asList(monsterGroups));
    }

    public List<MonsterGroup> getMonsterGroups() {
        return monsterGroups;
    }

}
