package de.pho.descent.shared.service;

import de.pho.descent.shared.model.map.MapField;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author pho
 */
public class MapRangeService {

    private static final Logger LOGGER = Logger.getLogger(MapRangeService.class.getName());

    public static Set<MapField> getFieldsInRange(MapField rootField, int range, List<MapField> mapFields, List<MapField> heroFields) {
        Set<MapField> inRangeSet = new HashSet<>();

        searchFieldsInRange(rootField, 0, range, mapFields, heroFields, inRangeSet);
        inRangeSet.remove(rootField);

        return inRangeSet;
    }

    private static void searchFieldsInRange(MapField field, int currentMoveCost, int rangeToCheck, List<MapField> mapFields, List<MapField> unpassableFields, Set<MapField> inRangeSet) {
//        LOGGER.info("Current reference field: " + field.getId());
//        LOGGER.info("Current move cost: " + currentMoveCost);
        List<MapField> nbFields = getFieldNeighbours(field, mapFields);

        for (MapField nbField : nbFields) {
//            LOGGER.info("Current nb field: " + field.getId() + "-" + nbField.getId());
            if ((currentMoveCost + nbField.getMoveCost() <= rangeToCheck)
                    && nbField.isPassable()
                    && !unpassableFields.contains(nbField)) {
                inRangeSet.add(nbField);
                searchFieldsInRange(nbField, currentMoveCost + nbField.getMoveCost(), rangeToCheck, mapFields, unpassableFields, inRangeSet);
            }
        }
    }

    private static List<MapField> getFieldNeighbours(MapField referenceField, List<MapField> fields) {
//        LOGGER.info("Calc neighbours for ID: " + referenceField.getId());
        List<MapField> nbList = new ArrayList<>();
        for (int xDelta = -1; xDelta <= 1; xDelta++) {
            for (int yDelta = -1; yDelta <= 1; yDelta++) {
                MapField nbField = getField(referenceField.getxPos() + xDelta, referenceField.getyPos() + yDelta, fields);
                if (nbField != null && !Objects.equals(referenceField, nbField)) {
//                    System.out.println("Found nb field: " + nbField.getId());
                    nbList.add(nbField);
                }
            }
        }
        return nbList;
    }

    private static MapField getField(int x, int y, List<MapField> fields) {
        return fields.stream()
                .filter(field -> (field.getxPos() == x && field.getyPos() == y))
                .findAny()
                .orElse(null);
    }
}
