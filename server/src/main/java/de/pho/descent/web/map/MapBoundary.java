package de.pho.descent.web.map;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.dto.WsGameMap;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.shared.model.map.GameMap;
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
@Path(ParamValue.SECURED_URL + "/maps")
public class MapBoundary {

    private static final Logger LOGGER = Logger.getLogger(MapBoundary.class.getName());

    @EJB
    private MapController mapController;

    @GET
    @Path("{" + ParamValue.MAP_ID + "}")
    @Produces({MediaType.APPLICATION_JSON})
    public WsGameMap getMap(@PathParam(ParamValue.MAP_ID) long mapID) throws NotFoundException {
        GameMap map = mapController.getMapById(mapID);

        if (map == null) {
            throw new NotFoundException();
        }

        return WsGameMap.createInstance(map);
    }

    @GET
    @Path("{" + ParamValue.MAP_ID + "}/text")
    @Produces({MediaType.TEXT_PLAIN})
    public String getMapAsText(@PathParam(ParamValue.MAP_ID) long mapID) throws NotFoundException {
        GameMap map = mapController.getMapById(mapID);

        if (map == null) {
            throw new NotFoundException();
        }

        return map.toString();
    }

//    @GET
//    @Path("/getfield")
//    @Produces({MediaType.APPLICATION_JSON})
//    public MapField getMapField() {
//        return new MapField(47, 11, 99, true, false, false);
//    }
}
