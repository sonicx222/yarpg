package de.pho.descent.shared.dto;

import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.campaign.CampaignPhase;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.quest.Quest;
import de.pho.descent.shared.model.quest.QuestPart;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author pho
 */
public class WsCampaign {

    private Long id;

    private CampaignPhase phase;

    private String overlord;

    private Quest activeQuest;

    private QuestPart part;

    private Date createdOn;

    private Date startedOn;

    private List<String> gameHeroes = new ArrayList<>();

    public WsCampaign() {
    }

    public WsCampaign(String overlord, CampaignPhase phase, Quest quest, QuestPart part) {
        this.phase = phase;
        this.overlord = overlord;
        this.activeQuest = quest;
        this.part = part;
    }

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

    public String getOverlord() {
        return overlord;
    }

    public void setOverlord(String overlord) {
        this.overlord = overlord;
    }

    public Quest getActiveQuest() {
        return activeQuest;
    }

    public void setActiveQuest(Quest activeQuest) {
        this.activeQuest = activeQuest;
    }

    public QuestPart getPart() {
        return part;
    }

    public void setPart(QuestPart part) {
        this.part = part;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getStartedOn() {
        return startedOn;
    }

    public void setStartedOn(Date startedOn) {
        this.startedOn = startedOn;
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
        Objects.requireNonNull(campaign);
        WsCampaign wsCampaign = new WsCampaign();

        wsCampaign.setId(campaign.getId());
        wsCampaign.setActiveQuest(
                campaign.getActiveQuest() == null ? null : campaign.getActiveQuest().getQuest());
        wsCampaign.setPart(
                campaign.getActiveQuest() == null ? null : campaign.getActiveQuest().getPart());
        wsCampaign.setOverlord(
                campaign.getOverlord() == null ? null : campaign.getOverlord().getUsername());
        wsCampaign.setPhase(campaign.getPhase());
        wsCampaign.setCreatedOn(campaign.getCreatedOn());
        wsCampaign.setStartedOn(campaign.getStartedOn());

        List<String> heroNames = new ArrayList<>();
        for (GameHero hero : campaign.getHeroes()) {
            heroNames.add(hero.getName());
        }
        wsCampaign.setGameHeroes(heroNames);

        return wsCampaign;
    }

    @Override
    public String toString() {
        return "WsCampaign{" + "id=" + id + ", phase=" + phase + ", overlord=" + overlord + ", activeQuest=" + activeQuest + ", part=" + part + ", gameHeroes=" + gameHeroes + '}';
    }
}
