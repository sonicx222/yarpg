package de.pho.descent.fxclient.presentation.game.hero;

import static de.pho.descent.fxclient.MainApp.showError;
import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.MessageClient;
import de.pho.descent.fxclient.business.ws.QuestClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.startmenu.StartMenuView;
import de.pho.descent.shared.dto.WsMessage;
import de.pho.descent.shared.dto.WsQuestEncounter;
import de.pho.descent.shared.model.hero.GameHero;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class HeroGamePresenter implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(HeroGamePresenter.class.getName());

    @FXML
    private ScrollPane mapScrollPane;

    @FXML
    private ImageView heroSheetView;

    @FXML
    private ImageView heroImageView;

    @FXML
    private Label labelName;
    @FXML
    private Label labelSpeed;
    @FXML
    private Label labelHealth;
    @FXML
    private Label labelStamina;
    @FXML
    private Label labelMight;
    @FXML
    private Label labelKnowledge;
    @FXML
    private Label labelWillpower;
    @FXML
    private Label labelAwareness;
    @FXML
    private Label labelHeroAbility;
    @FXML
    private Label labelHeroicFeat;

    @FXML
    private ListView<WsMessage> chatListView;

    @FXML
    private TextField messageTextField;

    @Inject
    private Credentials credentials;

    @Inject
    private GameDataModel gameDataModel;

    private final String IMAGE_SUFFIX = ".png";

    private LinearGradient gradientMenuItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        gradientMenuItem = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.DARKVIOLET),
            new Stop(0.1, Color.BLACK),
            new Stop(0.9, Color.BLACK),
            new Stop(1, Color.DARKVIOLET)
        });

        // load active quest encounter of current campaign
        if (gameDataModel.getCurrentQuestEncounter() == null) {
            loadQuestEncounter();
        }

        setupHeroSheet();
        setupMessages();
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

    private void setupMessages() {
        // messages
        chatListView.setItems(gameDataModel.getCampaignMessages());
        chatListView.setCellFactory(param -> new ListCell<WsMessage>() {
            private Text text;

            @Override
            protected void updateItem(WsMessage item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getMessageText() == null) {
                    setText(null);
                } else {
                    text = new Text(item.getUsername() + ": " + item.getMessageText());
                    text.setWrappingWidth(chatListView.getPrefWidth());
                    setGraphic(text);
                }
            }
        });
        // ENTER key on message text area
        messageTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSendMessage(event);
            }
        });
        updateCampaignMessages();
    }

    private void setupHeroSheet() {
        GameHero hero = gameDataModel.getCurrentCampaign().getGameHeroes().stream()
                .filter(h -> h.getPlayedBy().getUsername().equals(credentials.getUsername()))
                .findAny()
                .orElse(null);

        labelName.setText(hero.getName());
        labelSpeed.setText(String.valueOf(hero.getMovementPoints() - hero.getMovementSpent()));
        labelHealth.setText(String.valueOf(hero.getCurrentLife()));
        labelStamina.setText(String.valueOf(hero.getExhaustion()));
        labelMight.setText(String.valueOf(hero.getMight()));
        labelKnowledge.setText(String.valueOf(hero.getKnowledge()));
        labelWillpower.setText(String.valueOf(hero.getWillpower()));
        labelAwareness.setText(String.valueOf(hero.getAwareness()));
        labelHeroAbility.setText(hero.getHeroTemplate().getHeroAbilityText());
        labelHeroicFeat.setText(hero.getHeroTemplate().getHeroicFeatText());

        // sheet
        StringBuilder ressourcePathSheet = new StringBuilder("/img/layout/hero_sheet_");
        ressourcePathSheet.append(hero.getHeroTemplate().getArchetype().name().toLowerCase());
        ressourcePathSheet.append(IMAGE_SUFFIX);
        InputStream isSheet = getClass().getResourceAsStream(ressourcePathSheet.toString());
        if (isSheet != null) {
            heroSheetView.setImage(new Image(isSheet));
        }

        // hero image
        StringBuilder ressourcePathHero = new StringBuilder("/img/heroes/");
        ressourcePathHero.append(hero.getHeroTemplate().getImageName());
        ressourcePathHero.append(IMAGE_SUFFIX);
        InputStream isHero = getClass().getResourceAsStream(ressourcePathHero.toString());
        if (isHero != null) {
            heroImageView.setImage(new Image(isHero));
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
    public void handleRefresh(MouseEvent event) {
        LOGGER.info("HeroGamePresenter: handleRefresh()");
        updateCampaignMessages();
    }

    @FXML
    public void handleNavigationBack(MouseEvent event) {
        LOGGER.info("HeroGamePresenter: handleNavigationBack()");
        switchFullscreenScene(event, new StartMenuView());
    }

    @FXML
    public void handleSendMessage(MouseEvent event) {
        // cast Event to prevent recursive stack overflow
        handleSendMessage((Event) event);
    }

    private void handleSendMessage(Event event) {
        if ((messageTextField.getText() != null) && (!messageTextField.getText().isEmpty())) {
            WsMessage message = null;
            try {
                message = MessageClient.postMessage(credentials.getPlayer(), gameDataModel.getCurrentCampaign(), messageTextField.getText());
            } catch (ServerException ex) {
                showError(ex);
            }
            if (message != null) {
                updateCampaignMessages();
                messageTextField.clear();
            }
        }
    }

    private void updateCampaignMessages() {
        gameDataModel.getCampaignMessages().clear();

        List<WsMessage> campaignMessages = null;
        try {
            campaignMessages = MessageClient.getCampaignMessages(credentials.getUsername(), credentials.getPassword(), gameDataModel.getCurrentCampaign());
        } catch (ServerException ex) {
            showError(ex);
        }

        if (campaignMessages != null && !campaignMessages.isEmpty()) {
            gameDataModel.getCampaignMessages().addAll(campaignMessages);
            chatListView.scrollTo(gameDataModel.getCampaignMessages().size() - 1);
        }
    }
}
