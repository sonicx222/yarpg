package de.pho.descent.web.quest;

import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.Quest;
import de.pho.descent.shared.model.quest.QuestPart;
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

    public QuestEncounter loadQuestEncounter(Quest quest, QuestPart part) {
        QuestEncounter encounter = questService.getEncounterByQuestAndPart(quest, part);
        return encounter;
    }
}
