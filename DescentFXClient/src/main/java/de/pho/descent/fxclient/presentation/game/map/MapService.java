package de.pho.descent.fxclient.presentation.game.map;

import static de.pho.descent.fxclient.presentation.game.map.MapDataModel.FIELD_SIZE;
import de.pho.descent.fxclient.presentation.general.ActionService;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.shared.model.GameUnit;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.map.MapField;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.shared.model.token.Token;
import de.pho.descent.shared.model.token.TokenType;
import de.pho.descent.shared.service.MapRangeService;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class MapService {

    @Inject
    private GameDataModel gameDataModel;

    @Inject
    private MapDataModel mapDataModel;

    @Inject
    private ActionService actionService;

    @PostConstruct
    public void init() {
    }

    public void rebuildMap() {
        mapDataModel.getMapStackPaneElements().removeIf(n -> n instanceof Button);
        addHeroesToMap();
        addMonstersToMap();
        addTokensToMap();
        resetHighlightedMapFields();
    }

    public void applyHighlightEffect(Node node) {
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

    public void applyActiveUnitEffect(Node node) {
        // apply highlight effect
        int depth = 70;
        DropShadow borderGlow = new DropShadow();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.ALICEBLUE);
        borderGlow.setWidth(depth);
        borderGlow.setHeight(depth);

        node.setEffect(borderGlow);
    }

    public void resetEffect(Node node) {
        node.setEffect(null);
    }

    public void calcAndDisplayAllowedMovePositions(GameUnit unit) {
        // treat GameUnit locations as unpassable fields for range check
        List<MapField> unpassableFields = new ArrayList<>();

        // add hero locations
        for (GameHero hero : gameDataModel.getCurrentQuestEncounter().getGameHeroes()) {
            unpassableFields.addAll(hero.getCurrentLocation());
        }
        // add monster locations
        for (GameMonster monster : gameDataModel.getCurrentQuestEncounter().getGameMonsters()) {
            unpassableFields.addAll(monster.getCurrentLocation());
        }

        Set<MapField> fieldsInRange = MapRangeService.getFieldsInMovementRange(
                unit.getCurrentLocation(),
                unit.getMovementPoints(), gameDataModel.getCurrentQuestMap().getMapFields(), unpassableFields);

        // highlight grid fields
        mapDataModel.getMarkedGridPaneElements().clear();
        mapDataModel.getGridElements().keySet().stream().forEach(mapField -> {
            if (fieldsInRange.contains(mapField)) {
                Rectangle fieldArea = mapDataModel.getGridElements().get(mapField);
                fieldArea.setFill(Color.AQUA);
                fieldArea.setOpacity(0.4d);
                mapDataModel.getMarkedGridPaneElements().add(fieldArea);
            }
        });

    }

    public void calcAndDisplayAllowedAttackTargets(GameUnit unit) {

        // highlight grid fields
        mapDataModel.getMarkedGridPaneElements().clear();
        mapDataModel.getGridElements().keySet().stream().forEach(mapField -> {

            if (mapField.getGameUnit() != null && !Objects.equals(unit, mapField.getGameUnit())) {
                Rectangle fieldArea = mapDataModel.getGridElements().get(mapField);
                fieldArea.setFill(Color.AQUA);
                fieldArea.setOpacity(0.4d);
                mapDataModel.getMarkedGridPaneElements().add(fieldArea);
            }
        });

    }

    private void addHeroesToMap() {
        gameDataModel.getCurrentQuestEncounter().getGameHeroes().forEach((hero) -> {
            Button heroButton = createHeroFieldButton(hero);
            heroButton.setUserData(hero);
            if (hero.isActive()) {
                applyActiveUnitEffect(heroButton);
            } else if (hero.isKnockedOut()) {
                heroButton.setDisable(true);
            }
            heroButton.setTranslateX(hero.getCurrentLocation().get(0).getxPos() * FIELD_SIZE);
            heroButton.setTranslateY(hero.getCurrentLocation().get(0).getyPos() * FIELD_SIZE);

            heroButton.setOnMouseClicked(e -> {
                handleUnitClick((Button) e.getSource());
            });
            heroButton.setOnMouseEntered(e -> {
                handleOnUnitMouseEntered((Button) e.getSource());
            });
            heroButton.setOnMouseExited(e -> {
                handleOnUnitMouseExit((Button) e.getSource());
            });

            mapDataModel.getMapStackPaneElements().add(heroButton);
        });
    }

    private void addMonstersToMap() {
        gameDataModel.getCurrentQuestEncounter().getAliveMonsters().forEach((monster) -> {
            Button monsterButton = createMonsterFieldButton(monster);
            monsterButton.setUserData(monster);
            if (monster.isActive()) {
                applyActiveUnitEffect(monsterButton);
            }

            monsterButton.setOnMouseClicked(e -> {
                handleUnitClick((Button) e.getSource());
            });
            monsterButton.setOnMouseEntered(e -> {
                handleOnUnitMouseEntered((Button) e.getSource());
            });
            monsterButton.setOnMouseExited(e -> {
                handleOnUnitMouseExit((Button) e.getSource());
            });

            // make sure the northwest field of 2x2 monsters is used
            MapField northwestField = monster.getCurrentLocation()
                    .stream()
                    .min(Comparator.comparing(MapField::getxPos).thenComparing(MapField::getyPos))
                    .orElseThrow(NoSuchElementException::new);
            monsterButton.setTranslateX(northwestField.getxPos() * FIELD_SIZE);
            monsterButton.setTranslateY(northwestField.getyPos() * FIELD_SIZE);

            mapDataModel.getMapStackPaneElements().add(monsterButton);
        });
    }

    private void addTokensToMap() {
        for (int i = 0; i < gameDataModel.getCurrentQuestEncounter().getTokens().size(); i++) {
            Token token = gameDataModel.getCurrentQuestEncounter().getTokens().get(i);
            Button tokenButton = createTokenButton(token, i);

            tokenButton.setTranslateX(token.getCurrentLocation().getxPos() * FIELD_SIZE);
            tokenButton.setTranslateY(token.getCurrentLocation().getyPos() * FIELD_SIZE);

            mapDataModel.getMapStackPaneElements().add(tokenButton);
        }
    }

    private void handleUnitClick(Button button) {
        if (gameDataModel.getCurrentAction() != null) {
            switch (gameDataModel.getCurrentAction()) {
                case ATTACK:
                    actionService.handleAttack(button);
                    rebuildMap();
                    break;
            }
        } else {
//                    Button b = (Button) e.getSource();
//                    handleHeroSelected(b);
        }
    }

    private void handleOnUnitMouseEntered(Button button) {
        if (gameDataModel.getCurrentAction() != null) {
            GameUnit activeUnit = gameDataModel.getCurrentQuestEncounter().getActiveHero() == null
                    ? gameDataModel.getCurrentQuestEncounter().getActiveMonster()
                    : gameDataModel.getCurrentQuestEncounter().getActiveHero();
            if (!Objects.equals(activeUnit, (GameUnit) button.getUserData())) {
                switch (gameDataModel.getCurrentAction()) {
                    case ATTACK:
                        applyHighlightEffect(button);
                        break;
                }
            }
        }
    }

    private void handleOnUnitMouseExit(Button button) {
        if (gameDataModel.getCurrentAction() != null) {
            GameUnit activeUnit = gameDataModel.getCurrentQuestEncounter().getActiveHero() == null
                    ? gameDataModel.getCurrentQuestEncounter().getActiveMonster()
                    : gameDataModel.getCurrentQuestEncounter().getActiveHero();
            if (!Objects.equals(activeUnit, (GameUnit) button.getUserData())) {
                switch (gameDataModel.getCurrentAction()) {
                    case ATTACK:
                        resetEffect(button);
                        break;
                }
            }
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
        sb.append(System.lineSeparator()).append(hero.getActions())
                .append(" turns left");
        heroButton.setTooltip(new Tooltip(sb.toString()));

        if (hero.isActive()) {
            mapDataModel.setActiveUnit(new Pair<>(hero, heroButton));
        }

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

        if (monster.isActive()) {
            mapDataModel.setActiveUnit(new Pair<>(monster, monsterButton));
        }

        return monsterButton;
    }

    private Button createTokenButton(Token token, int number) {
        StringBuilder ressourcePathToken = new StringBuilder("/img/token/");
        ressourcePathToken.append(token.getType().getImageName());
        if (token.getType() == TokenType.SEARCH) {
            ressourcePathToken.append("_").append(number + 1);
        }
        ressourcePathToken.append(gameDataModel.getImageSuffix());
        InputStream isToken = getClass().getResourceAsStream(ressourcePathToken.toString());

        BackgroundImage backgroundImage = new BackgroundImage(new Image(isToken),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);

        Button tokenButton = new Button();
        tokenButton.setPrefSize(FIELD_SIZE, FIELD_SIZE);
        tokenButton.setBackground(background);

        return tokenButton;
    }

    public void resetHighlightedMapFields() {
        mapDataModel.getMarkedGridPaneElements().stream().forEach(item -> {
            item.setFill(Color.TRANSPARENT);
        });
        mapDataModel.getMarkedGridPaneElements().clear();
//        gridElements.keySet().stream().forEach(mapField -> {
//            Rectangle fieldArea = gridElements.get(mapField);
//            fieldArea.setFill(Color.TRANSPARENT);
//            fieldArea.setOpacity(0.0);
//        });
    }
}
