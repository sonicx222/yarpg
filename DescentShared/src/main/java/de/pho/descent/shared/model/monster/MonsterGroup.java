package de.pho.descent.shared.model.monster;

/**
 *
 * @author pho
 */
public enum MonsterGroup {
    ZOMBIE("Zombie", 2, 1, 3, 1, 4, 1),
    GOBLIN_ARCHER("Goblin Archer", 2, 1, 3, 1, 4, 1),
    CAVE_SPIDER("Cave Spider", 2, 1, 3, 1, 4, 1),
    FLESH_MOULDER("Flesh Moulder", 1, 1, 2, 1, 3, 1),
    BARGHEST("Barghest", 1, 1, 2, 1, 3, 1),
    ETTIN("Ettin", 1, 0, 0, 1, 1, 1),
    ELEMENTAL("Elemental", 1, 0, 0, 1, 1, 1),
    MERRIOD("Merriod", 1, 0, 0, 1, 1, 1),
    SHADOW_DRAGON("Shadow Dragon", 1, 0, 0, 1, 1, 1);

    private final String text;
    private final int normalCountTwoHero;
    private final int eliteCountTwoHero;
    private final int normalCountThreeHero;
    private final int eliteCountThreeHero;
    private final int normalCountFourHero;
    private final int eliteCountFourHero;

    private MonsterGroup(String text, int normalCountTwoHero, int eliteCountTwoHero, int normalCountThreeHero, int eliteCountThreeHero, int normalCountFourHero, int eliteCountFourHero) {
        this.text = text;
        this.normalCountTwoHero = normalCountTwoHero;
        this.eliteCountTwoHero = eliteCountTwoHero;
        this.normalCountThreeHero = normalCountThreeHero;
        this.eliteCountThreeHero = eliteCountThreeHero;
        this.normalCountFourHero = normalCountFourHero;
        this.eliteCountFourHero = eliteCountFourHero;
    }

    public int getNormalCount(int heroCount) {
        switch (heroCount) {
            case 2:
                return normalCountTwoHero;
            case 3:
                return normalCountThreeHero;
            default:
                return normalCountFourHero;
        }
    }

    public boolean hasElite(int heroCount) {
        switch (heroCount) {
            case 2:
                return eliteCountTwoHero > 0;
            case 3:
                return eliteCountThreeHero > 0;
            default:
                return eliteCountFourHero > 0;
        }
    }

    public String getText() {
        return text;
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
