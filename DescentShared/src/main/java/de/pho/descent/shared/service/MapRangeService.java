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

    public static Set<MapField> getFieldsInMovementRange(List<MapField> rootFields, int range, List<MapField> mapFields, List<MapField> heroFields) {
        Set<MapField> inRangeSet = new HashSet<>();

        for (MapField rootField : rootFields) {
            searchFieldsInRange(rootField, true, 0, range, mapFields, heroFields, inRangeSet);
            inRangeSet.remove(rootField);
        }

        return inRangeSet;
    }

    public static Set<MapField> getFieldsInMovementRange(MapField rootField, int range, List<MapField> mapFields, List<MapField> heroFields) {
        Set<MapField> inRangeSet = new HashSet<>();

        searchFieldsInRange(rootField, true, 0, range, mapFields, heroFields, inRangeSet);
        inRangeSet.remove(rootField);

        return inRangeSet;
    }

    public static Set<MapField> getFieldsInAttackRange(MapField rootField, int range, List<MapField> mapFields, List<MapField> heroFields) {
        Set<MapField> inRangeSet = new HashSet<>();

        searchFieldsInRange(rootField, false, 0, range, mapFields, heroFields, inRangeSet);
        inRangeSet.remove(rootField);

        return inRangeSet;
    }

    private static void searchFieldsInRange(MapField field, boolean useMovementCost, int currentMoveCost, int rangeToCheck, List<MapField> mapFields, List<MapField> unpassableFields, Set<MapField> inRangeSet) {
//        LOGGER.info("Current reference field: " + field.getId());
//        LOGGER.info("Current move cost: " + currentMoveCost);
        List<MapField> nbFields = getFieldNeighbours(field, mapFields);

        for (MapField nbField : nbFields) {
//            LOGGER.info("Current nb field: " + field.getId() + "-" + nbField.getId());
            if (useMovementCost) {
                if ((currentMoveCost + nbField.getMoveCost() <= rangeToCheck)
                        && nbField.isPassable()
                        && !unpassableFields.contains(nbField)) {
                    inRangeSet.add(nbField);
                    searchFieldsInRange(nbField, useMovementCost, currentMoveCost + nbField.getMoveCost(), rangeToCheck, mapFields, unpassableFields, inRangeSet);
                }
            } else {
                searchFieldsInRange(nbField, useMovementCost, 0, rangeToCheck, mapFields, unpassableFields, inRangeSet);
            }
        }
    }

    private static List<MapField> getFieldNeighbours(MapField referenceField, List<MapField> mapFields) {
//        LOGGER.info("Calc neighbours for ID: " + referenceField.getId());
        List<MapField> nbList = new ArrayList<>();
        for (int xDelta = -1; xDelta <= 1; xDelta++) {
            for (int yDelta = -1; yDelta <= 1; yDelta++) {
                MapField nbField = getFieldByPosition(referenceField.getxPos() + xDelta, referenceField.getyPos() + yDelta, mapFields);
                if (nbField != null && !Objects.equals(referenceField, nbField)) {
//                    System.out.println("Found nb field: " + nbField.getId());
                    nbList.add(nbField);
                }
            }
        }
        return nbList;
    }

    private static MapField getFieldByPosition(int x, int y, List<MapField> mapFields) {
        return mapFields.stream()
                .filter(field -> (field.getxPos() == x && field.getyPos() == y))
                .findAny()
                .orElse(null);
    }
}
