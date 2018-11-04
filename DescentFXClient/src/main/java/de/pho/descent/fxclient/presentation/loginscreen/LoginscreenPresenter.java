package de.pho.descent.fxclient.presentation.loginscreen;

import static de.pho.descent.fxclient.MainApp.*;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.debug.Automate;
import de.pho.descent.fxclient.business.ws.PlayerClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.game.hero.HeroGameView;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.general.GameService;
import de.pho.descent.fxclient.presentation.startmenu.StartMenuView;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.model.Player;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javax.inject.Inject;
import org.controlsfx.control.Notifications;

/**
 *
 * @author pho
 */
public class LoginscreenPresenter implements Initializable {

    @FXML
    private TextField loginUserText;

    @FXML
    private TextField loginPwdText;

    @Inject
    private Credentials credentials;

    @Inject
    private GameDataModel gameDataModel;

    @Inject
    private GameService gameService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // return key on pwd field
        loginPwdText.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleLogin(e);
            }
        });
    }

    @FXML
    public void handleRegister(MouseEvent event) {
        if (loginUserText.getText() != null && !loginUserText.getText().isEmpty()
                && loginPwdText.getText() != null && !loginPwdText.getText().isEmpty()) {
            Player player = null;
            try {
                player = PlayerClient.registerPlayer(loginUserText.getText(), loginPwdText.getText());
            } catch (ServerException ex) {
                showError(ex);
            }
            if (player != null) {
                Notifications tmp = Notifications.create();
                tmp.darkStyle().position(Pos.TOP_RIGHT).text("Player " + loginUserText.getText() + " registered").showInformation();
            }
        }
    }

    @FXML
    public void handleLogin(MouseEvent event) {
        handleLogin((Event) event);
    }

    @FXML
    public void handleExit(MouseEvent event) {
        System.exit(0);
    }

    private void handleLogin(Event event) {
        if (loginUserText.getText().equals("debug")) {
            Object[] result = Automate.startCampaignWithThreeHeroes();
            credentials.setPlayer((Player) result[1]);
            gameDataModel.setCurrentCampaign((WsCampaign) result[2]);
            switchFullscreenScene(event, new HeroGameView());
            gameService.updateGameState((WsCampaign) result[2]);
        }
        if (loginUserText.getText() != null && !loginUserText.getText().isEmpty()
                && loginPwdText.getText() != null && !loginPwdText.getText().isEmpty()) {

            Player player = null;
            try {
                player = PlayerClient.loginPlayer(loginUserText.getText(), loginPwdText.getText());
            } catch (ServerException ex) {
                showError(ex);
            }
            if (player != null) {
                credentials.setPlayer(player);
                switchFullscreenScene(event, new StartMenuView());
            }
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
}
