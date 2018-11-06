package de.pho.descent.fxclient.presentation.startmenu;

import static de.pho.descent.fxclient.MainApp.showError;
import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.CampaignClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.campaignselection.CampaignSelectionView;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.general.GameService;
import de.pho.descent.fxclient.presentation.heroselection.HeroSelectionView;
import de.pho.descent.fxclient.presentation.loginscreen.LoginscreenView;
import de.pho.descent.shared.dto.WsCampaign;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javax.inject.Inject;
import org.controlsfx.control.Notifications;

/**
 *
 * @author pho
 */
public class StartMenuPresenter implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(StartMenuPresenter.class.getName());

    @FXML
    private StackPane paneContinue;

    @FXML
    private Line lineContinue;

    @FXML
    private StackPane paneJoinCampaign;

    @FXML
    private Label labelJoinCampaign;

    @Inject
    private Credentials credentials;

    @Inject
    private GameDataModel gameDataModel;

    @Inject
    private GameService gameService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        paneContinue.setVisible(false);
        lineContinue.setVisible(false);

        // load playable campaigns
        updatePlayableCampaigns();

        // update menu
        if (gameDataModel.getPlayableCampaigns().isEmpty()) {
            paneJoinCampaign.setDisable(true);
            labelJoinCampaign.setTextFill(Color.BLACK);
        } else {
            paneJoinCampaign.setDisable(false);
            labelJoinCampaign.setTextFill(Color.DARKGRAY);
        }
    }

    @FXML
    public void handleOnKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            exit();
        }
    }

    @FXML
    public void handleOnMouseEntered(MouseEvent event) {
        gameService.handleOnMouseEntered(event);
    }

    @FXML
    public void handleOnMouseExited(MouseEvent event) {
        gameService.handleOnMouseExited(event);
    }

    @FXML
    public void handleOnMousePressed(MouseEvent event) {
        gameService.handleOnMousePressed(event);
    }

    @FXML
    public void handleOnMouseReleased(MouseEvent event) {
        gameService.handleOnMouseReleased(event);
    }

    @FXML
    public void handleNewCampaign(MouseEvent event) {

        if (credentials == null || !credentials.isValid()) {
            switchFullscreenScene(new LoginscreenView());
            Notifications.create()
                    .darkStyle().position(Pos.TOP_RIGHT)
                    .text("No credentials found. Please log in first")
                    .showInformation();

        } else {
            WsCampaign campaign = null;
            try {
                campaign = CampaignClient.newCampaign(credentials);
            } catch (ServerException ex) {
                showError(ex);
            }
            if (campaign != null) {
                gameDataModel.setCurrentCampaign(campaign);
                switchFullscreenScene(new HeroSelectionView());
            }
        }
    }

    @FXML
    public void handleJoinCampaign(MouseEvent event) {
        switchFullscreenScene(new CampaignSelectionView());
    }

    @FXML
    public void handleSettings(MouseEvent event) {
    }

    @FXML
    public void handleLogout(MouseEvent event) {
        credentials.setPlayer(null);
        switchFullscreenScene(new LoginscreenView());
    }

    @FXML
    public void handleExit(MouseEvent event) {
        exit();
    }

    private void exit() {
        System.exit(0);
    }

    private void updatePlayableCampaigns() {
        gameDataModel.getPlayableCampaigns().clear();

        List<WsCampaign> playableCampaigns = null;
        try {
            playableCampaigns = CampaignClient.getActiveCampaigns(credentials.getUsername(), credentials.getPassword());
        } catch (ServerException ex) {
            showError(ex);
        }

        if (playableCampaigns != null && !playableCampaigns.isEmpty()) {
            gameDataModel.getPlayableCampaigns().addAll(playableCampaigns);
        }
    }
}
