package de.pho.descent.shared.model.campaign;

import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.quest.QuestEncounter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

    @ManyToOne
    private Player overlord;

    @OneToOne
    private QuestEncounter activeQuest;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "campaign_id")
    private List<GameHero> heroes = new ArrayList<>();

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

    public Player getOverlord() {
        return overlord;
    }

    public void setOverlord(Player overlord) {
        this.overlord = overlord;
    }

    public List<GameHero> getHeroes() {
        if (heroes == null) {
            heroes = new ArrayList<>();
        }
        return heroes;
    }

    public void setHeroes(List<GameHero> heroes) {
        this.heroes = heroes;
    }

    @Override
    public String toString() {
        return "de.pho.descent.shared.model.campaign.Campaign[ id=" + id + " ]";
    }

}
