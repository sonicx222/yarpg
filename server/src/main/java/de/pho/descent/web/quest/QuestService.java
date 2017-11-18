package de.pho.descent.web.quest;

import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.Quest;
import de.pho.descent.shared.model.quest.QuestPart;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author pho
 */
@Stateless
public class QuestService {

    private static final Logger LOG = Logger.getLogger(QuestService.class.getName());

    @PersistenceContext
    private transient EntityManager em;

    public QuestEncounter getIntroQuestEncounter() {
        TypedQuery<QuestEncounter> query;
        QuestEncounter intro = null;

        query = em.createNamedQuery(QuestEncounter.findByQuestAndPart, QuestEncounter.class);
        query.setParameter(QuestEncounter.paramQuest, Quest.FIRST_BLOOD)
                .setParameter(QuestEncounter.paramPart, QuestPart.FIRST);
        List<QuestEncounter> resultList = query.getResultList();

        if (!resultList.isEmpty()) {
            intro = resultList.get(0);
        }

        return intro;
    }

    public QuestEncounter getEncounterByQuestAndPart(Quest quest, QuestPart part) {
        TypedQuery<QuestEncounter> query;
        QuestEncounter encounter = null;

        query = em.createNamedQuery(QuestEncounter.findByQuestAndPart, QuestEncounter.class);
        query.setParameter(QuestEncounter.paramQuest, quest);
        query.setParameter(QuestEncounter.paramPart, part);

        List<QuestEncounter> resultList = query.getResultList();

        if (!resultList.isEmpty()) {
            encounter = resultList.get(0);
        }

        return encounter;
    }
}
