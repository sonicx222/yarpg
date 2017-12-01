package de.pho.descent.web.message;

import de.pho.descent.shared.dto.WsMessage;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.message.Message;
import de.pho.descent.web.campaign.CampaignService;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.player.PlayerService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
@Stateless
public class MessageController {

    @Inject
    private MessageService messageService;

    @Inject
    private PlayerService playerService;

    @Inject
    private CampaignService campaignService;

    public List<WsMessage> getGeneralMessages() {
        List<Message> generalMessages = messageService.getGeneralMessages();
//        Collections.reverse(generalMessages);

        return beansToDTOs(generalMessages);
    }

    public List<WsMessage> getMessagesByCampaignId(long campaignId) {
        List<Message> campaignMessages = messageService.getCampaignMessages(campaignId);
//        Collections.reverse(campaignMessages);

        return beansToDTOs(campaignMessages);
    }

    public Message saveMessage(WsMessage wsMessage) throws NotFoundException {
        Player player = playerService.getPlayerByUsername(wsMessage.getUsername());
        Campaign campaign = null;

        if (wsMessage.getCampaignId() > 0) {
            campaign = campaignService.getCampaignById(wsMessage.getCampaignId());
        }

        Message message = new Message(campaign, player, wsMessage.getMessageText());

        return messageService.saveMessage(message);
    }

    private List<WsMessage> beansToDTOs(List<Message> messages) {
        List<WsMessage> dtoList = new ArrayList<>();

        for (Message m : messages) {
            dtoList.add(WsMessage.createInstance(m));
        }

        return dtoList;
    }
}
