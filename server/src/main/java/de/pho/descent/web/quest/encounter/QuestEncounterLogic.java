package de.pho.descent.web.quest.encounter;

import de.pho.descent.shared.model.campaign.Campaign;
import de.pho.descent.shared.model.quest.QuestEncounter;

/**
 *
 * @author pho
 */
public abstract class QuestEncounterLogic {
 public abstract void setup(QuestEncounter encounter, Campaign campaign);
}
