package de.pho.descent.shared.model.hero;

import de.pho.descent.shared.model.Player;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author pho
 */
@Entity
public class HeroSelection implements Serializable {

    private static final long serialVersionUID = 5L;
    public static final String findByCampaign = "de.pho.descent.shared.model.hero.HeroSelection.findByCampaign";
    public static final String paramCampaignId = "campaignIdParam";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @ManyToOne()
////    @OnDelete(action = OnDeleteAction.CASCADE)
//    private Campaign campaign;
    @ManyToOne
    private Player player;

    @Enumerated(EnumType.STRING)
    private HeroTemplate selectedHero;

    private boolean ready;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public Campaign getCampaign() {
//        return campaign;
//    }
//
//    public void setCampaign(Campaign campaign) {
//        this.campaign = campaign;
//    }
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public HeroTemplate getSelectedHero() {
        return selectedHero;
    }

    public void setSelectedHero(HeroTemplate selectedHero) {
        this.selectedHero = selectedHero;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

}
