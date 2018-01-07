package de.pho.descent.fxclient.presentation.game.prolog;

import static de.pho.descent.fxclient.MainApp.gameFont;
import de.pho.descent.fxclient.presentation.game.hero.HeroGameModel;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.general.GameService;
import de.pho.descent.shared.model.quest.QuestTemplate;
import java.net.URL;
import java.util.ResourceBundle;
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

    @FXML
    private Text prologText;

    @Inject
    private GameDataModel gameDataModel;

    @Inject
    private GameService gameService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prologText.setText(QuestTemplate.getTemplate(gameDataModel.getCurrentQuestEncounter().getQuest(),
                gameDataModel.getCurrentQuestEncounter().getPart()).getProlog());
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
