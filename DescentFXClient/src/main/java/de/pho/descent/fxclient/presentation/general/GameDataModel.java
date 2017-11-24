package de.pho.descent.fxclient.presentation.general;

import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsHeroSelection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.annotation.PostConstruct;

/**
 *
 * @author pho
 */
public class GameDataModel {

    private final ObservableList<WsCampaign> playableCampaigns = FXCollections.observableArrayList();
    private final ObservableList<WsHeroSelection> heroSelections = FXCollections.observableArrayList();
    
    private WsCampaign currentCampaign;

    @PostConstruct
    public void init() {
    }

    public ObservableList<WsCampaign> getPlayableCampaigns() {
        return playableCampaigns;
    }

    public ObservableList<WsHeroSelection> getHeroSelections() {
        return heroSelections;
    }
    

    public WsCampaign getCurrentCampaign() {
        return currentCampaign;
    }

    public void setCurrentCampaign(WsCampaign currentCampaign) {
        this.currentCampaign = currentCampaign;
    }

}
