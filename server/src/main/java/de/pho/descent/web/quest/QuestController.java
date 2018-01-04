package de.pho.descent.web.quest;

import de.pho.descent.shared.model.PlaySide;
import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.map.GameMap;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.QuestTemplate;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.web.map.MapController;
import de.pho.descent.web.quest.encounter.FirstBloodQuestSetup;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
@Stateless
public class QuestController {

    @Inject
    private QuestService questService;

    @Inject
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
        encounter.setActive(true);
        encounter.setRound(1);
        encounter.setMap(gameMap);
        encounter.setQuest(questTemplate.getQuest());
        encounter.setPart(questTemplate.getQuestPart());
        encounter.setCurrentTurn(PlaySide.HEROES);

        // set first active hero based on highest initiative roll
        if (!campaign.getHeroes().isEmpty()) {
            SortedMap<Integer, GameHero> initiativeOrder = new TreeMap<>();
            campaign.getHeroes().stream().forEach(hero -> {
                initiativeOrder.put(hero.rollInitiative(), hero);
            });
            GameHero activeHero = initiativeOrder.get(initiativeOrder.lastKey());
            activeHero.setActive(true);
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

    public void setNextActiveHero(Campaign campaign) {
        Objects.requireNonNull(campaign);

        if (!campaign.getHeroes().isEmpty()) {
            campaign.getActiveHero().setActive(false);

            SortedMap<Integer, GameHero> initiativeOrder = new TreeMap<>();
            campaign.getHeroes().stream()
                    .filter(hero -> hero.getActions() > 0)
                    .forEach(hero -> {
                        initiativeOrder.put(hero.rollInitiative(), hero);
                    });
            GameHero nextActiveHero = initiativeOrder.get(initiativeOrder.lastKey());
            nextActiveHero.setActive(true);
        }

    }

}
