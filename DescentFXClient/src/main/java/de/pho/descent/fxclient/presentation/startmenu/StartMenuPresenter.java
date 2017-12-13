package de.pho.descent.fxclient.presentation.startmenu;

import static de.pho.descent.fxclient.MainApp.showError;
import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.debug.Automate;
import de.pho.descent.fxclient.business.ws.CampaignClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.campaignselection.CampaignSelectionView;
import de.pho.descent.fxclient.presentation.game.overlord.OverlordGameView;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.heroselection.HeroSelectionView;
import de.pho.descent.fxclient.presentation.loginscreen.LoginscreenView;
import de.pho.descent.shared.dto.WsCampaign;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javax.inject.Inject;
import org.controlsfx.control.Notifications;

/**
 *
 * @author pho
 */
public class StartMenuPresenter implements Initializable {

    @FXML
    private StackPane paneContinue;

    @FXML
    private Line lineContinue;

    @FXML
    private StackPane paneJoinCampaign;

    @FXML
    private Text textJoinCampaign;

    @Inject
    private Credentials credentials;

    @Inject
    private GameDataModel gameDataModel;

    private LinearGradient gradientMenuItem;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        paneContinue.setVisible(false);
        lineContinue.setVisible(false);

        // load playable campaigns
        updatePlayableCampaigns();

        // update menu
        if (gameDataModel.getPlayableCampaigns().isEmpty()) {
            paneJoinCampaign.setDisable(true);
            textJoinCampaign.setFill(Color.BLACK);
        } else {
            paneJoinCampaign.setDisable(false);
            textJoinCampaign.setFill(Color.DARKGRAY);
        }

        gradientMenuItem = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.DARKVIOLET),
            new Stop(0.1, Color.BLACK),
            new Stop(0.9, Color.BLACK),
            new Stop(1, Color.DARKVIOLET)
        });
    }

    @FXML
    public void handleOnKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            handleExit((Event) event);
        }
    }

    @FXML
    public void handleOnMouseEntered(MouseEvent event) {
        if (event.getSource() instanceof StackPane) {
            StackPane menuItem = (StackPane) event.getSource();

            menuItem.getChildren().forEach((node) -> {
                if (node instanceof Rectangle) {
                    ((Rectangle) node).setFill(gradientMenuItem);
                } else if (node instanceof Text) {
                    ((Text) node).setFill(Color.WHITE);
                }
            });
        }
    }

    @FXML
    public void handleOnMouseExited(MouseEvent event) {
        if (event.getSource() instanceof StackPane) {
            StackPane menuItem = (StackPane) event.getSource();

            menuItem.getChildren().forEach((node) -> {
                if (node instanceof Rectangle) {
                    ((Rectangle) node).setFill(Color.BLACK);
                } else if (node instanceof Text) {
                    ((Text) node).setFill(Color.DARKGRAY);
                }
            });
        }
    }

    @FXML
    public void handleOnMousePressed(MouseEvent event) {
        if (event.getSource() instanceof StackPane) {
            StackPane menuItem = (StackPane) event.getSource();

            menuItem.getChildren().forEach((node) -> {
                if (node instanceof Rectangle) {
                    ((Rectangle) node).setFill(Color.DARKVIOLET);
                }
            });
        }
    }

    @FXML
    public void handleOnMouseReleased(MouseEvent event) {
        if (event.getSource() instanceof StackPane) {
            StackPane menuItem = (StackPane) event.getSource();

            menuItem.getChildren().forEach((node) -> {
                if (node instanceof Rectangle) {
                    ((Rectangle) node).setFill(gradientMenuItem);
                }
            });
        }
    }

    @FXML
    public void handleNewCampaign(MouseEvent event) {

        if (credentials == null || !credentials.isValid()) {
            switchFullscreenScene(event, new LoginscreenView());
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
//                switchFullscreenScene(event, new HeroGameView());
                switchFullscreenScene(event, new HeroSelectionView());
            }
        }
    }

    @FXML
    public void handleJoinCampaign(MouseEvent event) {
        switchFullscreenScene(event, new CampaignSelectionView());
    }

    @FXML
    public void handleSettings(MouseEvent event) {
        gameDataModel.setCurrentCampaign(Automate.startCampaignWithTwoHeroes());
        switchFullscreenScene(event, new OverlordGameView());
    }

    @FXML
    public void handleLogout(MouseEvent event) {
        credentials.setPlayer(null);
        switchFullscreenScene(event, new LoginscreenView());
    }

    @FXML
    public void handleExit(MouseEvent event) {
        handleExit((Event) event);
    }

    private void handleExit(Event event) {
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
