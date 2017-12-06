package de.pho.descent.web.quest;

import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.map.GameMap;
import de.pho.descent.shared.model.map.MapField;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.QuestTemplate;
import de.pho.descent.web.map.MapController;
import de.pho.descent.web.service.PersistenceService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private PersistenceService persistenceService;

    @Inject
    private MapController mapController;

    public QuestEncounter getQuestEncounterById(long id) {
        return questService.loadEncounterById(id);
    }
    
    public QuestEncounter startNextQuestEncounter(Campaign campaign) {
        return createQuestEncounter(campaign.getTemplateNextQuest(), campaign);
    }

    public QuestEncounter createQuestEncounter(QuestTemplate questTemplate, Campaign campaign) {
        QuestEncounter encounter = new QuestEncounter();

        GameMap gameMap = mapController.getMapByQuestTemplate(questTemplate);
        encounter.setMap(gameMap);
        encounter.setQuest(questTemplate.getQuest());

        spawnHeroes(encounter, campaign.getHeroes());
        spawnMonster(encounter);

        return questService.saveEncounter(encounter);
    }

    private void spawnHeroes(QuestEncounter encounter, List<GameHero> heroes) {
        List<MapField> spawnFields = new ArrayList<>();
        spawnFields.addAll(encounter.getMap().getHeroSpawnFields());
        // random spawn order
        Collections.shuffle(spawnFields);

        for (int i = 0; i < heroes.size(); i++) {
            GameHero hero = heroes.get(i);
            hero.setCurrentLocation(spawnFields.get(i));
        }
    }

    private void spawnMonster(QuestEncounter encounter) {

    }

//    public QuestEncounter loadQuestEncounter(Quest quest, QuestPart part) {
//        QuestEncounter encounter = questService.getEncounterByQuestAndPart(quest, part);
//        return encounter;
//    }
}
