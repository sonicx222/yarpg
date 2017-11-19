package de.pho.descent.shared.dto;

import de.pho.descent.shared.model.hero.HeroSelection;
import de.pho.descent.shared.model.hero.HeroTemplate;

/**
 *
 * @author pho
 */
public class WsHeroSelection {

    private long id;

    private long campaignId;

    private String username;

    private long userId;

    private HeroTemplate selectedHero;

    private boolean ready;

    public WsHeroSelection() {
    }

    public WsHeroSelection(String username, HeroTemplate selectedHero) {
        this.username = username;
        this.selectedHero = selectedHero;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(long campaignId) {
        this.campaignId = campaignId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public HeroTemplate getSelectedHero() {
        return selectedHero;
    }

    public void setSelectedHero(HeroTemplate selectedHero) {
        this.selectedHero = selectedHero;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    /**
     * Factory-Method to create new WsHeroSelection DTOs
     *
     * @param heroSelection the copy template for the DTO
     * @return the filled WsHeroSelection-DTO instance
     */
    public static WsHeroSelection createInstance(HeroSelection heroSelection) {
        WsHeroSelection wsHeroSelection = new WsHeroSelection();

        wsHeroSelection.setId(heroSelection.getId());
        wsHeroSelection.setCampaignId(heroSelection.getCampaign().getId());
        wsHeroSelection.setUsername(heroSelection.getPlayer().getUsername());
        wsHeroSelection.setUserId(heroSelection.getPlayer().getId());
        wsHeroSelection.setSelectedHero(heroSelection.getSelectedHero());
        wsHeroSelection.setReady(heroSelection.isReady());

        return wsHeroSelection;
    }

    @Override
    public String toString() {
        return "WsHeroSelection{" + "id=" + id + ", campaignId=" + campaignId + ", username=" + username + ", userId=" + userId + ", selectedHero=" + selectedHero + ", ready=" + ready + '}';
    }
}
