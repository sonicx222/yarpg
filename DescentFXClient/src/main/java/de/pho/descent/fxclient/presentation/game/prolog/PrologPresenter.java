package de.pho.descent.fxclient.presentation.game.prolog;

import static de.pho.descent.fxclient.MainApp.gameFont;
import static de.pho.descent.fxclient.business.config.Configuration.PROLOG_PROPERTIES_FILE;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.general.GameService;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class PrologPresenter implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(PrologPresenter.class.getName());

    @FXML
    private Text prologText;

    @Inject
    private GameDataModel gameDataModel;

    @Inject
    private GameService gameService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Properties prologTextByQuest = new Properties();
        InputStream is = PrologPresenter.class.getClassLoader().getResourceAsStream(PROLOG_PROPERTIES_FILE);
        try {
            prologTextByQuest.load(is);
        } catch (IOException ex) {
            LOGGER.severe("Unable to read prolog text from file");
        }
        prologText.setText(prologTextByQuest.getProperty(gameDataModel.getCurrentQuestEncounter().getQuest() + "_"
                + gameDataModel.getCurrentQuestEncounter().getPart()));
        prologText.setFont(gameFont);
    }

    @FXML
    public void handleOk(MouseEvent event) {
        gameDataModel.getPrologBoxVisible().set(false);
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
