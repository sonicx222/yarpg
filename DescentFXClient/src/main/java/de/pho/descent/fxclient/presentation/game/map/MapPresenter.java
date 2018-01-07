package de.pho.descent.fxclient.presentation.game.map;

import static de.pho.descent.fxclient.MainApp.showError;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.ActionClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import static de.pho.descent.fxclient.presentation.game.map.MapDataModel.FIELD_SIZE;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.general.GameService;
import de.pho.descent.fxclient.presentation.message.MessageService;
import de.pho.descent.shared.dto.WsAction;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsGameMap;
import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.PlaySide;
import de.pho.descent.shared.model.action.ActionType;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.map.MapField;
import de.pho.descent.shared.model.monster.GameMonster;
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
    private Credentials credentials;

    @Inject
    private GameDataModel gameDataModel;

    @Inject
    private GameService gameService;

    @Inject
    private MessageService messageService;

    @Inject
    private MapDataModel mapDataModel;

    @Inject
    private MapService mapService;

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
                            handleMove((Rectangle) e.getSource());
                            break;
                        case ATTACK:
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

    private void handleMove(Rectangle r) {
        WsCampaign updatedCampaign = null;
        WsAction moveAction = new WsAction();
        moveAction.setCampaignId(gameDataModel.getCurrentCampaign().getId());
        moveAction.setQuestEncounterId(gameDataModel.getCurrentQuestEncounter().getId());
        moveAction.setType(ActionType.MOVE);
        moveAction.setSourceField(mapDataModel.getActiveUnit().getKey().getCurrentLocation());
        moveAction.setTargetField((MapField) r.getUserData());

        if (gameDataModel.getCurrentQuestEncounter().getCurrentTurn() == PlaySide.HEROES) {
            moveAction.setSourceHero((GameHero) mapDataModel.getActiveUnit().getKey());
            moveAction.setHeroAction(true);
        } else {
            moveAction.setSourceMonster((GameMonster) mapDataModel.getActiveUnit().getKey());
            moveAction.setHeroAction(false);
        }

        try {
            updatedCampaign = ActionClient.sendAction(credentials, moveAction);
        } catch (ServerException ex) {
            showError(ex);
        }
        if (updatedCampaign != null) {
            // update
            gameService.updateGameState(updatedCampaign);

            // UI controls
//            gameService.enableOtherButtons();
            gameDataModel.getMoveButton().setText("Move");
            gameDataModel.setCurrentAction(null);
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
