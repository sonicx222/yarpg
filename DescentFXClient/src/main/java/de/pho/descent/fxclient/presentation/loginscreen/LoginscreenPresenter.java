package de.pho.descent.fxclient.presentation.loginscreen;

import static de.pho.descent.fxclient.MainApp.*;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.debug.Automate;
import de.pho.descent.fxclient.business.ws.PlayerClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.game.overlord.OverlordGameView;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javax.inject.Inject;
import org.controlsfx.control.Notifications;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.StyleClassValidationDecoration;

/**
 *
 * @author pho
 */
public class LoginscreenPresenter implements Initializable {

    @FXML
    private TextField loginUserText;

    @FXML
    private TextField loginPwdText;

    @FXML
    private StackPane paneRegister;

    @FXML
    private Rectangle itemRegister;

    @FXML
    private Text textRegister;

    @FXML
    private StackPane paneLogin;

    @FXML
    private Rectangle itemLogin;

    @FXML
    private Text textLogin;

    @FXML
    private StackPane paneExit;

    @FXML
    private Rectangle itemExit;

    @FXML
    private Text textExit;

    @Inject
    private Credentials credentials;

    @Inject
    private GameDataModel gameDataModel;

    private final ValidationSupport validationSupport = new ValidationSupport();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        validationSupport.setValidationDecorator(new StyleClassValidationDecoration());

        // return key on pwd field
        loginPwdText.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleLogin(e);
            }
        });

        // on Mouse entered
        paneRegister.setOnMouseEntered(event -> {
            itemRegister.setFill(gameDataModel.getButtonGradient());
            textRegister.setFill(Color.WHITE);
        });
        paneLogin.setOnMouseEntered(event -> {
            itemLogin.setFill(gameDataModel.getButtonGradient());
            textLogin.setFill(Color.WHITE);
        });
        paneExit.setOnMouseEntered(event -> {
            itemExit.setFill(gameDataModel.getButtonGradient());
            textExit.setFill(Color.WHITE);
        });

        // on Mouse exited
        paneRegister.setOnMouseExited(event -> {
            itemRegister.setFill(Color.BLACK);
            textRegister.setFill(Color.DARKGREY);
        });
        paneLogin.setOnMouseExited(event -> {
            itemLogin.setFill(Color.BLACK);
            textLogin.setFill(Color.DARKGREY);
        });
        paneExit.setOnMouseExited(event -> {
            itemExit.setFill(Color.BLACK);
            textExit.setFill(Color.DARKGREY);
        });

        // on Mouse pressed
        paneRegister.setOnMousePressed(event -> {
            itemRegister.setFill(Color.DARKVIOLET);
        });
        paneLogin.setOnMousePressed(event -> {
            itemLogin.setFill(Color.DARKVIOLET);
        });
        paneExit.setOnMousePressed(event -> {
            itemExit.setFill(Color.DARKVIOLET);
        });

        // on Mouse released
        paneRegister.setOnMouseReleased(event -> {
            itemRegister.setFill(gameDataModel.getButtonGradient());
        });
        paneLogin.setOnMouseReleased(event -> {
            itemLogin.setFill(gameDataModel.getButtonGradient());
        });
        paneExit.setOnMouseReleased(event -> {
            itemExit.setFill(gameDataModel.getButtonGradient());
        });

        // set font
//        textLogin.setFont(gameFont);
//        textRegister.setFont(gameFont);
    }

    @FXML
    public void handleRegister(MouseEvent event) {
        validationSupport.registerValidator(loginUserText, Validator.createEmptyValidator("Username is required"));
        validationSupport.registerValidator(loginPwdText, Validator.createEmptyValidator("Password is required"));

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
            Object[] result = Automate.startCampaignWithTwoHeroes();
            gameDataModel.setCurrentCampaign((WsCampaign) result[1]);
            credentials.setPlayer((Player) result[0]);
            switchFullscreenScene(event, new OverlordGameView());
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
}
