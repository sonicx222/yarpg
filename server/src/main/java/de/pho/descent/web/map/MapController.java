package de.pho.descent.web.map;

import de.pho.descent.shared.model.map.GameMap;
import de.pho.descent.shared.model.map.MapField;
import de.pho.descent.shared.model.quest.Quest;
import de.pho.descent.shared.model.quest.QuestPart;
import de.pho.descent.shared.model.quest.QuestTemplate;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
@Stateless
public class MapController {

    @Inject
    private transient MapService mapService;

    public List<MapField> getSpawnFieldsByMap(long mapId) {
        return mapService.getPlayerSpawnFieldsByMapId(mapId);
    }

    public GameMap getMapByQuestTemplate(QuestTemplate template) {
        return getMapByQuestAndPart(template.getQuest(), template.getQuestPart());
    }

    public GameMap getMapByQuestAndPart(Quest quest, QuestPart part) {
        return mapService.getMapByQuestAndPart(quest, part);
    }

    public GameMap getMapById(long id) {
        return mapService.getMapByID(id);
    }
}
