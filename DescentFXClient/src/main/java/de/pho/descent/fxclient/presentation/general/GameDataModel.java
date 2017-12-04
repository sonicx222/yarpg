package de.pho.descent.fxclient.presentation.general;

import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsHeroSelection;
import de.pho.descent.shared.dto.WsMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.annotation.PostConstruct;

/**
 *
 * @author pho
 */
public class GameDataModel {

    private final ObservableList<WsMessage> generalMessages = FXCollections.observableArrayList();
    private final ObservableList<WsMessage> campaignMessages = FXCollections.observableArrayList();
    private final ObservableList<WsCampaign> playableCampaigns = FXCollections.observableArrayList();
    private final ObservableList<WsHeroSelection> heroSelections = FXCollections.observableArrayList();

    private WsCampaign currentCampaign;

    @PostConstruct
    public void init() {
    }

    public ObservableList<WsMessage> getGeneralMessages() {
        return generalMessages;
    }

    public ObservableList<WsMessage> getCampaignMessages() {
        return campaignMessages;
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
