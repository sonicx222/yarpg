package de.pho.descent.web.quest;

import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.map.GameMap;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.QuestTemplate;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.map.MapController;
import de.pho.descent.web.quest.encounter.FirstBloodQuestSetup;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author pho
 */
@Stateless
public class QuestController {

    @EJB
    private QuestService questService;

    @EJB
    private MapController mapController;

    public QuestEncounter getQuestEncounterById(long id) {
        return questService.loadEncounterById(id);
    }

    public QuestEncounter startNextQuestEncounter(Campaign campaign) throws NotFoundException {
        return createQuestEncounter(campaign.getTemplateNextQuest(), campaign);
    }

    public QuestEncounter createQuestEncounter(QuestTemplate questTemplate, Campaign campaign) throws NotFoundException {
        QuestEncounter encounter = new QuestEncounter();

        GameMap gameMap = mapController.getMapByQuestTemplate(questTemplate);
        encounter.setIsActive(true);
        encounter.setRound(1);
        encounter.setMap(gameMap);
        encounter.setQuest(questTemplate.getQuest());
        encounter.setPart(questTemplate.getQuestPart());

        // set first active hero based on highest initiative roll
        if (!campaign.getHeroes().isEmpty()) {
            SortedMap<Integer, GameHero> initiativeOrder = new TreeMap<>();
            campaign.getHeroes().stream().forEach(hero -> {
                initiativeOrder.put(hero.rollInitiative(), hero);
            });
            GameHero activeHero = initiativeOrder.get(initiativeOrder.lastKey());
            activeHero.setActive(true);
            encounter.setActiveHero(activeHero);
        }

        switch (questTemplate) {
            case FIRST_BLOOD_INTRO:
                FirstBloodQuestSetup.setup(encounter, campaign);
                break;
            default:
                break;
        }

        return questService.saveEncounter(encounter);
    }

}
