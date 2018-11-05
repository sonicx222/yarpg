package de.pho.descent.fxclient.presentation.game.epilog;

import static de.pho.descent.fxclient.MainApp.showError;
import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.QuestClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.campaignselection.CampaignSelectionView;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.general.GameService;
import de.pho.descent.shared.auth.SecurityTools;
import de.pho.descent.shared.dto.WsQuestEncounter;
import de.pho.descent.shared.model.PlaySide;
import de.pho.descent.shared.model.quest.LootBox;
import de.pho.descent.shared.model.quest.QuestReward;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class EpilogPresenter implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(EpilogPresenter.class.getName());

    private static final String WIN_TEXT_HEROES = "The heroes win!";
    private static final String WIN_TEXT_OVERLORD = "The overlord wins!";
    private ResourceBundle resources = null;

    @Inject
    private Credentials credentials;
    @Inject
    private GameDataModel gameDataModel;
    @Inject
    private GameService gameService;

    @FXML
    private Label questLabel;

    @FXML
    private Label winnerLabel;

    @FXML
    private Text epilogText;

    @FXML
    private Text rewardText;

    @FXML
    private VBox boxContinue;

    private LootBox box = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        getQuestReward();
        setupScreen();
    }

    @FXML
    public void handleNavigationBack(MouseEvent event) {
        LOGGER.info("EpilogPresenter: handleNavigationBack()");
        switchFullscreenScene(event, new CampaignSelectionView());
    }

    @FXML
    public void handleContinue(MouseEvent event) {
        LOGGER.info("EpilogPresenter: handleContinue()");
        switchFullscreenScene(event, new CampaignSelectionView());
    }

    private void getQuestReward() {
        try {
            box = QuestClient.getQuestReward(credentials.getUsername(),
                    credentials.getPassword(),
                    gameDataModel.getCurrentQuestEncounter().getId());
        } catch (ServerException ex) {
            showError(ex);
        }
    }

    private void setupScreen() {
        if (box != null) {
            WsQuestEncounter questEnc = gameDataModel.getCurrentQuestEncounter();
            questLabel.setText("Quest " + questEnc.getQuest());
            String epilogTextKey = questEnc.getQuest() + "_" + questEnc.getPart() + "_";
            QuestReward reward = null;

            if (gameDataModel.getCurrentQuestEncounter().getWinner() == PlaySide.HEROES) {
                winnerLabel.setText(WIN_TEXT_HEROES);
                epilogText.setText(resources.getString(epilogTextKey + "HEROES"));
                reward = box.getRewardBySide(PlaySide.HEROES);
            } else {
                winnerLabel.setText(WIN_TEXT_OVERLORD);
                epilogText.setText(resources.getString(epilogTextKey + "OVERLORD"));
                reward = box.getRewardBySide(PlaySide.OVERLORD);
            }
            rewardText.setText(rewardToText(reward));
        }

        if (Objects.equals(credentials.getUsername(), gameDataModel.getCurrentCampaign().getOverlord().getPlayedBy().getUsername())) {
            boxContinue.setVisible(true);
        } else {
            boxContinue.setVisible(false);
        }
    }

    private String rewardToText(QuestReward reward) {
        StringBuilder sb = null;

        if (reward == null) {
            sb = new StringBuilder("No rewards!");
        } else {
            sb = new StringBuilder("Rewards: ");
            sb.append(System.lineSeparator());

            if (reward.getGold() > 0) {
                sb.append("Gold: ").append(reward.getGold());
            }
            if (reward.getXp() > 0) {
                sb.append(System.lineSeparator());
                sb.append("Experience: ").append(reward.getXp());
            }
            if (reward.getItem() != null) {
                sb.append(System.lineSeparator());
                sb.append("Item: ").append(reward.getItem().getName());
            }
        }

        return sb.toString();
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
