package de.pho.descent.fxclient.presentation.general;

import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsGameMap;
import de.pho.descent.shared.dto.WsHeroSelection;
import de.pho.descent.shared.dto.WsMessage;
import de.pho.descent.shared.dto.WsQuestEncounter;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javax.annotation.PostConstruct;

/**
 *
 * @author pho
 */
public class GameDataModel {

    private static final Logger LOGGER = Logger.getLogger(GameDataModel.class.getName());

    private final ObservableList<WsMessage> generalMessages = FXCollections.observableArrayList();
    private final ObservableList<WsMessage> campaignMessages = FXCollections.observableArrayList();
    private final ObservableList<WsCampaign> playableCampaigns = FXCollections.observableArrayList();
    private final ObservableList<WsHeroSelection> heroSelections = FXCollections.observableArrayList();

    private WsCampaign currentCampaign;

    private WsQuestEncounter currentQuestEncounter;

    private WsGameMap currentQuestMap;

    private LinearGradient buttonGradient;

    @PostConstruct
    public void init() {
        buttonGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.DARKVIOLET),
            new Stop(0.1, Color.BLACK),
            new Stop(0.9, Color.BLACK),
            new Stop(1, Color.DARKVIOLET)
        });
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

    public WsQuestEncounter getCurrentQuestEncounter() {
        return currentQuestEncounter;
    }

    public void setCurrentQuestEncounter(WsQuestEncounter currentQuestEncounter) {
        this.currentQuestEncounter = currentQuestEncounter;
    }

    public WsGameMap getCurrentQuestMap() {
        return currentQuestMap;
    }

    public void setCurrentQuestMap(WsGameMap currentQuestMap) {
        this.currentQuestMap = currentQuestMap;
    }

    public LinearGradient getButtonGradient() {
        return buttonGradient;
    }

    public void setButtonGradient(LinearGradient buttonGradient) {
        this.buttonGradient = buttonGradient;
    }

}
