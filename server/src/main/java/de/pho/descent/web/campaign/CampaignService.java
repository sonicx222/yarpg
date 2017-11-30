package de.pho.descent.web.campaign;

import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.hero.HeroSelection;
import de.pho.descent.web.exception.NotFoundException;
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

    public List<Campaign> getActiveCampaigns() {
        return em.createNamedQuery(Campaign.FINDACTIVE, Campaign.class).getResultList();
    }

    public Campaign getCampaignById(long campaignId) throws NotFoundException {
        Campaign campaign = em.find(Campaign.class, campaignId);
        if (campaign == null) {
            throw new NotFoundException("Campaign with Id: " + campaignId + " not found");
        }

        return campaign;
    }

    public Campaign saveCampaign(Campaign c) {
        return em.merge(c);
    }

    public void deleteCampaign(long id) {
        Object ref = em.getReference(Campaign.class, id);
        em.remove(ref);
    }

    public List<HeroSelection> getCurrentSelectionsByCampaignId(long campaignId) {
        Campaign c = em.find(Campaign.class, campaignId);
        return c.getHeroSelections();
    }

    public HeroSelection saveSelection(HeroSelection selection) throws HeroSelectionException {
        return em.merge(selection);
    }
}
