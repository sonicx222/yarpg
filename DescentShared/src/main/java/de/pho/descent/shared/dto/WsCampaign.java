package de.pho.descent.shared.dto;

import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.campaign.CampaignPhase;
import de.pho.descent.shared.model.hero.GameHero;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pho
 */
public class WsCampaign {

    private Long id;

    private CampaignPhase phase;

    private WsPlayer overlord;

    private WsQuestEncounter activeQuest;

    private List<String> gameHeroes = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CampaignPhase getPhase() {
        return phase;
    }

    public void setPhase(CampaignPhase phase) {
        this.phase = phase;
    }

    public WsPlayer getOverlord() {
        return overlord;
    }

    public void setOverlord(WsPlayer overlord) {
        this.overlord = overlord;
    }

    public WsQuestEncounter getActiveQuest() {
        return activeQuest;
    }

    public void setActiveQuest(WsQuestEncounter activeQuest) {
        this.activeQuest = activeQuest;
    }

    public List<String> getGameHeroes() {
        return gameHeroes;
    }

    public void setGameHeroes(List<String> gameHeroes) {
        this.gameHeroes = gameHeroes;
    }

    /**
     * Factory-Method to create new WsCampaign DTOs
     *
     * @param campaign the copy template for the DTO
     * @return the filled WsCampaign-DTO instance
     */
    public static WsCampaign createInstance(Campaign campaign) {
        WsCampaign wsCampaign = new WsCampaign();

        wsCampaign.setId(campaign.getId());
        wsCampaign.setActiveQuest(
                WsQuestEncounter.createInstance(campaign.getActiveQuest()));
        wsCampaign.setOverlord(WsPlayer.createInstance(campaign.getOverlord()));
        wsCampaign.setPhase(campaign.getPhase());

        List<String> heroNames = new ArrayList<>();
        for (GameHero hero : campaign.getHeroes()) {
            heroNames.add(hero.getName());
        }

        wsCampaign.setGameHeroes(heroNames);

        return wsCampaign;
    }

    @Override
    public String toString() {
        return "WsCampaign{" + "id=" + id + ", phase=" + phase + ", overlord=" + overlord + ", activeQuest=" + activeQuest + ", gameHeroes=" + gameHeroes + '}';
    }
    
    
}
