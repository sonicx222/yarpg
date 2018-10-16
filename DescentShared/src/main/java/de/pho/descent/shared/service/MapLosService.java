package de.pho.descent.shared.service;

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

    public static boolean hasLineOfSight(MapField sourceField, MapField targetField, GameMap map) {
        return hasLineOfSight(sourceField.getxPos(), sourceField.getyPos(), targetField.getxPos(), targetField.getyPos(), map, null);
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
        int x = x0;
        int y = y0;
        int n = 1 + dx + dy;
        int x_inc = (x1 > x0) ? 1 : -1;
        int y_inc = (y1 > y0) ? 1 : -1;
        int error = dx - dy;
        dx *= 2;
        dy *= 2;

        for (; n > 0; --n) {
            if (x == x1 && y == y1) {
                break;
            }
            if ((x != x0 || y != y0)) { // starting point excluded
                LOG.info("Check LOS for (x=" + x + ", y=" + y + ")");
                MapField field = map.getField(x, y);
                if (field == null) {
                    // los hits wall
                    LOG.info("No LOS because of wall at x=" + x + " y=" + y);
                    return false;
                }
                if (fieldPath != null && !fieldPath.contains(field)) {
                    fieldPath.add(field);
                }
                if (!canPass(y, x, field)) {
                    LOG.info("No LOS because of blocking field at x=" + x + " y=" + y);
                    return false;
                }
            }

            if (error > 0) {
                x += x_inc;
                error -= dy;
            } else {
                y += y_inc;
                error += dx;
            }

            // error == 0: pass on edges
        }

        return true;
    }

    private static boolean canPass(int x, int y, MapField field) {
        return field.isPassable() && (field.getGameUnit() == null);
    }

}
