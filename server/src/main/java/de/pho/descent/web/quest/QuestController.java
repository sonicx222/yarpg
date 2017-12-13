package de.pho.descent.web.quest;

import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.map.GameMap;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.QuestTemplate;
import de.pho.descent.web.map.MapController;
import de.pho.descent.web.quest.encounter.FirstBloodQuestSetup;
import de.pho.descent.web.service.PersistenceService;
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
        encounter.setIsActive(true);
        encounter.setMap(gameMap);
        encounter.setQuest(questTemplate.getQuest());
        encounter.setPart(questTemplate.getQuestPart());

        // TODO
        encounter.setActiveHero(campaign.getHeroes().stream().findFirst().orElse(null));
        switch (questTemplate) {
            case FIRST_BLOOD_INTRO:
                FirstBloodQuestSetup.setup(encounter, campaign);
                break;
            default:
                break;
        }

        return questService.saveEncounter(encounter);
    }

//    public QuestEncounter loadQuestEncounter(Quest quest, QuestPart part) {
//        QuestEncounter encounter = questService.getEncounterByQuestAndPart(quest, part);
//        return encounter;
//    }
}
