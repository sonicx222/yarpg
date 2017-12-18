package de.pho.descent.fxclient.presentation.game.hero;

import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.presentation.campaignselection.CampaignSelectionView;
import de.pho.descent.fxclient.presentation.game.map.MapDataModel;
import de.pho.descent.fxclient.presentation.game.map.MapService;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.general.GameService;
import de.pho.descent.fxclient.presentation.message.MessageService;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.map.MapField;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class HeroGamePresenter implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(HeroGamePresenter.class.getName());

    /**
     * Hero Data & Sheet
     */
    @FXML
    private ImageView heroSheetView;
    @FXML
    private ImageView heroImageView;
    @FXML
    private Label labelName;
    @FXML
    private Label labelSpeed;
    @FXML
    private Label labelHealth;
    @FXML
    private Label labelStamina;
    @FXML
    private Label labelMight;
    @FXML
    private Label labelKnowledge;
    @FXML
    private Label labelWillpower;
    @FXML
    private Label labelAwareness;
    @FXML
    private Label labelHeroAbility;
    @FXML
    private Label labelHeroicFeat;

    /**
     * Hero Action Controls
     */
    @FXML
    private Button moveButton;

    @FXML
    private Button attackButton;

    /**
     * Game Data
     */
    @Inject
    private Credentials credentials;
    @Inject
    private GameDataModel gameDataModel;
    @Inject
    private GameService gameService;
    @Inject
    private MapDataModel mapDataModel;
    @Inject
    private MapService mapService;
    @Inject
    private MessageService messageService;

    private String buttonMoveText;
    private String buttonAttackText;
    private String buttonCancelText;

    private final String IMAGE_SUFFIX = ".png";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        buttonMoveText = resources.getString("button.hero.game.move.toggle");
        buttonAttackText = resources.getString("button.hero.game.attack.toggle");
        buttonCancelText = resources.getString("button.hero.game.cancel.toggle");

        // load active quest encounter of current campaign
        if (gameDataModel.getCurrentQuestEncounter() == null) {
            gameService.loadQuestEncounter();
        }

        setupHeroSheet();
        checkPlayersTurn();

    }

    private void checkPlayersTurn() {
        // disable controls when it's not players turn
        if (gameDataModel.getCurrentQuestEncounter().getActiveHero().getPlayedBy().getUsername().equals(credentials.getUsername())) {
            moveButton.setDisable(false);
            attackButton.setDisable(false);
        } else {
            moveButton.setDisable(true);
            attackButton.setDisable(true);
        }
    }

    private void setupHeroSheet() {
        GameHero hero = gameDataModel.getCurrentCampaign().getGameHeroes().stream()
                .filter(h -> h.getPlayedBy().getUsername().equals(credentials.getUsername()))
                .findAny()
                .orElse(null);

        labelName.setText(hero.getName());
        labelSpeed.setText(String.valueOf(hero.getMovementPoints() - hero.getMovementSpent()));
        labelHealth.setText(String.valueOf(hero.getCurrentLife()));
        labelStamina.setText(String.valueOf(hero.getExhaustion()));
        labelMight.setText(String.valueOf(hero.getMight()));
        labelKnowledge.setText(String.valueOf(hero.getKnowledge()));
        labelWillpower.setText(String.valueOf(hero.getWillpower()));
        labelAwareness.setText(String.valueOf(hero.getAwareness()));
        labelHeroAbility.setText(hero.getHeroTemplate().getHeroAbilityText());
        labelHeroicFeat.setText(hero.getHeroTemplate().getHeroicFeatText());

        // sheet
        StringBuilder ressourcePathSheet = new StringBuilder("/img/layout/hero_sheet_");
        ressourcePathSheet.append(hero.getHeroTemplate().getArchetype().name().toLowerCase());
        ressourcePathSheet.append(IMAGE_SUFFIX);
        InputStream isSheet = getClass().getResourceAsStream(ressourcePathSheet.toString());
        if (isSheet != null) {
            heroSheetView.setImage(new Image(isSheet));
        }

        // hero image
        StringBuilder ressourcePathHero = new StringBuilder("/img/heroes/");
        ressourcePathHero.append(hero.getHeroTemplate().getImageName());
        ressourcePathHero.append(IMAGE_SUFFIX);
        InputStream isHero = getClass().getResourceAsStream(ressourcePathHero.toString());
        if (isHero != null) {
            heroImageView.setImage(new Image(isHero));
        }

    }

    @FXML
    public void handleRefresh(MouseEvent event) {
        LOGGER.info("HeroGamePresenter: handleRefresh()");
        // TODO: load updated campaign
        // TODO: load updated quest
        checkPlayersTurn();
        messageService.updateCampaignMessages();
    }

    @FXML
    public void handleNavigationBack(MouseEvent event) {
        LOGGER.info("HeroGamePresenter: handleNavigationBack()");
        switchFullscreenScene(event, new CampaignSelectionView());
    }

    @FXML
    public void handleToggleMove(MouseEvent event) {
        if (moveButton.getText().equalsIgnoreCase(buttonMoveText)) {
            handleMove(event);
        } else {
            handleCancel(event);
        }
    }

    private void handleMove(MouseEvent event) {
        LOGGER.info("HeroGamePresenter: handleMove()");
        disableOtherButtons(event);
        moveButton.setText(buttonCancelText);
        calcAndDisplayAllowedPositions();
    }

    private void handleCancel(MouseEvent event) {
        LOGGER.info("HeroGamePresenter: handleCancel()");
        enableOtherButtons(event);
        resetFields();
        moveButton.setText(buttonMoveText);
    }

    private void resetFields() {
        mapDataModel.getMarkedGridPaneElements().stream().forEach(item -> {
            item.setFill(Color.TRANSPARENT);
        });
        mapDataModel.getMarkedGridPaneElements().clear();
    }

    private void enableOtherButtons(MouseEvent event) {
        Button b = (Button) event.getSource();

        if (this.moveButton.equals(b)) {
            attackButton.setDisable(false);
        } else if (this.attackButton.equals(b)) {
            moveButton.setDisable(false);
        }
    }

    private void disableOtherButtons(MouseEvent event) {
        Button b = (Button) event.getSource();

        if (this.moveButton.equals(b)) {
            attackButton.setDisable(true);
        } else if (this.attackButton.equals(b)) {
            moveButton.setDisable(true);
        }
    }

    private void calcAndDisplayAllowedPositions() {
        GameHero activeHero = gameDataModel.getCurrentQuestEncounter().getActiveHero();
        List<MapField> heroFields = gameDataModel.getCurrentCampaign().getGameHeroes().stream()
                .map(GameHero::getCurrentLocation).collect(Collectors.toList());
        Set<MapField> fieldsInRange = mapService.getFieldsInRange(
                activeHero.getCurrentLocation(),
                activeHero.getMovementPoints(), gameDataModel.getCurrentQuestMap(), heroFields);

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

    @FXML
    public void handleOnMouseEntered(MouseEvent event) {
        gameService.handleOnMouseEntered(event);
    }

    @FXML
    public void handleOnMouseExited(MouseEvent event) {
        gameService.handleOnMouseExited(event);
    }

    @FXML
    public void handleOnMousePressed(MouseEvent event) {
        gameService.handleOnMousePressed(event);
    }

    @FXML
    public void handleOnMouseReleased(MouseEvent event) {
        gameService.handleOnMouseReleased(event);
    }
}
