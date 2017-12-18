package de.pho.descent.fxclient.presentation.game.map;

import de.pho.descent.shared.dto.WsGameMap;
import de.pho.descent.shared.model.map.MapField;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

/**
 *
 * @author pho
 */
public class MapService {

    private static final Logger LOGGER = Logger.getLogger(MapService.class.getName());

    @PostConstruct
    public void init() {

    }

    public Set<MapField> getFieldsInRange(MapField rootField, int range, WsGameMap map, List<MapField> heroFields) {
        Set<MapField> inRangeSet = new HashSet<>();

        searchFieldsInRange(rootField, 0, range, map, heroFields, inRangeSet);
        inRangeSet.remove(rootField);

        return inRangeSet;
    }

    private void searchFieldsInRange(MapField field, int currentMoveCost, int rangeToCheck, WsGameMap map, List<MapField> heroFields, Set<MapField> inRangeSet) {
        System.out.println("Current reference field: " + field.getId());
        System.out.println("Current move cost: " + currentMoveCost);
        List<MapField> nbFields = getFieldNeighbours(field, map);

        for (MapField nbField : nbFields) {
            System.out.println("Current nb field: " + field.getId() + "-" + nbField.getId());
            if ((currentMoveCost + nbField.getMoveCost() <= rangeToCheck)
                    && nbField.isPassable()
                    && !heroFields.contains(nbField)) {
                inRangeSet.add(nbField);
                searchFieldsInRange(nbField, currentMoveCost + nbField.getMoveCost(), rangeToCheck, map, heroFields, inRangeSet);
            }
        }
    }

    private List<MapField> getFieldNeighbours(MapField field, WsGameMap map) {
        System.out.println("Calc neighbours for ID: " + field.getId());
        List<MapField> nbList = new ArrayList<>();
        for (int xDelta = -1; xDelta <= 1; xDelta++) {
            for (int yDelta = -1; yDelta <= 1; yDelta++) {
                MapField nbField = map.getField(field.getxPos() + xDelta, field.getyPos() + yDelta);
                if (nbField != null && !Objects.equals(field, nbField)) {
//                    System.out.println("Found nb field: " + nbField.getId());
                    nbList.add(nbField);
                }
            }
        }
        return nbList;
    }
}
