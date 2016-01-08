package de.pho.descent.web.model;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author pho
 */
@Entity
public class Campaign implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "gm_id")
    private GameMaster gameMaster;

    @OneToOne
    private Encounter activeEncounter;

    @OneToMany
    @JoinColumn(name = "hero_id")
    private Set<Hero> heroes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Encounter getActiveEncounter() {
        return activeEncounter;
    }

    public void setActiveEncounter(Encounter activeEncounter) {
        this.activeEncounter = activeEncounter;
    }



}
