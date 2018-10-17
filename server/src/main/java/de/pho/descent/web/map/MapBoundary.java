package de.pho.descent.web.map;

import de.pho.descent.shared.auth.ParamValue;
import de.pho.descent.shared.dto.WsGameMap;
import de.pho.descent.shared.dto.WsMap;
import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.map.FieldAttribute;
import de.pho.descent.web.exception.NotFoundException;
import de.pho.descent.shared.model.map.GameMap;
import de.pho.descent.shared.model.map.MapField;
import de.pho.descent.shared.service.MapLosService;
import de.pho.descent.web.quest.QuestController;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author pho
 */
@Stateless
@Path(ParamValue.SECURED_URL + "/maps")
public class MapBoundary {

    private static final Logger LOGGER = Logger.getLogger(MapBoundary.class.getName());

    @Inject
    private MapController mapController;

    @Inject
    private QuestController questController;

    @GET
    @Path("{" + ParamValue.MAP_ID + "}")
    @Produces({MediaType.APPLICATION_JSON})
    public WsGameMap getMap(@PathParam(ParamValue.MAP_ID) long mapID) throws NotFoundException {
        LOGGER.log(Level.INFO, "Calling getMap() with id: {0}", mapID);

        GameMap map = mapController.getMapById(mapID);

        if (map == null) {
            throw new NotFoundException();
        }

        return WsGameMap.createInstance(map);
    }

    @POST
    @Path("{" + ParamValue.MAP_ID + "}/text")
    @Produces({MediaType.TEXT_PLAIN})
    @Consumes({MediaType.TEXT_PLAIN})
    public String getMapAsText(@PathParam(ParamValue.MAP_ID) long mapID, String type) throws NotFoundException {
        LOGGER.log(Level.INFO, "Calling getMapAsText() with id: {0}", mapID);
        GameMap map = mapController.getMapById(mapID);

        if (map == null) {
            throw new NotFoundException();
        }

        String mapLayout = null;
        FieldAttribute attr = FieldAttribute.valueOf(type);
        if (attr == FieldAttribute.COMBINED) {
            mapLayout = map.asText(FieldAttribute.FIELDID) + System.lineSeparator() + map.asText(FieldAttribute.UNITID);
        } else {
            mapLayout = map.asText(attr);
        }

        return mapLayout;
    }

    @POST
    @Path("/los")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Boolean getLOSCheck(WsMap map) throws NotFoundException {
        GameMap gameMap = mapController.getMapById(map.getMap());

        if (gameMap == null) {
            throw new NotFoundException();
        }

        GameUnit sourceUnit = questController.getGamUnit(map.getSource());
        GameUnit targetUnit = questController.getGamUnit(map.getTarget());

        return MapLosService.hasLineOfSight(sourceUnit, targetUnit, gameMap);
    }

    @POST
    @Path("/losfields")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getLOSFields(WsMap map) throws NotFoundException {
        GameMap gameMap = mapController.getMapById(map.getMap());

        if (gameMap == null) {
            throw new NotFoundException();
        }
        GameUnit sourceUnit = questController.getGamUnit(map.getSource());
        GameUnit targetUnit = questController.getGamUnit(map.getTarget());
        List<MapField> fields = MapLosService.getLOSPath(sourceUnit, targetUnit, gameMap);

        // wrap Fields in special List for REST response
        GenericEntity<List<MapField>> list = new GenericEntity<List<MapField>>(fields) {
        };

        return Response.ok().entity(list).build();
    }
}
