package de.pho.descent.shared.dto;

import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.campaign.CampaignPhase;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.overlord.Overlord;
import de.pho.descent.shared.model.quest.QuestTemplate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author pho
 */
public class WsCampaign {

    private long id;

    private CampaignPhase phase;

    private Overlord overlord;

    private QuestTemplate nextQuest;

    private long questEncounterId;

    private Date createdOn;

    private Date startedOn;

    private int countHeroSelections;

    private List<GameHero> gameHeroes = new ArrayList<>();

    public WsCampaign() {
    }

    public WsCampaign(Overlord overlord, CampaignPhase phase, QuestTemplate nextQuest, long questEncounterId) {
        this.phase = phase;
        this.overlord = overlord;
        this.nextQuest = nextQuest;
        this.questEncounterId = questEncounterId;
    }
    
    @XmlTransient
    public GameHero getActiveHero() {
        return gameHeroes.stream()
                .filter(hero -> hero.isActive()).findAny().orElse(null);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CampaignPhase getPhase() {
        return phase;
    }

    public void setPhase(CampaignPhase phase) {
        this.phase = phase;
    }

    public Overlord getOverlord() {
        return overlord;
    }

    public void setOverlord(Overlord overlord) {
        this.overlord = overlord;
    }

    public QuestTemplate getNextQuest() {
        return nextQuest;
    }

    public void setNextQuest(QuestTemplate nextQuest) {
        this.nextQuest = nextQuest;
    }

    public long getQuestEncounterId() {
        return questEncounterId;
    }

    public void setQuestEncounterId(long questEncounterId) {
        this.questEncounterId = questEncounterId;
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

    public int getCountHeroSelections() {
        return countHeroSelections;
    }

    public void setCountHeroSelections(int countHeroSelections) {
        this.countHeroSelections = countHeroSelections;
    }

    public List<GameHero> getGameHeroes() {
        return gameHeroes;
    }

    public void setGameHeroes(List<GameHero> gameHeroes) {
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
        wsCampaign.setOverlord(campaign.getOverlord());
        wsCampaign.setPhase(campaign.getPhase());
        wsCampaign.setNextQuest(campaign.getTemplateNextQuest());
        wsCampaign.setQuestEncounterId(campaign.getActiveQuest() == null ? 0 : campaign.getActiveQuest().getId());

        wsCampaign.setCreatedOn(campaign.getCreatedOn());
        wsCampaign.setStartedOn(campaign.getStartedOn());
        wsCampaign.setCountHeroSelections(campaign.getHeroSelections().size());
        wsCampaign.setGameHeroes(new ArrayList<>(campaign.getHeroes()));

        return wsCampaign;
    }

    @Override
    public String toString() {
        return "WsCampaign{" + "id=" + id + ", phase=" + phase + ", overlord="
                + overlord + ", nextQuest=" + nextQuest + ", questEncounterId="
                + questEncounterId + ", createdOn=" + createdOn + ", startedOn="
                + startedOn + ", countHeroSelections=" + countHeroSelections
                + ", gameHeroes=" + gameHeroes + '}';
    }
}
