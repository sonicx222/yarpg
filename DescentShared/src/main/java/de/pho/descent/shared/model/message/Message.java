package de.pho.descent.shared.model.message;

import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.Campaign;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author pho
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Message.FINDGENERAL, query = "select m from Message as m"
            + " where m.campaign is null"
            + " order by m.createdOn desc")
    ,
    @NamedQuery(name = Message.FINDBYCAMPAIGNID, query = "select m from Message as m"
            + " where m.campaign.id = :" + Message.CAMPAIGN_ID_PARAM
            + " order by m.createdOn desc")
    ,
    @NamedQuery(name = Message.FINDBYCAMPAIGNIDANDDATE, query = "select m from Message as m"
            + " where m.campaign.id = :" + Message.CAMPAIGN_ID_PARAM
            + " and m.createdOn > :" + Message.DATE_PARAM
            + " order by m.createdOn desc")
    ,
    @NamedQuery(name = Message.FINDBYCAMPAIGNIDANDPLAYERID, query = "select m from Message as m"
            + " where m.campaign.id = :" + Message.CAMPAIGN_ID_PARAM
            + " and m.player.id = :" + Message.PLAYER_ID_PARAM
            + " order by m.createdOn desc")
})
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String FINDGENERAL = "de.pho.descent.shared.model.message.Message.findGeneral";
    public static final String FINDBYCAMPAIGNID = "de.pho.descent.shared.model.message.Message.findbyCampaign";
    public static final String FINDBYCAMPAIGNIDANDDATE = "de.pho.descent.shared.model.message.Message.findbyCampaignAndDate";
    public static final String FINDBYCAMPAIGNIDANDPLAYERID = "de.pho.descent.shared.model.message.Message.findbyCampaignIDPlayerId";
    public static final String CAMPAIGN_ID_PARAM = "paramCampaignId";
    public static final String DATE_PARAM = "paramDate";
    public static final String PLAYER_ID_PARAM = "paramPlayerId";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @ManyToOne
    private Campaign campaign;

    @ManyToOne
    private Player player;

    @Temporal(TemporalType.TIMESTAMP)
    private final Date createdOn = new Date();

    @Column(length = 2000)
    private String text;

    public Message() {
    }

    public Message(Campaign campaign, MessageType type, Player player, String text) {
        this.campaign = campaign;
        this.type = type;
        this.player = player;
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
