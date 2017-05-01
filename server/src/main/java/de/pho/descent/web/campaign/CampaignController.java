package de.pho.descent.web.campaign;

import de.pho.descent.shared.model.Player;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.campaign.CampaignPhase;
import de.pho.descent.shared.model.hero.HeroSelection;
import de.pho.descent.shared.model.hero.HeroTemplate;
import static de.pho.descent.shared.model.quest.Quest.FIRST_BLOOD;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.QuestPart;
import de.pho.descent.web.quest.QuestController;
import de.pho.descent.web.service.PersistenceService;
import de.pho.descent.web.service.PlayerService;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author pho
 */
@Stateless
public class CampaignController {

    @EJB
    private QuestController questController;

    @EJB
    private PlayerService playerService;

    @EJB
    private PersistenceService persistenceService;

    @EJB
    private transient CampaignService campaignService;

    public List<Campaign> getAllCampaigns() {
        return campaignService.getAllCampaigns();
    }

    public Campaign createNewCampaign(Campaign c) {
        c.setPhase(CampaignPhase.HERO_SELECTION);
        c.setActiveQuest(questController.loadQuestEncounter(FIRST_BLOOD, QuestPart.FIRST));

        return campaignService.createCampaign(c);
    }

    public Campaign createTestCampaign() {
        List<Player> players = playerService.getAllPlayers();
        Campaign c = new Campaign();
        c.setPhase(CampaignPhase.HERO_SELECTION);
        c = persistenceService.update(c);

        HeroSelection hs = new HeroSelection();
        hs.setCampaign(c);
        hs.setPlayer(players.get(1));
        hs.setSelectedHero(HeroTemplate.SYNDRAEL);
        persistenceService.create(hs);

        HeroSelection hs1 = new HeroSelection();
        hs1.setCampaign(c);
        hs1.setPlayer(players.get(1));
        hs1.setSelectedHero(HeroTemplate.ASHRIAN);
        persistenceService.create(hs);

        HeroSelection hs2 = new HeroSelection();
        hs2.setCampaign(c);
        hs2.setPlayer(players.get(2));
        hs2.setSelectedHero(HeroTemplate.AVRICALBRIGHT);
        persistenceService.create(hs);

        return new Campaign();
    }

    public QuestEncounter getCurrentEncounter() {
        return new QuestEncounter();
    }

    public QuestEncounter getEncounter(int id) {
        return new QuestEncounter();
    }
}
