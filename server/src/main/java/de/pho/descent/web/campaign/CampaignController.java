package de.pho.descent.web.campaign;

import static java.util.Objects.requireNonNull;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsHeroSelection;
import de.pho.descent.shared.model.PlaySide;
import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.campaign.CampaignPhase;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.hero.HeroSelection;
import de.pho.descent.shared.model.overlord.Overlord;
import de.pho.descent.shared.model.overlord.OverlordClass;
import de.pho.descent.shared.model.quest.LootBox;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.QuestReward;
import de.pho.descent.shared.model.quest.QuestTemplate;
import de.pho.descent.web.auth.UserValidationException;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.quest.QuestController;
import de.pho.descent.web.quest.QuestValidationException;
import de.pho.descent.web.quest.encounter.FirstBlood;
import de.pho.descent.web.service.PersistenceService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
    private QuestController questController;

    @Inject
    private transient CampaignService campaignService;

    @Inject
    private transient PersistenceService persistenceService;

    public List<Campaign> getPlayableCampaigns(Player player) throws UserValidationException {

        List<Campaign> activeCampaigns = campaignService.getActiveCampaigns();
        List<Campaign> playableCampaigns = new ArrayList<>();

        for (Campaign c : activeCampaigns) {
            if (c.getOverlord().getPlayedBy().equals(player)) {
                playableCampaigns.add(c);
                continue;
            }

            // check if player is part of active quest heroes
            boolean partOfActiveQuest = false;
            if (c.getActiveQuest() != null) {
                for (GameHero hero : c.getActiveQuest().getHeroes()) {
                    if (player.equals(hero.getPlayedBy())) {
                        partOfActiveQuest = true;
                        playableCampaigns.add(c);
                        break;
                    }
                }
            }

            // check if player is instead part of hero selections
            if (!partOfActiveQuest && (c.getPhase() == CampaignPhase.HERO_SELECTION)) {
                List<HeroSelection> selections = c.getHeroSelections();
                if (selections.size() < 4) {
                    // hero selection still going with enough room
                    playableCampaigns.add(c);
                    continue;
                } else {
                    for (HeroSelection hs : selections) {
                        if (hs.getPlayer().equals(player)) {
                            playableCampaigns.add(c);
                            break;
                        }
                    }
                }
            }
        }

        return playableCampaigns;
    }

    public Campaign getCampaignById(long id) throws NotFoundException {
        return campaignService.getCampaignById(id);
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
    public Campaign startCampaign(Player player, String campaignId) throws UserValidationException, HeroSelectionException, NotFoundException, IOException, QuestValidationException {

        Campaign campaignToBeStarted = campaignService.getCampaignById(Long.parseLong(campaignId));

        // check if starting player is overlord
        if (!player.equals(campaignToBeStarted.getOverlord().getPlayedBy())) {
            StringBuilder sb = new StringBuilder("Only overlord '");
            sb.append(campaignToBeStarted.getOverlord().getPlayedBy().getUsername());
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

        // create initial start quest
        QuestEncounter encounter = startNextQuestEncounter(campaignToBeStarted);
        campaignToBeStarted.setActiveQuest(encounter);
        campaignToBeStarted.setPhase(CampaignPhase.ENCOUNTER);
        campaignToBeStarted.setStartedOn(new Date());

        return campaignService.saveCampaign(campaignToBeStarted);
    }

    public Campaign createCampaign(WsCampaign wsCampaign) throws NotFoundException, IOException, QuestValidationException {
        Campaign c = new Campaign();

//        Overlord overlord = wsCampaign.getOverlord();
//        persistenceService.create(overlord);
//        c.setOverlord(overlord);
//        c.setPhase(wsCampaign.getPhase());
//        c.setActiveQuest(questController.createQuestEncounter(wsCampaign.getNextQuest(), c));
        return campaignService.saveCampaign(c);
    }

    public Campaign newCampaign(Player overlordPlayer) {

        Campaign c = new Campaign();
        Overlord overlord = new Overlord(overlordPlayer, OverlordClass.BASIC, new Date());
        persistenceService.create(overlord);
        c.setOverlord(overlord);
        c.setPhase(CampaignPhase.HERO_SELECTION);
        c.setNextQuestTemplate(QuestTemplate.FIRST_BLOOD_INTRO);

        return campaignService.saveCampaign(c);
    }

    public void endActiveUnitTurn(Campaign campaign) throws QuestValidationException, NotFoundException {
        questController.deactivateCurrentUnit(campaign.getActiveQuest());
        questController.setNextActiveUnit(campaign);
    }

    public void endActiveQuest(Campaign campaign) throws QuestValidationException, NotFoundException {
        QuestEncounter encounter = campaign.getActiveQuest();
        LootBox box = null;

        encounter.setActive(false);
        switch (encounter.getQuest()) {
            case FIRST_BLOOD: {
                box = FirstBlood.getQuestRewards();
            }
            break;
            default:
                break;
        }
        // rewards
        handleQuestReward(campaign, box);

        // next phase
        campaign = setNextCampaignPhase(campaign);
    }

    public QuestEncounter startNextQuestEncounter(Campaign campaign) throws NotFoundException, IOException, QuestValidationException {
        List<GameHero> heroes = null;

        if (campaign.getActiveQuest() != null && !campaign.getActiveQuest().isActive() && campaign.getActiveQuest().getWinner() != null) {
            // current quest = old quest => transfer heroes
            heroes = campaign.getActiveQuest().getHeroes();
        } else {
            // create game heroes
            heroes = new ArrayList<>();
            for (HeroSelection hs : campaign.getHeroSelections()) {
                GameHero hero = new GameHero(hs.getSelectedHero());
                hero.setPlayedBy(hs.getPlayer());
                hero.setActions(2);
                heroes.add(hero);
            }
        }

        return questController.createQuestEncounter(campaign.getNextQuestTemplate(), campaign, heroes);
    }

    private void handleQuestReward(Campaign campaign, LootBox box) {
        QuestEncounter encounter = campaign.getActiveQuest();

        if (encounter.getWinner() == PlaySide.HEROES) {
            QuestReward heroesReward = box.getRewardBySide(PlaySide.HEROES);

            // gold
            campaign.setGold(campaign.getGold() + heroesReward.getGold());

            // xp
            encounter.getHeroes().stream()
                    .forEach(hero -> hero.addXp(heroesReward.getXp()));

            // TODO item e.g relics
        } else {
            QuestReward overlordReward = box.getRewardBySide(PlaySide.OVERLORD);

            // xp
            campaign.getOverlord().addXp(overlordReward.getXp());

            // TODO item e.g relics
        }
    }

    public Campaign setNextCampaignPhase(String campaignId) throws NotFoundException {
        Campaign campaign = campaignService.getCampaignById(Long.parseLong(campaignId));

        return setNextCampaignPhase(campaign);
    }

    public Campaign setNextCampaignPhase(Campaign campaign) throws NotFoundException {

        switch (campaign.getPhase()) {
            case HERO_SELECTION:
                campaign.setPhase(CampaignPhase.ENCOUNTER);
                break;
            case ENCOUNTER:
                campaign.setPhase(CampaignPhase.FINISHED_ENCOUNTER);
                break;
            case FINISHED:
                campaign.setPhase(CampaignPhase.MARKETPLACE);
                break;
            case MARKETPLACE:
                campaign.setPhase(CampaignPhase.SKILL_SELECTION);
                break;
            case SKILL_SELECTION:
                campaign.setPhase(CampaignPhase.QUEST_SELECTION);
                break;
            case QUEST_SELECTION:
                campaign.setPhase(CampaignPhase.TRAVEL);
                break;
            case TRAVEL:
                campaign.setPhase(CampaignPhase.ENCOUNTER);
                break;
        }

        return campaign;
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

        // check if hero selection user is same as service caller
        if (!Objects.equals(player.getUsername(), wsSelection.getUsername())) {
            throw new HeroSelectionException("Hero selection can only be made for same user calling service");
        }

        // check if user is overlord
        if (Objects.equals(player, campaign.getOverlord().getPlayedBy())) {
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
//                if (Objects.equals(hSelection.getPlayer().getUsername(), wsSelection.getUsername())) {
                if (hSelection.getId() == wsSelection.getId()) {
                    selection = hSelection;
                    break;
                }
            }
        }
        if (selection == null) {
            throw new HeroSelectionException("Hero selection with id " + wsSelection.getId() + " not found for update");
        }

        // set selection attributes
        selection.setSelectedHero(wsSelection.getSelectedHero());
        selection.setReady(wsSelection.isReady());

        campaignService.saveSelection(selection);
        return selection;
    }
}
