package de.pho.descent.fxclient.presentation.message;

import static de.pho.descent.fxclient.MainApp.showError;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.config.Configuration;
import de.pho.descent.fxclient.business.ws.MessageClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.general.GameService;
import de.pho.descent.shared.dto.WsMessage;
import java.net.URL;
import java.time.ZoneId;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class MessagePresenter implements Initializable {

    @FXML
    private ListView<WsMessage> chatListView;

    @FXML
    private TextField messageTextField;

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
        setupMessages();
    }

    private void setupMessages() {
        // messages
        gameDataModel.setChatListView(chatListView);
        chatListView.setItems(gameDataModel.getCampaignMessages());
        chatListView.setCellFactory(param -> new ListCell<WsMessage>() {
            private Text text;

            @Override
            protected void updateItem(WsMessage item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getMessageText() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    switch (item.getType()) {
                        case SYSTEM:
                            break;
                        case GAME:
                            text = new Text(item.getMessageText());
                            text.setFill(Color.CORNFLOWERBLUE);
                            break;
                        default:
                            text = new Text(Configuration.DATE_TIME_FORMAT.format(item.getCreatedOn()
                                    .toInstant().atZone(ZoneId.systemDefault())
                                    .toLocalDateTime())
                                    + " " + item.getUsername() + ": " + item.getMessageText());
                            break;
                    }

                    text.setWrappingWidth(chatListView.getPrefWidth());
                    setGraphic(text);
                }
            }
        });
        // ENTER key on message text area
        messageTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSendCampaignMessage(event);
            }
        });
        messageService.updateCampaignMessages();
    }

    @FXML
    public void handleSendMessage(MouseEvent event) {
        // cast Event to prevent recursive stack overflow
        handleSendCampaignMessage((Event) event);
    }

    private void handleSendCampaignMessage(Event event) {
        if ((messageTextField.getText() != null) && (!messageTextField.getText().isEmpty())) {
            WsMessage message = null;
            try {
                message = MessageClient.postMessage(credentials.getPlayer(), gameDataModel.getCurrentCampaign(), messageTextField.getText());
            } catch (ServerException ex) {
                showError(ex);
            }
            if (message != null) {
                messageService.updateCampaignMessages();
                chatListView.scrollTo(gameDataModel.getGeneralMessages().size() - 1);
                messageTextField.clear();
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
