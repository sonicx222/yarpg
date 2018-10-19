package de.pho.descent.fxclient.presentation.general;

import static de.pho.descent.fxclient.MainApp.showError;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.CampaignClient;
import de.pho.descent.fxclient.business.ws.MapClient;
import de.pho.descent.fxclient.business.ws.QuestClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.game.hero.HeroGameService;
import de.pho.descent.fxclient.presentation.game.map.MapService;
import de.pho.descent.fxclient.presentation.game.overlord.OverlordGameService;
import de.pho.descent.fxclient.presentation.message.MessageService;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.dto.WsGameMap;
import de.pho.descent.shared.dto.WsQuestEncounter;
import de.pho.descent.shared.model.GameUnit;
import java.util.Objects;
import java.util.logging.Logger;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class GameService {

    private static final Logger LOGGER = Logger.getLogger(GameService.class.getName());

    @Inject
    private Credentials credentials;
    @Inject
    private GameDataModel gameDataModel;
    @Inject
    private OverlordGameService overlordGameService;
    @Inject
    private HeroGameService heroGameService;
    @Inject
    private MapService mapService;
    @Inject
    private MessageService messageService;

    @PostConstruct
    public void init() {

    }

    public GameUnit getActiveUnit() {
        GameUnit unit = gameDataModel.getCurrentQuestEncounter().getActiveHero();

        if (unit == null) {
            unit = gameDataModel.getCurrentQuestEncounter().getActiveMonster();
        }

        return unit;
    }

    public void disableOtherButtons() {
        switch (gameDataModel.getCurrentAction()) {
            case MOVE:
                gameDataModel.getAttackButton().setDisable(true);
                break;
            case ATTACK:
                gameDataModel.getMoveButton().setDisable(true);
                break;
            default:
                break;
        }
    }

    public void enableOtherButtons() {
        switch (gameDataModel.getCurrentAction()) {
            case MOVE:
                gameDataModel.getAttackButton().setDisable(false);
                break;
            case ATTACK:
                gameDataModel.getMoveButton().setDisable(false);
                break;
            default:
                gameDataModel.getAttackButton().setDisable(false);
                gameDataModel.getMoveButton().setDisable(false);
                break;
        }
    }

    public void handleOnMouseEntered(MouseEvent event) {
        if (event.getSource() instanceof StackPane) {
            StackPane menuItem = (StackPane) event.getSource();

            menuItem.getChildren().forEach((node) -> {
                if (node instanceof Rectangle) {
                    ((Rectangle) node).setFill(gameDataModel.getButtonGradient());
                } else if (node instanceof Text) {
                    ((Text) node).setFill(Color.WHITE);
                } else if (node instanceof Label) {
                    ((Label) node).setTextFill(Color.WHITE);
                }
            });
        }
    }

    public void handleOnMouseExited(MouseEvent event) {
        if (event.getSource() instanceof StackPane) {
            StackPane menuItem = (StackPane) event.getSource();

            menuItem.getChildren().forEach((node) -> {
                if (node instanceof Rectangle) {
                    ((Rectangle) node).setFill(Color.BLACK);
                } else if (node instanceof Text) {
                    ((Text) node).setFill(Color.DARKGREY);
                } else if (node instanceof Label) {
                    ((Label) node).setTextFill(Color.DARKGREY);
                }
            });
        }
    }

    public void handleOnMousePressed(MouseEvent event) {
        if (event.getSource() instanceof StackPane) {
            StackPane menuItem = (StackPane) event.getSource();

            menuItem.getChildren().forEach((node) -> {
                if (node instanceof Rectangle) {
                    ((Rectangle) node).setFill(Color.DARKVIOLET);
                }
            });
        }
    }

    public void handleOnMouseReleased(MouseEvent event) {
        if (event.getSource() instanceof StackPane) {
            StackPane menuItem = (StackPane) event.getSource();

            menuItem.getChildren().forEach((node) -> {
                if (node instanceof Rectangle) {
                    ((Rectangle) node).setFill(gameDataModel.getButtonGradient());
                }
            });
        }
    }

    public void startCampaign() {
        WsCampaign startedCampaign = null;
        try {
            startedCampaign = CampaignClient.startCampaign(credentials.getUsername(),
                    credentials.getPassword(), gameDataModel.getCurrentCampaign());
        } catch (ServerException ex) {
            showError(ex);
        }
        if (startedCampaign != null) {
            gameDataModel.setCurrentCampaign(startedCampaign);
        }
    }

    public void updateCampaign() {
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
            gameDataModel.setCurrentCampaign(updatedCampaign);
        }
    }

    public void updateGameState(WsCampaign updatedCampaign) {
        gameDataModel.setCurrentCampaign(updatedCampaign);
        loadQuestEncounter();
        loadQuestMap();
        mapService.rebuildMap();
        messageService.updateCampaignMessages();

        if (credentials.getPlayer().equals(gameDataModel.getCurrentCampaign().getOverlord().getPlayedBy())) {
            overlordGameService.updateMonsterStats();
            overlordGameService.updateUIControls();
        } else {
            heroGameService.updateHeroStats();
            heroGameService.updateUIControls();
        }
    }

    public void loadQuestEncounter() {
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

    public void loadQuestMap() {
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
}
