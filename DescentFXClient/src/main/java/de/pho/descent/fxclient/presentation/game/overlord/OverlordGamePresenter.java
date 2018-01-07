package de.pho.descent.fxclient.presentation.game.overlord;

import static de.pho.descent.fxclient.MainApp.showError;
import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.CampaignClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.game.map.MapService;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.general.GameService;
import de.pho.descent.fxclient.presentation.startmenu.StartMenuView;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.model.action.ActionType;
import de.pho.descent.shared.model.monster.GameMonster;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class OverlordGamePresenter implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(OverlordGamePresenter.class.getName());

    @FXML
    private VBox prologBox;

    @FXML
    private TableView monsterTable;

    @FXML
    private TableColumn<GameMonster, String> colMonsterName;

    @FXML
    private TableColumn<GameMonster, String> colMonsterGroup;

    @FXML
    private TableColumn<GameMonster, String> colMonsterActive;

    @FXML
    private TableColumn<GameMonster, String> colMonsterActions;

    @FXML
    private TableColumn<GameMonster, String> colMonsterSpeed;

    @FXML
    private TableColumn<GameMonster, String> colMonsterHealth;

    @FXML
    private TableColumn<GameMonster, String> colMonsterSpecials;

    /**
     * Monster Action Controls
     */
    @FXML
    private Button moveButton;

    @FXML
    private Button attackButton;

    @Inject
    private Credentials credentials;

    @Inject
    private GameDataModel gameDataModel;

    @Inject
    private GameService gameService;

    @Inject
    private OverlordGameModel overlordGameModel;

    @Inject
    private OverlordGameService overlordGameService;

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

        setupMonsterTable();
        setupUIControls(resources);
        overlordGameService.updateUIControls();

        // update stats
        overlordGameService.updateMonsterStats();

        // show prolog
        gameDataModel.setPrologBoxVisible(prologBox.visibleProperty());
        if (gameDataModel.getCurrentQuestEncounter().getRound() == 1) {
            prologBox.setVisible(true);
        }
    }

    private void setupUIControls(ResourceBundle resources) {
        buttonMoveText = resources.getString("button.hero.game.move.toggle");
        buttonAttackText = resources.getString("button.hero.game.attack.toggle");
        buttonCancelText = resources.getString("button.hero.game.cancel.toggle");

        gameDataModel.setMoveButton(moveButton);
        gameDataModel.setAttackButton(attackButton);
    }

    private void setupMonsterTable() {
        // table items
        monsterTable.setItems(overlordGameModel.getMonsters());

        // columns
        colMonsterName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colMonsterGroup.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GameMonster, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<GameMonster, String> p) {
                return new SimpleStringProperty(p.getValue().getMonsterTemplate().getGroup().getText());
            }
        });
        colMonsterActive.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GameMonster, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<GameMonster, String> p) {
                return new SimpleStringProperty(p.getValue().isActive() ? "X" : "");
            }
        });
        colMonsterActions.setCellValueFactory(new PropertyValueFactory<>("actions"));
        colMonsterSpeed.setCellValueFactory(new PropertyValueFactory<>("movementPoints"));
        colMonsterHealth.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GameMonster, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<GameMonster, String> p) {
                return new SimpleStringProperty(p.getValue().getCurrentLife() + " / " + p.getValue().getTotalLife());
            }
        });
        colMonsterSpecials.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GameMonster, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<GameMonster, String> p) {
                StringBuilder sb = new StringBuilder();

                p.getValue().getMonsterTemplate().getAbilities().stream()
                        .forEach(ability -> sb.append(ability.toString()).append("\n"));

                return new SimpleStringProperty(sb.toString());
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
        mapService.calcAndDisplayAllowedPositions(gameService.getActiveUnit());
    }

    private void handleCancel(MouseEvent event) {
        LOGGER.info("HeroGamePresenter: handleCancel()");
        gameService.enableOtherButtons();
        mapService.resetHighlightedMapFields();
        moveButton.setText(buttonMoveText);
        gameDataModel.setCurrentAction(null);
    }

    @FXML
    public void handleOk(MouseEvent event) {
        prologBox.setVisible(false);
    }

    @FXML
    public void handleRefresh(MouseEvent event) {
        LOGGER.info("OverlordGamePresenter: handleRefresh()");
        WsCampaign updatedCampaign = null;
        try {
            updatedCampaign = CampaignClient.getCampaignById(
                    credentials.getUsername(),
                    credentials.getPassword(),
                    gameDataModel.getCurrentCampaign().getId());
        } catch (ServerException ex) {
            showError(ex);
        }
        if (updatedCampaign != null) {
            // update
            gameService.updateGameState(updatedCampaign);
        }
    }

    @FXML
    public void handleNavigationBack(MouseEvent event) {
        LOGGER.info("OverlordGamePresenter: handleNavigationBack()");
        switchFullscreenScene(event, new StartMenuView());
    }

}
