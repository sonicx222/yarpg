package de.pho.descent.shared.model.campaign;

import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.quest.QuestEncounter;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
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
    @NamedQuery(name = Campaign.findAll, query = "select c from Campaign c")
})
public class Campaign implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String findAll = "de.pho.descent.shared.model.Campaign.findAll";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date createdOn = new Date();

    @Enumerated(EnumType.STRING)
    private CampaignPhase phase;

    private Player overlord;

    @OneToOne
    private QuestEncounter activeQuest;

    @OneToMany
    @JoinColumn(name = "hero_id")
    private Set<GameHero> heroes;

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

    public Set<GameHero> getHeroes() {
        return heroes;
    }

    public void setHeroes(Set<GameHero> heroes) {
        this.heroes = heroes;
    }

    @Override
    public String toString() {
        return "de.pho.descent.shared.model.campaign.Campaign[ id=" + id + " ]";
    }

}
