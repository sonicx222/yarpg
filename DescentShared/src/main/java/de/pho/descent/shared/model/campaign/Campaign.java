package de.pho.descent.shared.model.campaign;

import de.pho.descent.shared.model.hero.HeroSelection;
import de.pho.descent.shared.model.overlord.Overlord;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.QuestTemplate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author pho
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Campaign.FINDACTIVE, query = "select c from Campaign as c "
            + "where c.phase <> de.pho.descent.shared.model.campaign.CampaignPhase.FINISHED")
})
public class Campaign implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String FINDACTIVE = "de.pho.descent.shared.model.Campaign.findActive";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private final Date createdOn = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date startedOn;

    @Enumerated(EnumType.STRING)
    private CampaignPhase phase;

    @Enumerated(EnumType.STRING)
    private QuestTemplate nextQuestTemplate;

    @OneToOne
    private Overlord overlord;

    @OneToOne(cascade = CascadeType.ALL)
    private QuestEncounter activeQuest;
    
    private int gold;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "campaign_id")
    private List<HeroSelection> heroSelections = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuestEncounter getActiveQuest() {
        return activeQuest;
    }

    public void setActiveQuest(QuestEncounter activeQuest) {
        this.activeQuest = activeQuest;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }
    
    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getStartedOn() {
        return startedOn;
    }

    public void setStartedOn(Date startedOn) {
        this.startedOn = startedOn;
    }

    public CampaignPhase getPhase() {
        return phase;
    }

    public void setPhase(CampaignPhase phase) {
        this.phase = phase;
    }

    public QuestTemplate getNextQuestTemplate() {
        return nextQuestTemplate;
    }

    public void setNextQuestTemplate(QuestTemplate nextQuestTemplate) {
        this.nextQuestTemplate = nextQuestTemplate;
    }

    public Overlord getOverlord() {
        return overlord;
    }

    public void setOverlord(Overlord overlord) {
        this.overlord = overlord;
    }

    public List<HeroSelection> getHeroSelections() {
        if (heroSelections == null) {
            heroSelections = new ArrayList<>();
        }
        return heroSelections;
    }

    public void setHeroSelections(List<HeroSelection> heroSelections) {
        this.heroSelections = heroSelections;
    }

    @Override
    public String toString() {
        return "de.pho.descent.shared.model.campaign.Campaign[ id=" + id + " ]";
    }

}
