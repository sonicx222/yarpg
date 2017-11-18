package de.pho.descent.fxclient.presentation.general;

import de.pho.descent.shared.dto.WsCampaign;
import javax.annotation.PostConstruct;

/**
 *
 * @author pho
 */
public class GameDataModel {

    private WsCampaign campaign;

    @PostConstruct
    public void init() {

    }

    public WsCampaign getCampaign() {
        return campaign;
    }

    public void setCampaign(WsCampaign campaign) {
        this.campaign = campaign;
    }

}
