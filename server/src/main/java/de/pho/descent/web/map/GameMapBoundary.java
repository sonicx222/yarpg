package de.pho.descent.web.map;

import de.pho.descent.shared.dto.WsGameMap;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.shared.model.GameMap;
import de.pho.descent.shared.model.MapField;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author pho
 */
@Stateless
@Path("/maps")
public class GameMapBoundary {

    private static final Logger LOGGER = Logger.getLogger(GameMapBoundary.class.getName());

    @EJB
    private MapController mapController;

    @GET
    @Path("{mapID}")
    @Produces({MediaType.APPLICATION_JSON})
    public WsGameMap getMap(@PathParam("mapID") long mapID) throws NotFoundException {
        GameMap map = mapController.getMapByID(mapID);

        if (map == null) {
            throw new NotFoundException();
        }

        LOGGER.info(map.toString());

        return WsGameMap.createInstance(map);
    }

    @GET
    @Path("/getfield")
    @Produces({MediaType.APPLICATION_JSON})
    public MapField getMapField() {
        return new MapField(47, 11, 99, true);
    }

}
