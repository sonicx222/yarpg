package de.pho.descent.shared.model.quest;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.pho.descent.shared.model.map.GameMap;
import de.pho.descent.shared.model.PlaySide;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.shared.model.monster.MonsterGroup;
import de.pho.descent.shared.model.token.Token;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

/**
 *
 * @author pho
 */
@Entity
@NamedQueries({
    @NamedQuery(name = QuestEncounter.findByQuestAndPart, query = "select distinct qe from QuestEncounter as qe "
            + "where qe.quest=:" + QuestEncounter.paramQuest + " "
            + "and qe.part=:" + QuestEncounter.paramPart)
})
public class QuestEncounter implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String findByQuestAndPart = "de.pho.descent.shared.model.quest.QuestEncounter.findByQuestAndPart";

    public static final String paramQuest = "questParam";
    public static final String paramPart = "questPartParam";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Quest quest;

    @Enumerated(EnumType.STRING)
    private QuestPart part;

    @OneToOne(optional = true)
    @JoinColumn(name = "campaign_id", nullable = true)
    @JsonBackReference(value = "active_campaign_quest")
    private Campaign campaign;

    private QuestPhase phase;

    private boolean active;

    private int round;

    private int trigger;

    @Enumerated(EnumType.STRING)
    private PlaySide currentTurn;

    @Enumerated(EnumType.STRING)
    private PlaySide winner;

    @OneToOne(cascade = CascadeType.ALL)
    private GameMap map;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "encounter_id")
    private List<GameHero> heroes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "encounter_id")
    private List<GameMonster> monsters = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "encounter_id")
    private List<Token> token = new ArrayList<>();

    public GameHero getActiveHero() {
        return heroes.stream()
                .filter(hero -> hero.isActive()).findAny().orElse(null);
    }

    public GameMonster getActiveMonster() {
        return monsters.stream()
                .filter(monster -> monster.isActive()).findAny().orElse(null);
    }

    public List<GameMonster> getMonsterGroup(MonsterGroup group) {
        return monsters.stream()
                .filter(monster -> (monster.getMonsterTemplate().getGroup() == group))
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    public QuestPart getPart() {
        return part;
    }

    public void setPart(QuestPart part) {
        this.part = part;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public QuestPhase getPhase() {
        return phase;
    }

    public void setPhase(QuestPhase phase) {
        this.phase = phase;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getTrigger() {
        return trigger;
    }

    public void setTrigger(int trigger) {
        this.trigger = trigger;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public PlaySide getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(PlaySide currentTurn) {
        this.currentTurn = currentTurn;
    }

    public PlaySide getWinner() {
        return winner;
    }

    public void setWinner(PlaySide winner) {
        this.winner = winner;
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public List<GameHero> getHeroes() {
        return heroes;
    }

    public void setHeroes(List<GameHero> heroes) {
        this.heroes = heroes;
    }

    public List<GameMonster> getMonsters() {
        return monsters;
    }

    public void setMonsters(List<GameMonster> monsters) {
        this.monsters = monsters;
    }

    public List<Token> getToken() {
        return token;
    }

    public void setToken(List<Token> token) {
        this.token = token;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof QuestEncounter)) {
            return false;
        }
        QuestEncounter other = (QuestEncounter) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "de.pho.descent.shared.model.QuestEncounter[ id=" + id + " ]";
    }
}
