package de.pho.descent.fxclient.presentation.game.map;

import static de.pho.descent.fxclient.presentation.game.map.MapDataModel.FIELD_SIZE;
import de.pho.descent.fxclient.presentation.general.ActionService;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.general.GameService;
import de.pho.descent.shared.dto.WsGameMap;
import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.map.MapField;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class MapPresenter implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(MapPresenter.class.getName());

    //* holding background image, gridpane and game unit nodes */
    @FXML
    private StackPane mapStackPane;

    @FXML
    private ImageView mapImageView;

    @FXML
    private GridPane mapGridPane;

    @Inject
    private GameDataModel gameDataModel;

    @Inject
    private GameService gameService;

    @Inject
    private MapDataModel mapDataModel;

    @Inject
    private MapService mapService;

    @Inject
    private ActionService actionService;

    private Node selectedNode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // load active quest encounter of current campaign
        gameService.loadQuestEncounter();
        // load map data
        gameService.loadQuestMap();

        setupMap();
        mapService.rebuildMap();
    }

    private void setupMap() {
        // set map background image
        mapImageView.setImage(new Image(getClass().getResourceAsStream(gameDataModel.getCurrentQuestMap().getImagePath())));

        // resize stack pane to fit map background image
        mapStackPane.setPrefHeight(mapImageView.getFitHeight());
        mapStackPane.setPrefWidth(mapImageView.getFitWidth());
        mapDataModel.setMapStackPaneElements(mapStackPane.getChildren());

        // setup mapfield nodes
        setupMapGridPane();
    }

    private void setupMapGridPane() {
        WsGameMap map = gameDataModel.getCurrentQuestMap();
        mapDataModel.getGridElements().clear();
        mapGridPane.setPrefHeight(mapImageView.getFitHeight());
        mapGridPane.setPrefWidth(mapImageView.getFitWidth());

        // column size
        mapGridPane.getColumnConstraints().clear();
        for (int x = 0; x < map.getGridXSize(); x++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setMinWidth(FIELD_SIZE);
            col.setMaxWidth(FIELD_SIZE);
            mapGridPane.getColumnConstraints().add(col);
        }
        // row size
        mapGridPane.getRowConstraints().clear();
        for (int y = 0; y < map.getGridYSize(); y++) {
            RowConstraints row = new RowConstraints();
            row.setMinHeight(FIELD_SIZE);
            row.setMaxHeight(FIELD_SIZE);
            mapGridPane.getRowConstraints().add(row);
        }

        // fill cells
        for (MapField field : map.getMapFields()) {
            Rectangle fieldArea = new Rectangle(FIELD_SIZE, FIELD_SIZE);
            fieldArea.setUserData(field);
            fieldArea.setFill(Color.TRANSPARENT);
            fieldArea.setOnMouseEntered(e -> {
                if (gameDataModel.getCurrentAction() != null) {
                    Rectangle r = (Rectangle) e.getSource();
                    r.setStroke(Color.DARKVIOLET);
                    r.setStrokeWidth(3d);
                }
            });
            fieldArea.setOnMouseExited(e -> {
                Rectangle r = (Rectangle) e.getSource();
                r.setStroke(null);
            });
            fieldArea.setOnMouseClicked(e -> {
                if (gameDataModel.getCurrentAction() != null) {
                    switch (gameDataModel.getCurrentAction()) {
                        case MOVE:
                            actionService.handleMove((Rectangle) e.getSource());
                            mapService.rebuildMap();
                            break;
                    }
                }
            });

            // tooltip move cost
            StringBuilder sb = new StringBuilder();
            sb.append(field.getId()).append(System.lineSeparator())
                    .append("Move cost: ").append(field.getMoveCost());
            Tooltip t = new Tooltip(sb.toString());
            Tooltip.install(fieldArea, t);

            // show field id
//            StackPane fieldPane = new StackPane(fieldArea);
//            fieldPane.getChildren().add(new Text(String.valueOf(field.getId())));
//            fieldPane.setAlignment(Pos.CENTER);
//            mapGridPane.add(fieldPane, field.getxPos(), field.getyPos());
//            mapGridPane.setHalignment(fieldPane, HPos.CENTER);
            mapGridPane.add(fieldArea, field.getxPos(), field.getyPos());
            mapDataModel.getGridElements().put(field, fieldArea);
        }
    }

    private void handleMonsterSelected(Button button) {

        // remove effect from previous selected node
        if (selectedNode != null) {
            selectedNode.setEffect(null);
        }

        // switch current selected node
        selectedNode = button;

        // apply highlight effect
        mapService.applyHighlightEffect(selectedNode);
        LOGGER.info("Selected Monster: " + ((GameUnit) button.getUserData()).getName());
    }

    private void showMovementCost() {
        WsGameMap map = gameDataModel.getCurrentQuestMap();
        for (MapField field : map.getMapFields()) {
            Label label = new Label(String.valueOf(field.getMoveCost()));
            label.setTextFill(Color.WHITE);
            label.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
            label.setTooltip(new Tooltip(field.getTileGroup().getName()));
            mapGridPane.add(label, field.getxPos(), field.getyPos());
            mapGridPane.setHalignment(label, HPos.CENTER);
        }
    }
}
