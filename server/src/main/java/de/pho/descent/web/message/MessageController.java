package de.pho.descent.web.message;

import de.pho.descent.shared.dto.WsMessage;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.message.Message;
import de.pho.descent.web.campaign.CampaignService;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.player.PlayerService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
        Collections.reverse(generalMessages);

        return beansToDTOs(generalMessages);
    }

    public List<WsMessage> getMessagesByCampaignId(long campaignId) {
        List<Message> campaignMessages = messageService.getCampaignMessages(campaignId);
        Collections.reverse(campaignMessages);

        return beansToDTOs(campaignMessages);
    }

    public List<WsMessage> getCampaignMessagesSinceDate(long campaignId, String username) throws NotFoundException {
        List<WsMessage> dtoList = new ArrayList<>();
        Date dateFrom = null;
        Campaign campaign = campaignService.getCampaignById(campaignId);

        if (campaign != null) {

            // test for overlord
            if (campaign.getOverlord().getPlayedBy().getUsername().equals(username)) {
                Date date = campaign.getOverlord().getLastMessageUpdate();
                dateFrom = (date == null ? new Date() : new Date(date.getTime()));
                campaign.getOverlord().setLastMessageUpdate(new Date());
            } else {
                GameHero hero = campaign.getActiveQuest().getHeroes().stream()
                        .filter(h -> h.getPlayedBy().getUsername().equals(username))
                        .findAny().orElse(null);
                if (hero == null) {
                    throw new NotFoundException("Campaign player with username " + username + " not found");
                }
                Date date = hero.getLastMessageUpdate();
                dateFrom = (date == null ? new Date() : new Date(date.getTime()));
                hero.setLastMessageUpdate(new Date());
            }

            List<Message> campaignMessages;
            if (dateFrom == null) {
                campaignMessages = messageService.getCampaignMessages(campaignId);
            } else {
                campaignMessages = messageService.getCampaignMessagesSinceDate(campaignId, dateFrom);
            }
            Collections.reverse(campaignMessages);
            dtoList = beansToDTOs(campaignMessages);
        }

        return dtoList;
    }

    public Message saveMessage(WsMessage wsMessage) throws NotFoundException {
        Player player = playerService.getPlayerByUsername(wsMessage.getUsername());
        Campaign campaign = null;

        if (wsMessage.getCampaignId() > 0) {
            campaign = campaignService.getCampaignById(wsMessage.getCampaignId());
        }

        Message message = new Message(campaign, wsMessage.getType(), player, wsMessage.getMessageText());

        return messageService.saveMessage(message);
    }

    public Message saveMessage(Message message) {
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
