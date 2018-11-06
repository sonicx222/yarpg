package de.pho.descent.fxclient.presentation.campaignselection;

import static de.pho.descent.fxclient.MainApp.showError;
import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.config.Configuration;
import de.pho.descent.fxclient.business.ws.CampaignClient;
import de.pho.descent.fxclient.business.ws.MessageClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.general.GameService;
import de.pho.descent.fxclient.presentation.message.MessageService;
import de.pho.descent.fxclient.presentation.startmenu.StartMenuView;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsMessage;
import de.pho.descent.shared.model.campaign.CampaignPhase;
import de.pho.descent.shared.model.quest.Quest;
import de.pho.descent.shared.model.quest.QuestPart;
import java.net.URL;
import java.time.ZoneId;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.ResourceBundle;
import java.util.logging.Logger;
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
import javafx.scene.text.Text;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class CampaignSelectionPresenter implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(CampaignSelectionPresenter.class.getName());

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
    private TableColumn<WsCampaign, String> campaignCreatedColumn;

    @FXML
    private ListView<WsMessage> chatListView;

    @FXML
    private TextField messageTextField;

    @Inject
    private GameDataModel gameDataModel;

    @Inject
    private GameService gameService;

    @Inject
    private Credentials credentials;

    @Inject
    private MessageService messageService;

    private WsCampaign selectedCampaign;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // disable continue button, until selection made
        paneContinue.setDisable(true);

        // populate table view
        updatePlayableCampaigns();
        campaignsTableView.setItems(gameDataModel.getPlayableCampaigns());
        campaignsTableView.setEditable(false);

        // columns
        campaignIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        campaignOverlordColumn.setCellValueFactory((TableColumn.CellDataFeatures<WsCampaign, String> cell) -> {
            return new SimpleStringProperty(cell.getValue().getOverlord().getPlayedBy().getUsername());
        });
        campaignPlayersColumn.setCellValueFactory((TableColumn.CellDataFeatures<WsCampaign, String> cell) -> {
            String strPlayers = String.valueOf(cell.getValue().getCountHeroSelections());
            return new SimpleStringProperty(strPlayers);
        });
        campaignPhaseColumn.setCellValueFactory(new PropertyValueFactory<>("phase"));
        campaignQuestColumn.setCellValueFactory(new PropertyValueFactory<>("currentQuest"));
        campaignQuestPartColumn.setCellValueFactory(new PropertyValueFactory<>("currentPart"));
        campaignCreatedColumn.setCellValueFactory(
                (TableColumn.CellDataFeatures<WsCampaign, String> p) -> new SimpleStringProperty(
                        Configuration.DATE_TIME_FORMAT.format(p.getValue().getCreatedOn()
                                .toInstant().atZone(ZoneId.systemDefault())
                                .toLocalDateTime())));

        // selection action
        // quality of life change: pre select only item in list
        if (campaignsTableView.getItems().size() == 1) {
            campaignsTableView.getSelectionModel().selectFirst();
            selectedCampaign = (WsCampaign) campaignsTableView.getSelectionModel().getSelectedItem();
            paneContinue.setDisable(false);
        }
        campaignsTableView.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                    selectedCampaign = (WsCampaign) campaignsTableView.getSelectionModel().getSelectedItem();

                    if (selectedCampaign != null) {
                        paneContinue.setDisable(false);
                    } else {
                        paneContinue.setDisable(true);
                    }
                }
                );

        // messages part
        setupGeneralMessages();
    }

    private void setupGeneralMessages() {
        chatListView.setItems(gameDataModel.getGeneralMessages());
        chatListView.setCellFactory(param -> new ListCell<WsMessage>() {
            private Text text;

            @Override
            protected void updateItem(WsMessage item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getMessageText() == null) {
                    setText(null);
                    setGraphic(null);
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
        messageService.updateGeneralMessages();
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
    public void handleBack(MouseEvent event) {
        switchFullscreenScene(new StartMenuView());
    }

    @FXML
    public void handleRefresh(MouseEvent event) {
        LOGGER.info("CampaignSelectionPresenter: handleRefresh");
        updatePlayableCampaigns();
        messageService.updateGeneralMessages();
    }

    @FXML
    public void handleContinue(MouseEvent event) {
        LOGGER.info("CampaignSelectionPresenter: handleContinue");
        requireNonNull(selectedCampaign);
        gameDataModel.setCurrentCampaign(selectedCampaign);
        gameService.loadQuestEncounter();
        gameService.navigateToScene(selectedCampaign);
    }

    @FXML
    public void handleSendMessage(MouseEvent event) {
        LOGGER.info("CampaignSelectionPresenter: handleSendMessage");
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
                messageService.updateGeneralMessages();
                messageTextField.clear();
            }
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
