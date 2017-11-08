
package de.pho.descent.shared.dto;

import de.pho.descent.shared.model.quest.Quest;
import de.pho.descent.shared.model.quest.QuestEncounter;
import de.pho.descent.shared.model.quest.QuestPart;

/**
 *
 * @author pho
 */
public class WsQuestEncounter {
    
    
    private Quest quest;
    
    private QuestPart part;

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    public QuestPart getPart() {
        return part;
    }

    public void setPart(QuestPart part) {
        this.part = part;
    }
   
    /**
     * Factory-Method to create new WsQuestEncounter DTOs
     *
     * @param encounter the copy template for the DTO
     * @return the filled WsQuestEncounter-DTO instance
     */
    public static WsQuestEncounter createInstance(QuestEncounter encounter) {
        WsQuestEncounter wsQuestEncounter = new WsQuestEncounter();

        wsQuestEncounter.setQuest(encounter.getQuest());
        wsQuestEncounter.setPart(encounter.getPart());

        return wsQuestEncounter;
    }

    @Override
    public String toString() {
        return "WsQuestEncounter{" + "quest=" + quest + ", part=" + part + '}';
    }
}
