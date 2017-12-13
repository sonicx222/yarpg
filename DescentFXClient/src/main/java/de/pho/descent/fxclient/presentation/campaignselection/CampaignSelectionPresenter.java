package de.pho.descent.fxclient.presentation.campaignselection;

import static de.pho.descent.fxclient.MainApp.showError;
import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.CampaignClient;
import de.pho.descent.fxclient.business.ws.MessageClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.game.hero.HeroGameView;
import de.pho.descent.fxclient.presentation.game.overlord.OverlordGameView;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.heroselection.HeroSelectionView;
import de.pho.descent.fxclient.presentation.startmenu.StartMenuView;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsMessage;
import de.pho.descent.shared.model.campaign.CampaignPhase;
import de.pho.descent.shared.model.quest.Quest;
import de.pho.descent.shared.model.quest.QuestPart;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
public class CampaignSelectionPresenter implements Initializable {

    @FXML
    private StackPane paneContinue;

    @FXML
    private TableView campaignsTableView;

    @FXML
    private TableColumn<WsCampaign, Long> campaignIDColumn;

    @FXML
    private TableColumn<WsCampaign, String> campaignOverlordColumn;

    @FXML
    private TableColumn<WsCampaign, CampaignPhase> campaignPhaseColumn;

    @FXML
    private TableColumn<WsCampaign, String> campaignPlayersColumn;

    @FXML
    private TableColumn<WsCampaign, Quest> campaignQuestColumn;

    @FXML
    private TableColumn<WsCampaign, QuestPart> campaignQuestPartColumn;

    @FXML
    private TableColumn<WsCampaign, Date> campaignCreatedColumn;

    @FXML
    private ListView<WsMessage> chatListView;

    @FXML
    private TextField messageTextField;

    @Inject
    private GameDataModel gameDataModel;

    @Inject
    private Credentials credentials;

    private LinearGradient gradientMenuItem;

    private ResourceBundle resources = null;

    private WsCampaign selectedCampaign;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        gradientMenuItem = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.DARKVIOLET),
            new Stop(0.1, Color.BLACK),
            new Stop(0.9, Color.BLACK),
            new Stop(1, Color.DARKVIOLET)
        });

        // disable continue button, until selection made
        paneContinue.setDisable(true);

        // populate table view
        updatePlayableCampaigns();
        campaignsTableView.setItems(gameDataModel.getPlayableCampaigns());
        campaignsTableView.setEditable(false);

        // columns
        campaignIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        campaignOverlordColumn.setCellValueFactory(new PropertyValueFactory<>("overlord"));
        campaignPlayersColumn.setCellValueFactory((TableColumn.CellDataFeatures<WsCampaign, String> cell) -> {
            String strPlayers;
            if (cell.getValue().getPhase() == CampaignPhase.HERO_SELECTION) {
                strPlayers = String.valueOf(cell.getValue().getCountHeroSelections());
            } else {
                strPlayers = String.valueOf(cell.getValue().getGameHeroes().size());
            }
            return new SimpleStringProperty(strPlayers);
        });
        campaignPhaseColumn.setCellValueFactory(new PropertyValueFactory<>("phase"));
        campaignQuestColumn.setCellValueFactory(new PropertyValueFactory<>("activeQuest"));
        campaignQuestPartColumn.setCellValueFactory(new PropertyValueFactory<>("part"));
        campaignCreatedColumn.setCellValueFactory(new PropertyValueFactory<>("createdOn"));

        // selection action
        campaignsTableView.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue observale, Object oldValue, Object newValue) -> {
                    selectedCampaign = (WsCampaign) campaignsTableView.getSelectionModel().getSelectedItem();

                    if (selectedCampaign != null) {
                        paneContinue.setDisable(false);
                    } else {
                        paneContinue.setDisable(true);
                    }
                }
                );

        // messages part
        chatListView.setItems(gameDataModel.getGeneralMessages());
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
        updateGeneralMessages();
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
    public void handleBack(MouseEvent event) {
        switchFullscreenScene(event, new StartMenuView());
    }

    @FXML
    public void handleRefresh(MouseEvent event) {
        updatePlayableCampaigns();
        updateGeneralMessages();
    }

    @FXML
    public void handleContinue(MouseEvent event) {
        requireNonNull(selectedCampaign);
        gameDataModel.setCurrentCampaign(selectedCampaign);
        if (selectedCampaign.getPhase() == CampaignPhase.HERO_SELECTION) {
            switchFullscreenScene(event, new HeroSelectionView());
        } else {
            if (Objects.equals(credentials.getUsername(), selectedCampaign.getOverlord())) {
                switchFullscreenScene(event, new OverlordGameView());
            } else {
                switchFullscreenScene(event, new HeroGameView());
            }
        }
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
                message = MessageClient.postMessage(credentials.getPlayer(), null, messageTextField.getText());
            } catch (ServerException ex) {
                showError(ex);
            }
            if (message != null) {
                updateGeneralMessages();
                messageTextField.clear();
            }
        }
    }

    private void updateGeneralMessages() {
        gameDataModel.getGeneralMessages().clear();

        List<WsMessage> generalMessages = null;
        try {
            generalMessages = MessageClient.getGeneralMessages(credentials.getUsername(), credentials.getPassword());
        } catch (ServerException ex) {
            showError(ex);
        }

        if (generalMessages != null && !generalMessages.isEmpty()) {
            gameDataModel.getGeneralMessages().addAll(generalMessages);
            chatListView.scrollTo(gameDataModel.getGeneralMessages().size() - 1);
        }
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
