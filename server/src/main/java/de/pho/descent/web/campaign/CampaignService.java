package de.pho.descent.web.campaign;

import de.pho.descent.shared.model.campaign.Campaign;
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
public class CampaignService implements Serializable {

    private static final long serialVersionUID = 5L;

    @PersistenceContext
    private transient EntityManager em;

    public List<Campaign> getAllCampaigns() {
        return em.createNamedQuery(Campaign.findAll, Campaign.class).getResultList();
    }
    
    public Campaign createCampaign(Campaign c) {
         return em.merge(c);
    }
}
