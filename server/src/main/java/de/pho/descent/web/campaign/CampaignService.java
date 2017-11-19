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
        return em.createNamedQuery(HeroSelection.findByCampaign, HeroSelection.class)
                .setParameter(HeroSelection.paramCampaignId, campaignId)
                .getResultList();
    }

    public HeroSelection saveSelection(HeroSelection selection) throws HeroSelectionException {
        checkIfHeroIsAlreadySelected(selection);

        return em.merge(selection);
    }

    public boolean allSelectionsReady(long campaignId) {
        List<HeroSelection> selections = getCurrentSelectionsByCampaignId(campaignId);
        boolean allReady = true;

        for (HeroSelection hs : selections) {
            if (!hs.isReady()) {
                allReady = false;
                break;
            }
        }

        return allReady;
    }

    private void checkIfHeroIsAlreadySelected(HeroSelection selection) throws HeroSelectionException {
        List<HeroSelection> savedSelections = getCurrentSelectionsByCampaignId(selection.getCampaign().getId());

        for (HeroSelection tmpSelection : savedSelections) {
            if (selection.getCampaign().equals(tmpSelection.getCampaign())
                    && selection.getSelectedHero().equals(tmpSelection.getSelectedHero())
                    && !selection.getPlayer().equals(tmpSelection.getPlayer())) {
                throw new HeroSelectionException("Hero " + selection.getSelectedHero().getName() + " already selected");
            }
        }
    }
}
