package de.pho.descent.fxclient.presentation.general;

import de.pho.descent.shared.model.campaign.Campaign;
import javax.annotation.PostConstruct;

/**
 *
 * @author pho
 */
public class GameDataModel {
    
    private Campaign campaign;
    
    @PostConstruct
    public void init() {

    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }
    
    
}
