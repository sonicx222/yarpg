package de.pho.descent.fxclient.presentation.game.hero;

import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.presentation.campaignselection.CampaignSelectionView;
import de.pho.descent.fxclient.presentation.game.map.MapService;

import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.general.GameService;
import de.pho.descent.shared.model.action.ActionType;
import de.pho.descent.shared.model.hero.GameHero;
import de.pho.descent.shared.model.hero.skill.HeroSkill;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class HeroGamePresenter implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(HeroGamePresenter.class.getName());

    @FXML
    private VBox prologBox;

    @FXML
    private Label labelRound;

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
    @FXML
    private GridPane skillGridPane;
    @FXML
    private ImageView heroSkill1View;
    @FXML
    private ImageView heroSkill2View;
    @FXML
    private ImageView heroSkill3View;
    @FXML
    private ImageView heroSkill4View;
    @FXML
    private ImageView heroSkill5View;
    @FXML
    private ImageView heroSkill6View;
    @FXML
    private ImageView itemWeapon1View;
    @FXML
    private ImageView itemWeapon2View;
    @FXML
    private ImageView itemShieldView;
    @FXML
    private ImageView itemArmorView;
    @FXML
    private ImageView itemTrinket1View;
    @FXML
    private ImageView itemTrinket2View;

    /**
     * Hero Action Controls
     */
    @FXML
    private Button moveButton;

    @FXML
    private Button attackButton;

    @FXML
    private Button turnButton;

    @FXML
    private Label labelActionsLeft;

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
    private HeroGameService heroGameService;
    @Inject
    private HeroGameModel heroGameModel;
    @Inject
    private MapService mapService;

    private String buttonMoveText;
    private String buttonAttackText;
    private String buttonCancelText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // load active quest encounter of current campaign
        if (gameDataModel.getCurrentQuestEncounter() == null) {
            gameService.loadQuestEncounter();
        }

        setupHeroSheet();
        setupHeroSkills();
        setupHeroEquipment();
        setupUIControls(resources);
        heroGameService.updateUIControls();

        // update hero stats
        heroGameService.updateHeroStats();

        // show prolog
        if (gameDataModel.getCurrentQuestEncounter().getRound() == 1) {
            prologBox.setVisible(true);
        }
    }

    private void setupUIControls(ResourceBundle resources) {
        buttonMoveText = resources.getString("button.hero.game.move.toggle");
        buttonAttackText = resources.getString("button.hero.game.attack.toggle");
        buttonCancelText = resources.getString("button.hero.game.cancel.toggle");

        gameDataModel.setPrologBoxVisible(prologBox.visibleProperty());
        gameDataModel.setRound(labelRound.textProperty());

        gameDataModel.setMoveButton(moveButton);
        gameDataModel.setAttackButton(attackButton);
        gameDataModel.setTurnButton(turnButton);
    }

    private void setupHeroSheet() {
        GameHero hero = gameDataModel.getCurrentQuestEncounter().getGameHeroes().stream()
                .filter(h -> h.getPlayedBy().getUsername().equals(credentials.getUsername()))
                .findAny()
                .orElse(null);

        labelName.setText(hero.getName());
        labelMight.setText(String.valueOf(hero.getMight()));
        labelKnowledge.setText(String.valueOf(hero.getKnowledge()));
        labelWillpower.setText(String.valueOf(hero.getWillpower()));
        labelAwareness.setText(String.valueOf(hero.getAwareness()));
        labelHeroAbility.setText(hero.getHeroTemplate().getHeroAbilityText());
        labelHeroicFeat.setText(hero.getHeroTemplate().getHeroicFeatText());

        heroGameModel.setHealth(labelHealth.textProperty());
        heroGameModel.setMovementLeft(labelSpeed.textProperty());
        heroGameModel.setExhaustion(labelStamina.textProperty());
        heroGameModel.setActionsLeft(labelActionsLeft.textProperty());

        // sheet
        StringBuilder ressourcePathSheet = new StringBuilder("/img/layout/hero_sheet_");
        ressourcePathSheet.append(hero.getHeroTemplate().getArchetype().name().toLowerCase());
        ressourcePathSheet.append(gameDataModel.getImageSuffix());
        InputStream isSheet = getClass().getResourceAsStream(ressourcePathSheet.toString());
        if (isSheet != null) {
            heroSheetView.setImage(new Image(isSheet));
        }

        // hero image
        StringBuilder ressourcePathHero = new StringBuilder("/img/heroes/");
        ressourcePathHero.append(hero.getHeroTemplate().getImageName());
        ressourcePathHero.append(gameDataModel.getImageSuffix());
        InputStream isHero = getClass().getResourceAsStream(ressourcePathHero.toString());
        if (isHero != null) {
            heroImageView.setImage(new Image(isHero));
        }
    }

    private void setupHeroSkills() {
        GameHero hero = gameDataModel.getCurrentQuestEncounter().getGameHeroes().stream()
                .filter(h -> h.getPlayedBy().getUsername().equals(credentials.getUsername()))
                .findAny()
                .orElse(null);

        if (!hero.getAllSkills().isEmpty()) {
            int index = 0;
            for (HeroSkill skill : hero.getAllSkills()) {
                StringBuilder ressourcePathHeroSkill = new StringBuilder("/img/skills/");
                ressourcePathHeroSkill.append(skill.getImagePath());
                ressourcePathHeroSkill.append(gameDataModel.getImageSuffix());
                InputStream streamSkill = getClass().getResourceAsStream(ressourcePathHeroSkill.toString());
                if (streamSkill != null) {
                    Image skillImage = new Image(streamSkill);
                    switch (index) {
                        case 0:
                            heroSkill1View.setImage(skillImage);
                            break;
                        case 1:
                            heroSkill2View.setImage(skillImage);
                            break;
                        case 2:
                            heroSkill3View.setImage(skillImage);
                            break;
                        case 3:
                            heroSkill4View.setImage(skillImage);
                            break;
                        case 4:
                            heroSkill5View.setImage(skillImage);
                            break;
                        case 5:
                            heroSkill6View.setImage(skillImage);
                            break;
                    }
                }
                index++;
            }
        }
    }

    private void setupHeroEquipment() {
        GameHero hero = gameDataModel.getCurrentQuestEncounter().getGameHeroes().stream()
                .filter(h -> h.getPlayedBy().getUsername().equals(credentials.getUsername()))
                .findAny()
                .orElse(null);

        if (hero.getWeapon() != null) {
            StringBuilder ressourcePathEquip = new StringBuilder("/img/items/");
            ressourcePathEquip.append(hero.getWeapon().getImagePath());
            ressourcePathEquip.append(gameDataModel.getImageSuffix());
            InputStream streamItem = getClass().getResourceAsStream(ressourcePathEquip.toString());
            itemWeapon1View.setImage(new Image(streamItem));
        }
        if (hero.getShield() != null) {
            StringBuilder ressourcePathEquip = new StringBuilder("/img/items/");
            ressourcePathEquip.append(hero.getShield().getImagePath());
            ressourcePathEquip.append(gameDataModel.getImageSuffix());
            InputStream streamItem = getClass().getResourceAsStream(ressourcePathEquip.toString());
            itemShieldView.setImage(new Image(streamItem));
        }
        if (hero.getArmor() != null) {
            StringBuilder ressourcePathEquip = new StringBuilder("/img/items/");
            ressourcePathEquip.append(hero.getArmor().getImagePath());
            ressourcePathEquip.append(gameDataModel.getImageSuffix());
            InputStream streamItem = getClass().getResourceAsStream(ressourcePathEquip.toString());
            itemArmorView.setImage(new Image(streamItem));
        }
        if (hero.getTrinket1() != null) {
            StringBuilder ressourcePathEquip = new StringBuilder("/img/items/");
            ressourcePathEquip.append(hero.getTrinket1().getImagePath());
            ressourcePathEquip.append(gameDataModel.getImageSuffix());
            InputStream streamItem = getClass().getResourceAsStream(ressourcePathEquip.toString());
            itemTrinket1View.setImage(new Image(streamItem));
        }
        if (hero.getTrinket2() != null) {
            StringBuilder ressourcePathEquip = new StringBuilder("/img/items/");
            ressourcePathEquip.append(hero.getTrinket2().getImagePath());
            ressourcePathEquip.append(gameDataModel.getImageSuffix());
            InputStream streamItem = getClass().getResourceAsStream(ressourcePathEquip.toString());
            itemTrinket2View.setImage(new Image(streamItem));
        }

    }

    @FXML
    public void handleOk(MouseEvent event) {
        prologBox.setVisible(false);
    }

    @FXML
    public void handleRefresh(MouseEvent event) {
        LOGGER.info("HeroGamePresenter: handleRefresh()");
        gameService.updateCampaign();
        gameService.updateGameState(gameDataModel.getCurrentCampaign());
        mapService.rebuildMap();
    }

    @FXML
    public void handleNavigationBack(MouseEvent event) {
        LOGGER.info("HeroGamePresenter: handleNavigationBack()");
        switchFullscreenScene(event, new CampaignSelectionView());
    }

    @FXML
    public void endHeroTurn() {
        gameService.endTurn();
        gameService.updateMessagesAndUnitStats();
        mapService.rebuildMap();
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
        moveButton.setText(buttonCancelText);
        gameDataModel.setCurrentAction(ActionType.MOVE);
        gameService.disableOtherButtons();
        mapService.calcAndDisplayAllowedMovePositions(gameService.getActiveUnit());
    }

    @FXML
    public void handleToggleAttack(MouseEvent event) {
        if (attackButton.getText().equalsIgnoreCase(buttonAttackText)) {
            handleAttack(event);
        } else {
            handleCancel(event);
        }
    }

    private void handleAttack(MouseEvent event) {
        LOGGER.info("HeroGamePresenter: handleAttack()");
        attackButton.setText(buttonCancelText);
        gameDataModel.setCurrentAction(ActionType.ATTACK);
        gameService.disableOtherButtons();
        mapService.calcAndDisplayAllowedAttackTargets(gameService.getActiveUnit());
    }

    private void handleCancel(MouseEvent event) {
        LOGGER.info("HeroGamePresenter: handleCancel()");
        gameService.enableOtherButtons();
        mapService.resetHighlightedMapFields();
        moveButton.setText(buttonMoveText);
        gameDataModel.setCurrentAction(null);
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
