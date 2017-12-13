package de.pho.descent.fxclient.presentation.game.hero;

import static de.pho.descent.fxclient.MainApp.showError;
import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.QuestClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.startmenu.StartMenuView;
import de.pho.descent.shared.dto.WsQuestEncounter;
import de.pho.descent.shared.model.hero.GameHero;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class HeroGamePresenter implements Initializable {

    @FXML
    private ScrollPane mapScrollPane;

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

    @Inject
    private Credentials credentials;

    @Inject
    private GameDataModel gameDataModel;
    private final String IMAGE_SUFFIX = ".png";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // load active quest encounter of current campaign
        if (gameDataModel.getCurrentQuestEncounter() == null) {
            loadQuestEncounter();
        }

        setupHeroSheet();

        // temporary
        mapScrollPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                switchFullscreenScene(e, new StartMenuView());
            }
        });
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
    public void handleBackNavigation(MouseEvent event) {

    }

    private void resizeGridPane() {

    }
}
