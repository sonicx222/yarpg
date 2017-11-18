package de.pho.descent.fxclient.presentation.startmenu;

import static de.pho.descent.fxclient.MainApp.showError;
import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.CampaignClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.game.GameView;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.loginscreen.LoginscreenView;
import de.pho.descent.shared.dto.WsCampaign;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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

    @Inject
    private Credentials credentials;

    @Inject
    private GameDataModel gameDataModel;

    private LinearGradient gradientMenuItem;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        paneContinue.setVisible(false);
        lineContinue.setVisible(false);

        gradientMenuItem = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.DARKVIOLET),
            new Stop(0.1, Color.BLACK),
            new Stop(0.9, Color.BLACK),
            new Stop(1, Color.DARKVIOLET)
        });
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
                    ((Text) node).setFill(Color.DARKGREY);
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
                gameDataModel.setCampaign(campaign);
                switchFullscreenScene(event, new GameView());
//                switchFullscreenScene(event, new HeroSelectionView());
            }
        }
    }

    @FXML
    public void handleSettings(MouseEvent event) {

    }

    @FXML
    public void handleLogout(MouseEvent event) {
        credentials.setPlayer(null);
        switchFullscreenScene(event, new LoginscreenView());
    }

    @FXML
    public void handleExit(MouseEvent event) {
        System.exit(0);
    }
}
