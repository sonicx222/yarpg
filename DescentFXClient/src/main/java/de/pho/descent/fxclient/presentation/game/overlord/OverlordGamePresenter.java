package de.pho.descent.fxclient.presentation.game.overlord;

import de.pho.descent.fxclient.presentation.game.hero.*;
import static de.pho.descent.fxclient.MainApp.showError;
import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.QuestClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.startmenu.StartMenuView;
import de.pho.descent.shared.dto.WsQuestEncounter;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class OverlordGamePresenter implements Initializable {

    @FXML
    private ScrollPane mapScrollPane;

    @Inject
    private Credentials credentials;

    @Inject
    private GameDataModel gameDataModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // temporary
        mapScrollPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                switchFullscreenScene(e, new StartMenuView());
            }
        });

        // load active quest encounter of current campaign
        if (gameDataModel.getCurrentQuestEncounter() == null) {
            loadQuestEncounter();
        }
    }

    private void loadQuestEncounter() {
        Objects.requireNonNull(gameDataModel.getCurrentCampaign());
        WsQuestEncounter wsQuestEncounter = null;
        try {
            wsQuestEncounter = QuestClient.getQuestEncounter(
                    credentials.getUsername(), credentials.getPassword(),
                    gameDataModel.getCurrentCampaign().getQuestEncounterId());
        } catch (ServerException ex) {
            showError(ex);
        }
        if (wsQuestEncounter != null) {
            gameDataModel.setCurrentQuestEncounter(wsQuestEncounter);
        }
    }

    @FXML
    public void handleBackNavigation(MouseEvent event) {

    }
}
