package de.pho.descent.shared.dto;

import de.pho.descent.shared.model.hero.HeroSelection;
import de.pho.descent.shared.model.hero.HeroTemplate;

/**
 *
 * @author pho
 */
public class WsHeroSelection {

    private Long id;

    private Long campaignId;

    private String username;

    private Long userId;

    private HeroTemplate selectedHero;

    private boolean ready;

    public WsHeroSelection() {
    }

    public WsHeroSelection(String username, HeroTemplate selectedHero) {
        this.username = username;
        this.selectedHero = selectedHero;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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
