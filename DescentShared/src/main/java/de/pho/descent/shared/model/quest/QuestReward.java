package de.pho.descent.shared.model.quest;

import de.pho.descent.shared.model.item.Item;

/**
 *
 * @author pho
 */
public class QuestReward {

    private int gold;
    private int xp;
    private Item item;

    public QuestReward() {
    }

    public QuestReward(int gold, int xp, Item item) {
        this.gold = gold;
        this.xp = xp;
        this.item = item;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
