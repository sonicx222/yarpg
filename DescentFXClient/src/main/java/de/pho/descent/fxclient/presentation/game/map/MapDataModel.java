package de.pho.descent.fxclient.presentation.game.map;

import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.map.MapField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import javax.annotation.PostConstruct;

/**
 *
 * @author pho
 */
public class MapDataModel {

    public static final double FIELD_SIZE = 64d;
    private ObservableList<Node> mapStackPaneElements = FXCollections.observableArrayList();
    private Map<MapField, Rectangle> gridElements = new HashMap<>();
    private List<Rectangle> markedGridPaneElements = new ArrayList<>();
    private Pair<GameUnit, Button> activeUnit;

    @PostConstruct
    public void init() {

    }

    public ObservableList<Node> getMapStackPaneElements() {
        return mapStackPaneElements;
    }

    public void setMapStackPaneElements(ObservableList<Node> mapStackPaneElements) {
        this.mapStackPaneElements = mapStackPaneElements;
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

    public Pair<GameUnit, Button> getActiveUnit() {
        return activeUnit;
    }

    public void setActiveUnit(Pair<GameUnit, Button> activeUnit) {
        this.activeUnit = activeUnit;
    }

}
