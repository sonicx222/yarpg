package de.pho.descent.fxclient.presentation.game.overlord;

import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.general.GameService;
import de.pho.descent.fxclient.presentation.message.MessageService;
import de.pho.descent.fxclient.presentation.startmenu.StartMenuView;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class OverlordGamePresenter implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(OverlordGamePresenter.class.getName());

    @FXML
    private ScrollPane mapScrollPane;

    @Inject
    private Credentials credentials;

    @Inject
    private GameDataModel gameDataModel;

    @Inject
    private GameService gameService;

    @Inject
    private MessageService messageService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // load active quest encounter of current campaign
        if (gameDataModel.getCurrentQuestEncounter() == null) {
            gameService.loadQuestEncounter();
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
    public void handleRefresh(MouseEvent event) {
        LOGGER.info("OverlordGamePresenter: handleRefresh()");
        messageService.updateCampaignMessages();
    }

    @FXML
    public void handleNavigationBack(MouseEvent event) {
        LOGGER.info("OverlordGamePresenter: handleNavigationBack()");
        switchFullscreenScene(event, new StartMenuView());
    }

}
