package de.pho.descent.shared.model.quest;

import de.pho.descent.shared.model.GameMap;
import de.pho.descent.shared.model.PlaySide;
import de.pho.descent.shared.model.hero.GameHero;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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

//    @Column(length = 4000)
//    private String prolog;
// 
//    @Column(length = 4000)
//    private String epilog;
    private boolean isActive;

    @OneToOne
    private GameHero activeHero;

    @Enumerated(EnumType.STRING)
    private PlaySide winner;

    @OneToOne
    private GameMap map;

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

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public GameHero getActiveHero() {
        return activeHero;
    }

    public void setActiveHero(GameHero activeHero) {
        this.activeHero = activeHero;
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
