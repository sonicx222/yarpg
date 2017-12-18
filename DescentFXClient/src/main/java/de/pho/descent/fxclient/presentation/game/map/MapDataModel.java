package de.pho.descent.fxclient.presentation.game.map;

import de.pho.descent.shared.model.map.MapField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.shape.Rectangle;
import javax.annotation.PostConstruct;

/**
 *
 * @author pho
 */
public class MapDataModel {

    private Map<MapField, Rectangle> gridElements = new HashMap<>();
    private List<Rectangle> markedGridPaneElements = new ArrayList<>();

    @PostConstruct
    public void init() {

    }

    public Map<MapField, Rectangle> getGridElements() {
        return gridElements;
    }

    public void setGridElements(Map<MapField, Rectangle> gridElements) {
        this.gridElements = gridElements;
    }

    public List<Rectangle> getMarkedGridPaneElements() {
        return markedGridPaneElements;
    }

    public void setMarkedGridPaneElements(List<Rectangle> markedGridPaneElements) {
        this.markedGridPaneElements = markedGridPaneElements;
    }
}
