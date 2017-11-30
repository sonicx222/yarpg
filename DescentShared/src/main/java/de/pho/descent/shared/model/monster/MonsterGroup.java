package de.pho.descent.shared.model.monster;

/**
 *
 * @author pho
 */
public enum MonsterGroup {
    ZOMBIE(2, 1, 3, 1, 4, 1),
    GOBLIN_ARCHER(2, 1, 3, 1, 4, 1),
    CAVE_SPIDER(2, 1, 3, 1, 4, 1),
    FLESH_MOULDER(1, 1, 2, 1, 3, 1),
    BARGHEST(1, 1, 2, 1, 3, 1),
    ETTIN(1, 0, 0, 1, 1, 1),
    ELEMENTAL(1, 0, 0, 1, 1, 1),
    MERRIOD(1, 0, 0, 1, 1, 1),
    SHADOW_DRAGON(1, 0, 0, 1, 1, 1);

    private final int normalCountTwoHero;
    private final int eliteCountTwoHero;
    private final int normalCountThreeHero;
    private final int eliteCountThreeHero;
    private final int normalCountFourHero;
    private final int eliteCountFourHero;

    private MonsterGroup(int normalCountTwoHero, int eliteCountTwoHero, int normalCountThreeHero, int eliteCountThreeHero, int normalCountFourHero, int eliteCountFourHero) {
        this.normalCountTwoHero = normalCountTwoHero;
        this.eliteCountTwoHero = eliteCountTwoHero;
        this.normalCountThreeHero = normalCountThreeHero;
        this.eliteCountThreeHero = eliteCountThreeHero;
        this.normalCountFourHero = normalCountFourHero;
        this.eliteCountFourHero = eliteCountFourHero;
    }

    public int getNormalCountTwoHero() {
        return normalCountTwoHero;
    }

    public int getEliteCountTwoHero() {
        return eliteCountTwoHero;
    }

    public int getNormalCountThreeHero() {
        return normalCountThreeHero;
    }

    public int getEliteCountThreeHero() {
        return eliteCountThreeHero;
    }

    public int getNormalCountFourHero() {
        return normalCountFourHero;
    }

    public int getEliteCountFourHero() {
        return eliteCountFourHero;
    }

}
