package de.pho.descent.shared.dto;

import de.pho.descent.shared.model.PlaySide;
import de.pho.descent.shared.model.item.Item;

/**
 *
 * @author pho
 */
public class WsCheatBox {

    private long campaignId;
    private PlaySide winner;
    private long heroId;
    private Item item;

    public long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(long campaignId) {
        this.campaignId = campaignId;
    }

    public PlaySide getWinner() {
        return winner;
    }

    public void setWinner(PlaySide winner) {
        this.winner = winner;
    }

    public long getHeroId() {
        return heroId;
    }

    public void setHeroId(long heroId) {
        this.heroId = heroId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

}
