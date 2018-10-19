package de.pho.descent.shared.dto;

import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.campaign.CampaignPhase;
import de.pho.descent.shared.model.overlord.Overlord;
import de.pho.descent.shared.model.quest.Quest;
import de.pho.descent.shared.model.quest.QuestPart;
import de.pho.descent.shared.model.quest.QuestTemplate;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author pho
 */
public class WsCampaign {

    private long id;

    private CampaignPhase phase;

    private Overlord overlord;

    private Quest currentQuest;

    private QuestPart currentPart;

    private QuestTemplate nextQuest;

    private long questEncounterId;

    private int gold;

    private Date createdOn;

    private Date startedOn;

    private int countHeroSelections;

    public WsCampaign() {
    }

    public WsCampaign(Overlord overlord, CampaignPhase phase, QuestTemplate nextQuest, long questEncounterId) {
        this.phase = phase;
        this.overlord = overlord;
        this.nextQuest = nextQuest;
        this.questEncounterId = questEncounterId;
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

    public Quest getCurrentQuest() {
        return currentQuest;
    }

    public void setCurrentQuest(Quest currentQuest) {
        this.currentQuest = currentQuest;
    }

    public QuestPart getCurrentPart() {
        return currentPart;
    }

    public void setCurrentPart(QuestPart currentPart) {
        this.currentPart = currentPart;
    }

    public QuestTemplate getNextQuest() {
        return nextQuest;
    }

    public void setNextQuest(QuestTemplate nextQuest) {
        this.nextQuest = nextQuest;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
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
        wsCampaign.setCurrentQuest(campaign.getActiveQuest() == null ? null : campaign.getActiveQuest().getQuest());
        wsCampaign.setCurrentPart(campaign.getActiveQuest() == null ? null : campaign.getActiveQuest().getPart());
        wsCampaign.setNextQuest(campaign.getNextQuestTemplate());
        wsCampaign.setQuestEncounterId(campaign.getActiveQuest() == null ? 0 : campaign.getActiveQuest().getId());

        wsCampaign.setCreatedOn(campaign.getCreatedOn());
        wsCampaign.setStartedOn(campaign.getStartedOn());
        wsCampaign.setCountHeroSelections(campaign.getHeroSelections().size());
        wsCampaign.setGold(campaign.getGold());

        return wsCampaign;
    }

    @Override
    public String toString() {
        return "WsCampaign{" + "id=" + id + ", phase=" + phase + ", overlord="
                + overlord + ", nextQuest=" + nextQuest + ", questEncounterId="
                + questEncounterId + ", createdOn=" + createdOn + ", startedOn="
                + startedOn + ", countHeroSelections=" + countHeroSelections + '}';
    }
}
