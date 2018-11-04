package de.pho.descent.shared.service;

import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.map.GameMap;
import de.pho.descent.shared.model.map.MapField;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author pho
 */
public class MapLosService {

    private static final Logger LOG = Logger.getLogger(MapLosService.class.getName());

    public static boolean hasLineOfSight(GameUnit sourceUnit, GameUnit targetUnit, GameMap map) {
        for (MapField sourceField : sourceUnit.getCurrentLocation()) {
            for (MapField targetField : targetUnit.getCurrentLocation()) {
                if (MapLosService.hasLineOfSight(sourceField, targetField, map)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean hasLineOfSight(MapField sourceField, MapField targetField, GameMap map) {
        return hasLineOfSight(sourceField.getxPos(), sourceField.getyPos(), targetField.getxPos(), targetField.getyPos(), map, null);
    }

    public static List<MapField> getLOSPath(GameUnit sourceUnit, GameUnit targetUnit, GameMap map) {
        List<MapField> fieldPath = new ArrayList<>();

        for (MapField sourceField : sourceUnit.getCurrentLocation()) {
            for (MapField targetField : targetUnit.getCurrentLocation()) {
                fieldPath.addAll(MapLosService.getLOSPath(sourceField, targetField, map));
            }
        }

        return fieldPath;
    }

    public static List<MapField> getLOSPath(MapField sourceField, MapField targetField, GameMap map) {
        List<MapField> fieldPath = new ArrayList<>();

        hasLineOfSight(sourceField.getxPos(), sourceField.getyPos(), targetField.getxPos(), targetField.getyPos(), map, fieldPath);

        return fieldPath;
    }

    public static boolean hasLineOfSight(int x0, int y0, int x1, int y1, GameMap map, List<MapField> fieldPath) {
        LOG.info("Check LOS from x=" + x0 + " y=" + y0 + ", to x=" + x1 + " y=" + y1);

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        // error determines in which direction (x/y) to correct the line to reach the target coords
        int error = dx - dy;
        
        // current position within the algorithm
        int xTmp = x0;
        int yTmp = y0;
        
        // max steps until target coords
        // required not to "overshoot" pastt the target coords
        int n = 1 + dx + dy;
        
        // setting the raytrace direction (quadrant of coord system)
        int x_inc = (x1 > x0) ? 1 : -1;
        int y_inc = (y1 > y0) ? 1 : -1;
        
        dx *= 2;
        dy *= 2;

        for (; n > 0; --n) {
            // target coords reached
            if (xTmp == x1 && yTmp == y1) {
                LOG.info("LOS to: (x=" + xTmp + ", y=" + yTmp + ") works!");
                break;
            }
            if ((xTmp != x0 || yTmp != y0)) { // starting point excluded
                LOG.info("Check LOS for (x=" + xTmp + ", y=" + yTmp + ")");
                MapField field = map.getField(xTmp, yTmp);
                if (field == null) {
                    // los hits wall
                    LOG.info("No LOS because of wall at x=" + xTmp + " y=" + yTmp);
                    return false;
                }
                if (fieldPath != null && !fieldPath.contains(field)) {
                    fieldPath.add(field);
                }
                if (!canPass(yTmp, xTmp, field)) {
                    LOG.info("No LOS because of blocking field at x=" + xTmp + " y=" + yTmp);
                    return false;
                }
            }

            // calc in which axis (x/y) to correct the drawn raytrace line
            if (error > 0) {
                xTmp += x_inc;
                error -= dy;
            } else if (error == 0) {
                // perfectly on track
                // go diagonally through grid
                xTmp += x_inc;
                error -= dy;
                yTmp += y_inc;
                error += dx;
                n--; // skip one loop step since we did two (x&y) steps
            } else {
                yTmp += y_inc;
                error += dx;
            }
        }

        return true;
    }

    private static boolean canPass(int x, int y, MapField field) {
        return field.isPassable() && (field.getGameUnit() == null);
    }

}
