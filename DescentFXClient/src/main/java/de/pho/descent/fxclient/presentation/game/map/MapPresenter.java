package de.pho.descent.fxclient.presentation.game.map;

import static de.pho.descent.fxclient.MainApp.showError;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.MapClient;
import de.pho.descent.fxclient.business.ws.QuestClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.shared.dto.WsGameMap;
import de.pho.descent.shared.dto.WsQuestEncounter;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.map.MapField;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.shared.model.token.Token;
import de.pho.descent.shared.model.token.TokenType;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class MapPresenter implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(MapPresenter.class.getName());
    private static final double FIELD_SIZE = 64d;

    @FXML
    private StackPane mapStackPane;

    @FXML
    private ImageView mapImageView;

    @FXML
    private GridPane mapGridPane;

    @FXML
    private Button moveButton;

    @Inject
    private Credentials credentials;

    @Inject
    private GameDataModel gameDataModel;

    @Inject
    private MapDataModel mapDataModel;

    @Inject
    private MapService mapService;

    private LinearGradient gradientMenuItem;

    private final String IMAGE_SUFFIX = ".png";

    private Node selectedNode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        gradientMenuItem = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.DARKVIOLET),
            new Stop(0.1, Color.BLACK),
            new Stop(0.9, Color.BLACK),
            new Stop(1, Color.DARKVIOLET)
        });

        // load active quest encounter of current campaign
        loadQuestEncounter();
        loadQuestMap();
//        showMovementCost();
        mapImageView.setImage(new Image(getClass().getResourceAsStream(gameDataModel.getCurrentQuestMap().getImagePath())));
        setupGridPane();
        mapStackPane.setPrefHeight(mapImageView.getFitHeight());
        mapStackPane.setPrefWidth(mapImageView.getFitWidth());

        addHeroesToMap();
        addMonstersToMap();
        addTokensToMap();

//        MapField testField = gameDataModel.getCurrentCampaign().getGameHeroes().stream()
//                    .filter(h -> h.isActive())
//                    .findAny().get().getCurrentLocation();
//        Set<MapField> fields = mapService.getFieldsInRange(testField, 3, gameDataModel.getCurrentQuestMap());
//        fields.stream().forEach(f -> LOGGER.info("Field ID: " + f.getId()));
    }

    private void loadQuestEncounter() {
        Objects.requireNonNull(gameDataModel.getCurrentCampaign());
        WsQuestEncounter wsQuestEncounter = null;
        try {
            wsQuestEncounter = QuestClient.getQuestEncounter(
                    credentials.getUsername(), credentials.getPassword(),
                    gameDataModel.getCurrentCampaign().getQuestEncounterId());
        } catch (ServerException ex) {
            showError(ex);
        }
        if (wsQuestEncounter != null) {
            gameDataModel.setCurrentQuestEncounter(wsQuestEncounter);
        }
    }

    private void loadQuestMap() {
        Objects.requireNonNull(gameDataModel.getCurrentQuestEncounter());
        WsGameMap wsGameMap = null;
        try {
            wsGameMap = MapClient.getQuestMap(
                    credentials.getUsername(), credentials.getPassword(),
                    gameDataModel.getCurrentQuestEncounter().getMapId());
        } catch (ServerException ex) {
            showError(ex);
        }
        if (wsGameMap != null) {
            gameDataModel.setCurrentQuestMap(wsGameMap);
        }
    }

    private void setupGridPane() {
        WsGameMap map = gameDataModel.getCurrentQuestMap();
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

        // style css
//        mapGridPane.getStyleClass().add("paneField");
        // fill cells
//        GameHero hero;
        for (MapField field : map.getMapFields()) {
            Rectangle fieldArea = new Rectangle(FIELD_SIZE, FIELD_SIZE);
            fieldArea.setUserData(field);
            fieldArea.setFill(Color.TRANSPARENT);
            fieldArea.setOnMouseEntered(e -> {
                Rectangle r = (Rectangle) e.getSource();
                r.setStroke(gradientMenuItem);
            });
            fieldArea.setOnMouseExited(e -> {
                Rectangle r = (Rectangle) e.getSource();
                r.setStroke(null);
            });
            // tooltip
            StringBuilder sb = new StringBuilder();
            sb.append(field.getId()).append(System.lineSeparator())
                    .append("Move cost: ").append(field.getMoveCost());
            Tooltip t = new Tooltip(sb.toString());
            Tooltip.install(fieldArea, t);

            StackPane fieldPane = new StackPane(fieldArea);
            fieldPane.getChildren().add(new Text(String.valueOf(field.getId())));
            fieldPane.setAlignment(Pos.CENTER);

            mapGridPane.add(fieldPane, field.getxPos(), field.getyPos());
            mapGridPane.setHalignment(fieldPane, HPos.CENTER);
            mapDataModel.getGridElements().put(field, fieldArea);
        }
    }

    private void addHeroesToMap() {
        gameDataModel.getCurrentCampaign().getGameHeroes().forEach((hero) -> {
            Button heroButton = createHeroFieldButton(hero);
            heroButton.setUserData(hero);
            if (hero.isActive()) {
                applyHighlightEffect(heroButton);
//            heroButton.setOnMouseClicked(e -> {
//                Button b = (Button) e.getSource();
//                handleHeroSelected(b);
//            });
            }
            heroButton.setTranslateX(hero.getCurrentLocation().getxPos() * FIELD_SIZE);
            heroButton.setTranslateY(hero.getCurrentLocation().getyPos() * FIELD_SIZE);

            mapStackPane.getChildren().add(heroButton);
        });
    }

    private void addMonstersToMap() {
        gameDataModel.getCurrentQuestEncounter().getGameMonsters().forEach((monster) -> {
            Button monsterButton = createMonsterFieldButton(monster);
            monsterButton.setUserData(monster);
//            monsterButton.setOnMouseClicked(e -> {
//                Button b = (Button) e.getSource();
//                handleHeroSelected(b);
//            });
            monsterButton.setTranslateX(monster.getCurrentLocation().getxPos() * FIELD_SIZE);
            monsterButton.setTranslateY(monster.getCurrentLocation().getyPos() * FIELD_SIZE);

            mapStackPane.getChildren().add(monsterButton);
        });
    }

    private void addTokensToMap() {
        for (int i = 0; i < gameDataModel.getCurrentQuestEncounter().getTokens().size(); i++) {
            Token token = gameDataModel.getCurrentQuestEncounter().getTokens().get(i);
            ImageView tokenImageView = createTokenImageView(token, i);

            tokenImageView.setTranslateX(token.getCurrentLocation().getxPos() * FIELD_SIZE);
            tokenImageView.setTranslateY(token.getCurrentLocation().getyPos() * FIELD_SIZE);

            mapStackPane.getChildren().add(tokenImageView);
        }
    }

    private Button createHeroFieldButton(GameHero hero) {
        StringBuilder ressourcePathHero = new StringBuilder("/img/heroes/");
        ressourcePathHero.append(hero.getHeroTemplate().getImageName());
        ressourcePathHero.append("_field.png");
        InputStream isHero = getClass().getResourceAsStream(ressourcePathHero.toString());

        BackgroundImage backgroundImage = new BackgroundImage(new Image(isHero),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);

        Button heroButton = new Button();
        heroButton.setPrefSize(FIELD_SIZE, FIELD_SIZE);
        heroButton.setBackground(background);

        // tooltip
        StringBuilder sb = new StringBuilder(hero.getName());
        sb.append(System.lineSeparator()).append(hero.getCurrentLife())
                .append("/").append(hero.getTotalLife()).append(" hp");
        sb.append(System.lineSeparator()).append(hero.getTurnsLeft())
                .append(" turns left");
        heroButton.setTooltip(new Tooltip(sb.toString()));

        return heroButton;
    }

    private Button createMonsterFieldButton(GameMonster monster) {
        StringBuilder ressourcePathMonster = new StringBuilder("/img/monster/");
        ressourcePathMonster.append(monster.getMonsterTemplate().getImageName());
        ressourcePathMonster.append("_field.png");
        InputStream isMonster = getClass().getResourceAsStream(ressourcePathMonster.toString());

        BackgroundImage backgroundImage = new BackgroundImage(new Image(isMonster),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);

        Button monsterButton = new Button();
        if (monster.getMonsterTemplate().getFieldSize() == 1) {
            monsterButton.setPrefSize(FIELD_SIZE, FIELD_SIZE);
        } else {
            monsterButton.setPrefSize(2d * FIELD_SIZE, 2d * FIELD_SIZE);
        }
        monsterButton.setBackground(background);

        // tooltip
        StringBuilder sb = new StringBuilder(monster.getName());
        sb.append(System.lineSeparator()).append(monster.getMovementPoints()).append(" speed");
        sb.append(System.lineSeparator()).append(monster.getCurrentLife())
                .append("/").append(monster.getTotalLife()).append(" hp");
        monsterButton.setTooltip(new Tooltip(sb.toString()));

        return monsterButton;
    }

    private ImageView createTokenImageView(Token token, int number) {
        StringBuilder ressourcePathToken = new StringBuilder("/img/token/");
        ressourcePathToken.append(token.getType().getImageName());
        if (token.getType() == TokenType.SEARCH) {
            ressourcePathToken.append("_").append(number + 1);
        }
        ressourcePathToken.append(IMAGE_SUFFIX);
        InputStream isToken = getClass().getResourceAsStream(ressourcePathToken.toString());

        ImageView view = new ImageView(new Image(isToken));
        view.setFitHeight(FIELD_SIZE);
        view.setFitWidth(FIELD_SIZE);

        return view;
    }

    private void applyHighlightEffect(Node node) {
        // apply highlight effect
        int depth = 70;
        DropShadow borderGlow = new DropShadow();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.RED);
        borderGlow.setWidth(depth);
        borderGlow.setHeight(depth);

        node.setEffect(borderGlow);
    }

    private void handleHeroSelected(Button button) {

        // remove effect from previous selected node
        if (selectedNode != null) {
            selectedNode.setEffect(null);
        }

        // switch current selected node
        selectedNode = button;

        // apply highlight effect
        applyHighlightEffect(selectedNode);
        LOGGER.info("Selected Hero: " + ((GameHero) button.getUserData()).getName());
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
