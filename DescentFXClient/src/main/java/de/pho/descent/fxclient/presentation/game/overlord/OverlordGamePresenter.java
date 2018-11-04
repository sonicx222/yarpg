package de.pho.descent.fxclient.presentation.game.overlord;

import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.presentation.game.map.MapService;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.general.GameService;
import de.pho.descent.fxclient.presentation.startmenu.StartMenuView;
import de.pho.descent.shared.model.PlaySide;
import de.pho.descent.shared.model.action.ActionType;
import de.pho.descent.shared.model.monster.GameMonster;
import de.pho.descent.shared.model.quest.QuestPhase;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
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
    private Label labelRound;

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

    @FXML
    private Button turnButton;

    @FXML
    private Button activateButton;

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

    private GameMonster selectedMonster;

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
        overlordGameModel.setActivateButton(activateButton);
    }

    private void setupMonsterTable() {
        // table items
        monsterTable.setItems(overlordGameModel.getMonsters());
        if (gameDataModel.getCurrentQuestEncounter().getCurrentTurn() == PlaySide.OVERLORD) {
            monsterTable.setDisable(false);
        } else {
            monsterTable.setDisable(true);
        }
        
        monsterTable.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                    selectedMonster = (GameMonster) monsterTable.getSelectionModel().getSelectedItem();

                    if (selectedMonster != null && !selectedMonster.isRemoved()
                            && selectedMonster.getActions() > 0) {
                        activateButton.setDisable(false);
                    } else {
                        activateButton.setDisable(true);
                    }
                }
                );
        monsterTable.setRowFactory(tv -> new TableRow<GameMonster>() {
            @Override
            public void updateItem(GameMonster item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("");
                } else if (item.isActive()) {
                    setStyle("-fx-background-color: CornflowerBlue;");
                } else {
                    setStyle("");
                }
            }
        });

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

    @FXML
    public void handleToggleAttack(MouseEvent event) {
        if (attackButton.getText().equalsIgnoreCase(buttonAttackText)) {
            handleAttack(event);
        } else {
            handleCancel(event);
        }
    }

    private void handleMove(MouseEvent event) {
        LOGGER.info("OverlordGamePresenter: handleMove()");
        moveButton.setText(buttonCancelText);
        gameDataModel.setCurrentAction(ActionType.MOVE);
        gameService.disableOtherButtons();
        mapService.calcAndDisplayAllowedMovePositions(gameService.getActiveUnit());
    }

    private void handleAttack(MouseEvent event) {
        LOGGER.info("OverlordGamePresenter: handleAttack()");
        attackButton.setText(buttonCancelText);
        gameDataModel.setCurrentAction(ActionType.ATTACK);
        gameService.disableOtherButtons();
        mapService.calcAndDisplayAllowedAttackTargets(gameService.getActiveUnit());
    }

    private void handleCancel(MouseEvent event) {
        LOGGER.info("OverlordGamePresenter: handleCancel()");
        gameService.enableOtherButtons();
        mapService.resetHighlightedMapFields();
        moveButton.setText(buttonMoveText);
        attackButton.setText(buttonAttackText);
        gameDataModel.setCurrentAction(null);
    }

    @FXML
    public void handleOk(MouseEvent event) {
        prologBox.setVisible(false);
    }

    @FXML
    public void activateMonsterGroup() {
        if (gameDataModel.getCurrentQuestEncounter().getPhase() == QuestPhase.MONSTER_ACTIVATION) {
            if (selectedMonster != null) {
            overlordGameService.activateMonsterGroup(selectedMonster.getMonsterTemplate().getGroup());
            }
            gameService.updateMessagesAndUnitStats();
            mapService.rebuildMap();
        }
    }

    @FXML
    public void endOverlordTurn() {
        gameService.endTurn();
        gameService.updateMessagesAndUnitStats();
        mapService.rebuildMap();
    }

    @FXML
    public void handleRefresh(MouseEvent event) {
        LOGGER.info("OverlordGamePresenter: handleRefresh()");
        gameService.updateCampaign();
        gameService.updateGameState(gameDataModel.getCurrentCampaign());
        mapService.rebuildMap();
    }

    @FXML
    public void handleNavigationBack(MouseEvent event) {
        LOGGER.info("OverlordGamePresenter: handleNavigationBack()");
        switchFullscreenScene(event, new StartMenuView());
    }

}
