package de.pho.descent.shared.service;

import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.hero.GameHero;
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

    public static Set<MapField> getFieldsInMovementRange(List<MapField> rootFields, int range, List<MapField> mapFields, List<MapField> heroFields, List<MapField> monsterFields) {
        Set<MapField> inRangeSet = new HashSet<>();

        // treat enemy GameUnit locations as unpassable fields for range check
        List<MapField> unpassableFields = new ArrayList<>();

        if (rootFields.get(0).getGameUnit() instanceof GameHero) {
            unpassableFields.addAll(monsterFields);
        } else {
            unpassableFields.addAll(heroFields);
        }

        for (MapField rootField : rootFields) {
            searchFieldsInRange(rootField, true, 0, range, mapFields, unpassableFields, inRangeSet);
        }

        if (rootFields.get(0).getGameUnit() instanceof GameHero) {
            inRangeSet.removeAll(heroFields);
        } else {
            inRangeSet.removeAll(monsterFields);
        }

        return inRangeSet;
    }

    public static Set<MapField> getFieldsInMovementRange(MapField rootField, int range, List<MapField> mapFields, List<MapField> unpassableFields) {
        Set<MapField> inRangeSet = new HashSet<>();

        searchFieldsInRange(rootField, true, 0, range, mapFields, unpassableFields, inRangeSet);
        inRangeSet.remove(rootField);

        return inRangeSet;
    }

    public static boolean isInAttackRange(GameUnit sourceUnit, GameUnit targetUnit, int range, List<MapField> mapFields) {
        Set<MapField> fieldsInRange = new HashSet<>();
        for (MapField sourceUnitLoc : sourceUnit.getCurrentLocation()) {
            fieldsInRange.addAll(getFieldsInAttackRange(sourceUnitLoc, range, mapFields));
        }

        // check if one of target unit fields is part of the calculated fields from the source unit
        for (MapField targetUnitLoc : targetUnit.getCurrentLocation()) {
            if (fieldsInRange.contains(targetUnitLoc)) {
                return true;
            }
        }

        return false;
    }

    public static Set<MapField> getFieldsInAttackRange(MapField rootField, int range, List<MapField> mapFields) {
        Set<MapField> inRangeSet = new HashSet<>();

        searchFieldsInRange(rootField, false, 0, range, mapFields, new ArrayList<>(), inRangeSet);
        inRangeSet.remove(rootField);

        return inRangeSet;
    }

    private static void searchFieldsInRange(MapField field, boolean useMovementCost, int currentMoveCost, int rangeToCheck, List<MapField> mapFields, List<MapField> unpassableFields, Set<MapField> inRangeSet) {
//        LOGGER.info("Current reference field: " + field.getId());
//        LOGGER.info("Current move cost: " + currentMoveCost);
        List<MapField> nbFields = getFieldNeighbours(field, mapFields);

        for (MapField nbField : nbFields) {
//            LOGGER.info("Current nb field: " + field.getId() + "-" + nbField.getId());
            int fieldMoveCost = 1;
            if (useMovementCost) {
                fieldMoveCost = nbField.getMoveCost();
            }
            if ((currentMoveCost + fieldMoveCost <= rangeToCheck)
                    && nbField.isPassable()
                    && !unpassableFields.contains(nbField)) {
                inRangeSet.add(nbField);
                searchFieldsInRange(nbField, useMovementCost, currentMoveCost + fieldMoveCost, rangeToCheck, mapFields, unpassableFields, inRangeSet);
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
