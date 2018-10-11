package de.pho.descent.web.map;

import de.pho.descent.shared.model.map.GameMap;
import de.pho.descent.shared.model.map.MapField;
import de.pho.descent.shared.model.quest.Quest;
import de.pho.descent.shared.model.quest.QuestPart;
import de.pho.descent.web.exception.NotFoundException;
import java.io.Serializable;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author pho
 */
@Stateless
public class MapService implements Serializable {

    private static final long serialVersionUID = 5L;

    @PersistenceContext
    private transient EntityManager em;

    public GameMap getMapByID(long mapID) {
        return em.find(GameMap.class, mapID);
    }
    
    public MapField getMapFieldById(long fieldId) {
        return em.find(MapField.class, fieldId);
    }
    
    public GameMap getMapByQuestAndPart(Quest quest, QuestPart part) throws NotFoundException {
        TypedQuery<GameMap> query = em.createNamedQuery(GameMap.FIND_MAP_BY_QUEST_AND_PART, GameMap.class)
                .setParameter(GameMap.QUEST_PARAM, quest)
                .setParameter(GameMap.QUESTPART_PARAM, part);
        List<GameMap> results = query.getResultList();

        if (results == null || results.isEmpty()) {
            throw new NotFoundException("GameMap for quest: " + quest + "/" + part + " not found");
        }

        return results.get(0);
    }

    public List<MapField> getPlayerSpawnFieldsByMapId(long mapId) {
        return em.createNamedQuery(MapField.FIND_SPAWNS_BY_MAPID, MapField.class)
                .setParameter(MapField.MAPID_PARAM, mapId)
                .getResultList();
    }

    public List<MapField> getPlayerSpawnFieldsByGroupId(long groupId) {
        return em.createNamedQuery(MapField.FIND_SPAWNS_BY_GROUPID, MapField.class)
                .setParameter(MapField.GROUPID_PARAM, groupId)
                .getResultList();
    }
}
