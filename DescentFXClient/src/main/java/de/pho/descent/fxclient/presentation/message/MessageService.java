package de.pho.descent.fxclient.presentation.message;

import static de.pho.descent.fxclient.MainApp.showError;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.MessageClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.shared.dto.WsMessage;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class MessageService {

    @Inject
    private Credentials credentials;
    @Inject
    private GameDataModel gameDataModel;

    @PostConstruct
    public void init() {

    }

    public void updateCampaignMessages() {
        gameDataModel.getCampaignMessages().clear();

        List<WsMessage> campaignMessages = null;
        try {
            campaignMessages = MessageClient.getCampaignMessages(credentials.getUsername(), credentials.getPassword(), gameDataModel.getCurrentCampaign());
        } catch (ServerException ex) {
            showError(ex);
        }

        if (campaignMessages != null && !campaignMessages.isEmpty()) {
            gameDataModel.getCampaignMessages().addAll(campaignMessages);
//            chatListView.scrollTo(gameDataModel.getCampaignMessages().size() - 1);
        }
    }

    public void updateGeneralMessages() {
        gameDataModel.getGeneralMessages().clear();

        List<WsMessage> generalMessages = null;
        try {
            generalMessages = MessageClient.getGeneralMessages(credentials.getUsername(), credentials.getPassword());
        } catch (ServerException ex) {
            showError(ex);
        }

        if (generalMessages != null && !generalMessages.isEmpty()) {
            gameDataModel.getGeneralMessages().addAll(generalMessages);
//            chatListView.scrollTo(gameDataModel.getGeneralMessages().size() - 1);
        }
    }
}
