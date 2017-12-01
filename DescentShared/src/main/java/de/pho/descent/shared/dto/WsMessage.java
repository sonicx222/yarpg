package de.pho.descent.shared.dto;

import de.pho.descent.shared.model.message.Message;
import java.util.Date;

/**
 *
 * @author pho
 */
public class WsMessage {

    private String username;
    private long campaignId;
    private Date createdOn;
    private String messageText;

    public WsMessage() {
    }

    public WsMessage(String username, long campaignId, String messageText) {
        this.username = username;
        this.campaignId = campaignId;
        this.messageText = messageText;
    }

    /**
     * Factory-Method to create new WsMessage DTOs
     *
     * @param message the copy template for the DTO
     * @return the filled WsMessage-DTO instance
     */
    public static WsMessage createInstance(Message message) {
        WsMessage wsMessage = new WsMessage();

        long campaignId = message.getCampaign() == null ? 0 : message.getCampaign().getId();
        wsMessage.setCampaignId(campaignId);
        wsMessage.setUsername(message.getPlayer().getUsername());
        wsMessage.setCreatedOn(message.getCreatedOn());
        wsMessage.setMessageText(message.getText());

        return wsMessage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(long campaignId) {
        this.campaignId = campaignId;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

}
