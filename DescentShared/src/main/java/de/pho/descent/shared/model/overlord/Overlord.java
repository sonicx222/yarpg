package de.pho.descent.shared.model.overlord;

import de.pho.descent.shared.model.Player;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author pho
 */
@Entity
public class Overlord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Player playedBy;

    @Enumerated(EnumType.STRING)
    private OverlordClass overlordClass;
    
    private int xp;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastMessageUpdate;

    public Overlord() {
    }

    public Overlord(Player playedBy, OverlordClass overlordClass, Date lastMessageUpdate) {
        this.playedBy = playedBy;
        this.overlordClass = overlordClass;
        this.lastMessageUpdate = lastMessageUpdate;
    }
    
    public Long getId() {
        return id;
    }

    public Player getPlayedBy() {
        return playedBy;
    }

    public void setPlayedBy(Player playedBy) {
        this.playedBy = playedBy;
    }

    public OverlordClass getOverlordClass() {
        return overlordClass;
    }

    public void setOverlordClass(OverlordClass overlordClass) {
        this.overlordClass = overlordClass;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }
    
    public void addXp(int amount) {
        this.xp = this.xp + amount;
    }
    

    public Date getLastMessageUpdate() {
        return lastMessageUpdate;
    }

    public void setLastMessageUpdate(Date lastMessageUpdate) {
        this.lastMessageUpdate = lastMessageUpdate;
    }
    
    
}
