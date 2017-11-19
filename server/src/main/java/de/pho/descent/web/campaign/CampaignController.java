package de.pho.descent.web.campaign;

import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsHeroSelection;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.CampaignPhase;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.hero.HeroSelection;
import static de.pho.descent.shared.model.quest.Quest.FIRST_BLOOD;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.QuestPart;
import de.pho.descent.web.player.PlayerController;
import de.pho.descent.web.auth.UserValidationException;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.quest.QuestController;
import de.pho.descent.web.service.PersistenceService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
@Stateless
public class CampaignController {

    @Inject
    private PlayerController playerController;

    @Inject
    private QuestController questController;

    @Inject
    private transient CampaignService campaignService;

    @Inject
    private transient PersistenceService persistenceService;

    public List<WsCampaign> getPlayableCampaigns(Player player) throws UserValidationException {

        List<Campaign> activeCampaigns = campaignService.getActiveCampaigns();
        List<WsCampaign> playableCampaigns = new ArrayList<>();

        for (Campaign c : activeCampaigns) {
            if (c.getOverlord().equals(player)) {
                playableCampaigns.add(WsCampaign.createInstance(c));
                continue;
            }

            // check if player is part of campaign heroes
            boolean partOfCampaignHeroes = false;
            for (GameHero hero : c.getHeroes()) {
                if (player.equals(hero.getPlayedBy())) {
                    partOfCampaignHeroes = true;
                    playableCampaigns.add(WsCampaign.createInstance(c));
                    break;
                }
            }

            // check if player is part of hero selections
            List<HeroSelection> selections = campaignService.getCurrentSelectionsByCampaignId(c.getId());

            if (!partOfCampaignHeroes && (c.getPhase() == CampaignPhase.HERO_SELECTION)) {
                if (selections.size() < 4) {
                    // not part but hero selection still going with enough room
                    playableCampaigns.add(WsCampaign.createInstance(c));
                } else {
                    // check if player is part of full hero selection
                    for (HeroSelection selection : selections) {
                        if (selection.getPlayer().equals(player)) {
                            playableCampaigns.add(WsCampaign.createInstance(c));
                            break;
                        }
                    }
                }
            }
        }

        return playableCampaigns;
    }

    public void checkCampaignPhase(Campaign campaign, CampaignPhase phaseToBeChecked) throws NotFoundException, CampaignValidationException {
        if (campaign == null) {
            throw new NotFoundException("Campaign not found");
        }

        if (campaign.getPhase() != phaseToBeChecked) {
            throw new CampaignValidationException("Campaign not in phase: " + phaseToBeChecked.name());
        }
    }

    private void checkCampaignPhase(String campaignId, CampaignPhase phaseToBeChecked) throws NotFoundException, CampaignValidationException {
        Campaign campaign = campaignService.getCampaignById(Long.parseLong(campaignId));

        if (campaign == null) {
            throw new NotFoundException("Campaign with Id: " + campaignId + " not found");
        }

        checkCampaignPhase(campaign, phaseToBeChecked);
    }

    /**
     * 1. check if calling user is overlord of campaign 2. check if all hero
     * selections within campaign are in status 'ready' 3. create game heroes
     * based on hero selection 4. move campaign into 'encounter' phase 5.
     * determine first hero turn based on initiation attribute
     *
     * @param player
     * @param campaignId
     * @throws UserValidationException
     * @throws HeroSelectionException
     * @throws NotFoundException
     */
    public Campaign startCampaign(Player player, String campaignId) throws UserValidationException, HeroSelectionException, NotFoundException {

        Campaign campaign = campaignService.getCampaignById(Long.parseLong(campaignId));

        if (!player.equals(campaign.getOverlord())) {
            StringBuilder sb = new StringBuilder("Only overlord '");
            sb.append(campaign.getOverlord().getUsername());
            sb.append("' can start campaign id ").append(campaignId);
            throw new UserValidationException(sb.toString());
        }

        if (!campaignService.allSelectionsReady(Long.parseLong(campaignId))) {
            throw new HeroSelectionException("Can not start campaign: some selections are not ready");
        }
        
        return campaign;
    }

    public Campaign createCampaign(WsCampaign wsCampaign) {
        Campaign c = new Campaign();

        Player overlord = playerController.getPlayerByName(wsCampaign.getOverlord());
        c.setOverlord(overlord);
        c.setPhase(wsCampaign.getPhase());
        c.setActiveQuest(questController.loadQuestEncounter(wsCampaign.getActiveQuest(), wsCampaign.getPart()));

        return campaignService.saveCampaign(c);
    }

    public Campaign newCampaign(Player overlord) {

        Campaign c = new Campaign();
        c.setOverlord(overlord);
        c.setPhase(CampaignPhase.HERO_SELECTION);
        c.setActiveQuest(questController.loadQuestEncounter(FIRST_BLOOD, QuestPart.FIRST));

        return campaignService.saveCampaign(c);
    }

    /**
     * @TODO: Heroselection: @OnDelete(action = OnDeleteAction.CASCADE)
     *
     * @param id
     */
    public void removeCampaign(long id) {
        List<HeroSelection> selections = campaignService.getCurrentSelectionsByCampaignId(id);
        persistenceService.deleteList(selections);
        campaignService.deleteCampaign(id);
    }

    public List<HeroSelection> getCurrentSelection(String campaignId) throws CampaignValidationException, NotFoundException {
        // check for hero selection phase
        checkCampaignPhase(campaignId, CampaignPhase.HERO_SELECTION);

        return campaignService.getCurrentSelectionsByCampaignId(Long.parseLong(campaignId));
    }

    public HeroSelection saveSelection(String campaignId, Player player, WsHeroSelection wsSelection) throws HeroSelectionException, NotFoundException, CampaignValidationException {

        Campaign campaign = campaignService.getCampaignById(Long.parseLong(campaignId));

        // check for hero selection phase
        checkCampaignPhase(campaign, CampaignPhase.HERO_SELECTION);

        // check if user is overlord
        if (Objects.equals(player, campaign.getOverlord())) {
            throw new HeroSelectionException("Overlord can not take part in hero selection");
        }

        // check if calling user is not part of already full group
        String username = wsSelection.getUsername();
        List<HeroSelection> campaignSelections = getCurrentSelection(campaignId);
        boolean partOfSelection = false;
        for (HeroSelection hSelection : campaignSelections) {
            // check if calling user is already part of group
            if (hSelection.getPlayer().getUsername().equalsIgnoreCase(username)) {
                partOfSelection = true;
                break;
            }
        }
        if (!partOfSelection && (campaignSelections.size() == 4)) {
            // hero group already full
            throw new HeroSelectionException("Hero selection not possible. Group is full");
        }

        HeroSelection selection = null;
        if (wsSelection.getId() > 0) {
            selection = persistenceService.find(HeroSelection.class, wsSelection.getId());
        } else {
            selection = new HeroSelection();
        }

        selection.setCampaign(campaign);
        selection.setPlayer(player);
        selection.setSelectedHero(wsSelection.getSelectedHero());
        selection.setReady(wsSelection.isReady());

        return campaignService.saveSelection(selection);
    }

    public QuestEncounter getCurrentEncounter() {
        return new QuestEncounter();
    }

    public QuestEncounter getEncounter(int id) {
        return new QuestEncounter();
    }
}
