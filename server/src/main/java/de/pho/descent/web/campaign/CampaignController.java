package de.pho.descent.web.campaign;

import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsHeroSelection;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.CampaignPhase;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.hero.HeroSelection;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.QuestTemplate;
import de.pho.descent.web.player.PlayerController;
import de.pho.descent.web.auth.UserValidationException;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.quest.QuestController;
import de.pho.descent.web.service.PersistenceService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author pho
 */
@Stateless
public class CampaignController {

    @EJB
    private PlayerController playerController;

    @EJB
    private QuestController questController;

    @EJB
    private transient CampaignService campaignService;

    @EJB
    private transient PersistenceService persistenceService;

    public List<WsCampaign> getPlayableCampaigns(Player player) throws UserValidationException {

        List<Campaign> activeCampaigns = campaignService.getActiveCampaigns();
        List<WsCampaign> playableCampaigns = new ArrayList<>();

        for (Campaign c : activeCampaigns) {
            if (c.getOverlord().equals(player)) {
                playableCampaigns.add(WsCampaign.createInstance(c));
                continue;
            }

            // check if player is part of campaignToBeStarted heroes
            boolean partOfCampaignHeroes = false;
            for (GameHero hero : c.getHeroes()) {
                if (player.equals(hero.getPlayedBy())) {
                    partOfCampaignHeroes = true;
                    playableCampaigns.add(WsCampaign.createInstance(c));
                    break;
                }
            }

            // check if player is instead part of hero selections
            if (!partOfCampaignHeroes && (c.getPhase() == CampaignPhase.HERO_SELECTION)) {
                List<HeroSelection> selections = c.getHeroSelections();
                if (selections.size() < 4) {
                    // hero selection still going with enough room
                    playableCampaigns.add(WsCampaign.createInstance(c));
                    continue;
                } else {
                    for (HeroSelection hs : selections) {
                        if (hs.getPlayer().equals(player)) {
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
        requireNonNull(campaign);

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
     * 1. check if calling user is overlord of campaignToBeStarted 2. check if
     * there are enough hero selections to start 3. check if all hero selections
     * within campaignToBeStarted are in status 'ready' 4. create game heroes
     * based on hero selection 5. move campaignToBeStarted into 'encounter'
     * phase 6. determine first hero turn based on initiation attribute
     *
     * @param player
     * @param campaignId
     * @return the started Campaign
     * @throws UserValidationException
     * @throws HeroSelectionException
     * @throws NotFoundException
     */
    public Campaign startCampaign(Player player, String campaignId) throws UserValidationException, HeroSelectionException, NotFoundException {

        Campaign campaignToBeStarted = campaignService.getCampaignById(Long.parseLong(campaignId));

        // check if starting player is overlord
        if (!player.equals(campaignToBeStarted.getOverlord())) {
            StringBuilder sb = new StringBuilder("Only overlord '");
            sb.append(campaignToBeStarted.getOverlord().getUsername());
            sb.append("' can start campaign id ").append(campaignId);

            throw new UserValidationException(sb.toString());
        }

        // check if there are at least 2 hero selections
        List<HeroSelection> heroSelections = campaignToBeStarted.getHeroSelections();
        if (heroSelections.size() < 2) {
            throw new HeroSelectionException("Can not start campaign: there need to be at least two hero selections");
        }

        // check if all selections are ready
        for (HeroSelection hs : heroSelections) {
            if (!hs.isReady()) {
                throw new HeroSelectionException("Can not start campaign: some selections are not ready");
            }
        }

        // create game heroes
        List<GameHero> heroes = new ArrayList<>();
        for (HeroSelection hs : heroSelections) {
            GameHero hero = new GameHero(hs.getSelectedHero());
            hero.setPlayedBy(hs.getPlayer());
            hero.setTurnsLeft(2);
            heroes.add(hero);
        }
        campaignToBeStarted.getHeroes().clear();
        campaignToBeStarted.getHeroes().addAll(heroes);

        QuestEncounter encounter = questController.startNextQuestEncounter(campaignToBeStarted);
        campaignToBeStarted.setActiveQuest(encounter);
        campaignToBeStarted.setPhase(CampaignPhase.ENCOUNTER);
        campaignToBeStarted.setStartedOn(new Date());

        return campaignService.saveCampaign(campaignToBeStarted);
    }

    public Campaign createCampaign(WsCampaign wsCampaign) throws NotFoundException {
        Campaign c = new Campaign();

        Player overlord = playerController.getPlayerByName(wsCampaign.getOverlord());
        c.setOverlord(overlord);
        c.setPhase(wsCampaign.getPhase());
        c.setActiveQuest(questController.createQuestEncounter(wsCampaign.getNextQuest(), c));

        return campaignService.saveCampaign(c);
    }

    public Campaign newCampaign(Player overlord) {

        Campaign c = new Campaign();
        c.setOverlord(overlord);
        c.setPhase(CampaignPhase.HERO_SELECTION);
        c.setTemplateNextQuest(QuestTemplate.FIRST_BLOOD_INTRO);

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

    public List<HeroSelection> getCurrentSelections(String campaignId) throws CampaignValidationException, NotFoundException {
        // check for hero selection phase
        checkCampaignPhase(campaignId, CampaignPhase.HERO_SELECTION);

        return campaignService.getCurrentSelectionsByCampaignId(Long.parseLong(campaignId));
    }

    public HeroSelection saveSelection(long campaignId, Player player, WsHeroSelection wsSelection) throws HeroSelectionException, NotFoundException, CampaignValidationException {

        // check if used campaign Ids are inconsistent
        if (campaignId != wsSelection.getCampaignId()) {
            throw new HeroSelectionException("Campaign Id within selection posted against campaign Id do not match");
        }

        Campaign campaign = campaignService.getCampaignById(campaignId);

        // check for hero selection phase
        checkCampaignPhase(campaign, CampaignPhase.HERO_SELECTION);

        // check if user is overlord
        if (Objects.equals(player, campaign.getOverlord())) {
            throw new HeroSelectionException("Overlord can not take part in hero selection");
        }

        // check if hero is already selected
        List<HeroSelection> heroSelections = campaign.getHeroSelections();
        for (HeroSelection hSelection : heroSelections) {
            if (hSelection.getSelectedHero().equals(wsSelection.getSelectedHero())
                    && !hSelection.getPlayer().getUsername().equals(wsSelection.getUsername())
                    && hSelection.isReady()) {
                throw new HeroSelectionException("Hero " + wsSelection.getSelectedHero().getName() + " already selected");
            }
        }

        // check if calling user is not part of already full group  
        HeroSelection selection = null;
        if (wsSelection.getId() == 0) {
            // new campaign selection
            if (heroSelections.size() == 4) {
                throw new HeroSelectionException("Hero selection not possible. Group is full");
            } else {
                selection = new HeroSelection();
                selection.setPlayer(player);
                selection.setCampaign(campaign);
                heroSelections.add(selection);
            }
        } else {
            for (HeroSelection hSelection : heroSelections) {
                if (Objects.equals(hSelection.getPlayer().getUsername(), wsSelection.getUsername())) {
                    selection = hSelection;
                    break;
                }
            }
        }

        // set selection attributes
        selection.setSelectedHero(wsSelection.getSelectedHero());
        selection.setReady(wsSelection.isReady());

        campaignService.saveSelection(selection);
        return selection;
    }

    public QuestEncounter getCurrentEncounter() {
        return new QuestEncounter();
    }

    public QuestEncounter getEncounter(int id) {
        return new QuestEncounter();
    }
}
