package de.pho.descent.shared.model.dice;

/**
 *
 * @author pho
 */
public enum AttackDiceResult {

    FIZZLE(0, 0, 0),
    BLUE2(2, 2, 1),
    BLUE3(3, 2, 0),
    BLUE4(4, 2, 0),
    BLUE5(5, 1, 0),
    BLUE6(6, 1, 1),
    RED1(0, 1, 0),
    RED2(0, 2, 0),
    RED3(0, 3, 0),
    RED4(0, 3, 1),
    YELLOW1(1, 0, 1),
    YELLOW2(1, 1, 0),
    YELLOW3(2, 1, 0),
    YELLOW4(0, 1, 1),
    YELLOW5(0, 2, 0),
    YELLOW6(0, 2, 1),
    GREEN1(0, 1, 0),
    GREEN2(0, 0, 1),
    GREEN3(1, 0, 1),
    GREEN4(1, 1, 0),
    GREEN5(0, 1, 1),
    GREEN6(1, 1, 1);

    private final int range;
    private final int damage;
    private final int surge;

    private AttackDiceResult(int range, int damage, int surge) {
        this.range = range;
        this.damage = damage;
        this.surge = surge;
    }

    public int getRange() {
        return range;
    }

    public int getDamage() {
        return damage;
    }

    public int getSurge() {
        return surge;
    }

}
