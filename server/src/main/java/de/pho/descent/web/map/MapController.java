package de.pho.descent.web.map;

import de.pho.descent.shared.model.GameMap;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author pho
 */
@Stateless
public class MapController {

    @PersistenceContext
    private EntityManager em;

    public GameMap getMapByID(long mapID) {
        return em.find(GameMap.class, mapID);
    }

}
