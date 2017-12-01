package de.pho.descent.web.message;

import de.pho.descent.shared.model.message.Message;
import java.io.Serializable;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author pho
 */
@Stateless
public class MessageService implements Serializable {

    private static final long serialVersionUID = 5L;
    private static final int MAX_RESULTS = 30;

    @PersistenceContext
    private transient EntityManager em;

    public List<Message> getGeneralMessages() {
        return em.createNamedQuery(Message.FINDGENERAL, Message.class)
                .setMaxResults(MAX_RESULTS)
                .getResultList();
    }

    public List<Message> getCampaignMessages(long id) {
        return em.createNamedQuery(Message.FINDBYCAMPAIGNID, Message.class)
                .setMaxResults(MAX_RESULTS)
                .setParameter(Message.CAMPAIGN_ID_PARAM, id).getResultList();
    }

    public List<Message> getPlayerMessagesByCampaign(long playerId, long campaignId) {
        return em.createNamedQuery(Message.FINDBYCAMPAIGNIDANDPLAYERID, Message.class)
                .setMaxResults(MAX_RESULTS)
                .setParameter(Message.CAMPAIGN_ID_PARAM, campaignId)
                .setParameter(Message.PLAYER_ID_PARAM, playerId).getResultList();
    }

    public Message saveMessage(Message m) {
        return em.merge(m);
    }
}
