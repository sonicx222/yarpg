package de.pho.descent.fxclient.presentation.heroselection;

import static de.pho.descent.fxclient.MainApp.showError;
import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.HeroSelectionClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.game.hero.HeroGameView;
import de.pho.descent.fxclient.presentation.game.overlord.OverlordGameView;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.general.GameService;
import de.pho.descent.fxclient.presentation.message.MessageService;
import de.pho.descent.fxclient.presentation.startmenu.StartMenuView;
import de.pho.descent.shared.dto.WsHeroSelection;
import static de.pho.descent.shared.model.campaign.CampaignPhase.ENCOUNTER;
import de.pho.descent.shared.model.hero.HeroTemplate;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class HeroSelectionPresenter implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(HeroSelectionPresenter.class.getName());

    @FXML
    private TableView activeSelectionsTableView;

    @FXML
    private ListView heroesListView;

    @FXML
    private ImageView heroImageView;

    @FXML
    private ImageView startSkillView;

    @FXML
    private ImageView startItem1View;

    @FXML
    private ImageView startItem2View;

    @FXML
    private Text textAction;

    @FXML
    private TableColumn<WsHeroSelection, HeroTemplate> selectionHeroColumn;

    @FXML
    private TableColumn<WsHeroSelection, String> selectionArchetypeColumn;

    @FXML
    private TableColumn<WsHeroSelection, String> selectionClassColumn;

    @FXML
    private TableColumn<WsHeroSelection, String> selectionPlayerColumn;

    @FXML
    private TableColumn<WsHeroSelection, Boolean> selectionReadyColumn;

    @FXML
    private Label labelArchetype;
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
    private Label labelAnnounce;

    @FXML
    private Label labelHeroAbility;

    @FXML
    private Label labelHeroicFeat;

    @FXML
    private VBox vboxAction;

    @FXML
    private VBox vboxStart;

    @Inject
    private Credentials credentials;

    @Inject
    private GameDataModel gameDataModel;

    @Inject
    private GameService gameService;

    @Inject
    private MessageService messageService;

    @Inject
    private HeroSelectionModel heroSelectionModel;

    private final Map<HeroTemplate, Image[]> heroImages = new HashMap<>();

    private String buttonReadyText;
    private String buttonCancelText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // setup label properties
        labelAnnounce.setFont(Font.font("Verdana", FontPosture.ITALIC, 13));

        // populate hero selections
        updateHeroSelections();
        activeSelectionsTableView.setItems(heroSelectionModel.getHeroSelections());
        activeSelectionsTableView.setEditable(false);

        // columns
        selectionHeroColumn.setCellValueFactory(new PropertyValueFactory<>("selectedHero"));
        selectionArchetypeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<WsHeroSelection, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WsHeroSelection, String> p) {
                return new SimpleStringProperty(p.getValue().getSelectedHero().getArchetype().getText());
            }
        });
        selectionClassColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<WsHeroSelection, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WsHeroSelection, String> p) {
                return new SimpleStringProperty(p.getValue().getSelectedHero().getHeroClass().getText());
            }
        });
        selectionPlayerColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        selectionReadyColumn.setCellValueFactory(new PropertyValueFactory<>("ready"));

        preloadHeroTemplateImages();

        heroSelectionModel.getHeroes().clear();
        heroSelectionModel.getHeroes().addAll(HeroTemplate.values());

        heroesListView.setItems(heroSelectionModel.getHeroes());
        heroesListView.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                    if (newValue != null) {
                        heroSelectionModel.setSelectedTemplate((HeroTemplate) newValue);
                        HeroTemplate template = heroSelectionModel.getSelectedTemplate();

                        labelArchetype.setText(template.getArchetype().name());
                        labelSpeed.setText(String.valueOf(template.getSpeed()));
                        labelHealth.setText(String.valueOf(template.getHealth()));
                        labelStamina.setText(String.valueOf(template.getStamina()));
                        labelMight.setText(String.valueOf(template.getMight()));
                        labelKnowledge.setText(String.valueOf(template.getKnowledge()));
                        labelWillpower.setText(String.valueOf(template.getWillpower()));
                        labelAwareness.setText(String.valueOf(template.getAwareness()));
                        labelAnnounce.setText("\"" + template.getAnnounce() + "\"");
                        labelHeroAbility.setText(template.getHeroAbilityText());
                        labelHeroicFeat.setText(template.getHeroicFeatText());
                        heroImageView.setImage(heroImages.get(template)[0]);
                        startSkillView.setImage(heroImages.get(template)[1]);
                        startItem1View.setImage(heroImages.get(template)[2]);
                        startItem2View.setImage(heroImages.get(template)[3]);
                    }
                }
                );

        // preselect first element
        heroesListView.getSelectionModel().select(0);

        // setup buttons
        buttonReadyText = resources.getString("button.hero.selection.ready.toggle");
        buttonCancelText = resources.getString("button.hero.selection.cancel.toggle");

        if (Objects.equals(credentials.getPlayer(),
                gameDataModel.getCurrentCampaign().getOverlord().getPlayedBy())) {
            // overlord screen
            vboxAction.setVisible(false);
            vboxStart.setVisible(true);
        } else {
            // hero player screen
            vboxAction.setVisible(true);
            vboxStart.setVisible(false);
        }

        // set correct text if player made already an earlier selection when entering screen
        if (heroSelectionModel.getCurrentSelection() != null
                && heroSelectionModel.getCurrentSelection().isReady()) {
            textAction.setText(buttonCancelText);
        }

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
    public void handleRefresh(MouseEvent event) {
        LOGGER.info("HeroSelectionPresenter: handleRefresh()");

        gameService.updateCampaign();
        switch (gameDataModel.getCurrentCampaign().getPhase()) {
            case HERO_SELECTION:
                updateHeroSelections();
                messageService.updateCampaignMessages();
                break;
            case ENCOUNTER:
                if (Objects.equals(credentials.getPlayer(), gameDataModel.getCurrentCampaign().getOverlord().getPlayedBy())) {
                    switchFullscreenScene(new OverlordGameView());
                } else {
                    switchFullscreenScene(new HeroGameView());
                }
        }
    }

    @FXML
    public void handleStart(MouseEvent event) {
        LOGGER.info("HeroSelectionPresenter: handleStart()");

        gameService.startCampaign();
        if (gameDataModel.getCurrentCampaign() != null
                && gameDataModel.getCurrentCampaign().getPhase() == ENCOUNTER) {
            if (Objects.equals(credentials.getPlayer(), gameDataModel.getCurrentCampaign().getOverlord().getPlayedBy())) {
                switchFullscreenScene(new OverlordGameView());
            } else {
                switchFullscreenScene(new HeroGameView());
            }
        }
    }

    @FXML
    public void handleNavigationBack(MouseEvent event) {
        LOGGER.info("HeroSelectionPresenter: handleNavigationBack()");
        switchFullscreenScene(new StartMenuView());
    }

    @FXML
    public void handleToggleReady(MouseEvent event) {
        if (textAction.getText().equalsIgnoreCase(buttonReadyText)) {
            handleReady();
        } else {
            handleCancel();
        }
    }

    private void handleReady() {
        LOGGER.info("HeroSelectionPresenter: handleReady()");
        WsHeroSelection selection = heroSelectionModel.getCurrentSelection();
        HeroTemplate template = heroSelectionModel.getSelectedTemplate();

        // check if there is already a user made selection
        if (selection != null && selection.getId() > 0) {
            // selection already done in the past
            selection.setSelectedHero(template);
            selection.setReady(true);
        } else {
            // new selection
            selection = new WsHeroSelection(credentials.getUsername(), template, true);
        }

        // update selections and check if other user already selected same hero
        WsHeroSelection updatedSelection = null;
        try {
            updatedSelection = HeroSelectionClient.saveSelection(
                    credentials, selection, gameDataModel.getCurrentCampaign());
        } catch (ServerException ex) {
            showError(ex);
        }
        if (updatedSelection != null) {
            heroSelectionModel.setCurrentSelection(updatedSelection);
            updateHeroSelections();
            textAction.setText(buttonCancelText);
        }
    }

    private void handleCancel() {
        LOGGER.info("HeroSelectionPresenter: handleCancel()");
        WsHeroSelection selection = heroSelectionModel.getCurrentSelection();
        selection.setReady(false);

        WsHeroSelection updatedSelection = null;
        try {
            updatedSelection = HeroSelectionClient.saveSelection(
                    credentials, selection, gameDataModel.getCurrentCampaign());
        } catch (ServerException ex) {
            showError(ex);
        }

        if (updatedSelection != null) {
            heroSelectionModel.setCurrentSelection(updatedSelection);
            updateHeroSelections();
            textAction.setText(buttonReadyText);
        }
    }

    private void preloadHeroTemplateImages() {
        heroImages.clear();

        for (HeroTemplate template : HeroTemplate.values()) {
            Image[] images = new Image[4];

            // hero preview image
            StringBuilder ressourcePathHero = new StringBuilder("/img/heroes/");
            ressourcePathHero.append(template.getImageName());
            ressourcePathHero.append(gameDataModel.getImageSuffix());
            InputStream isHero = getClass().getResourceAsStream(ressourcePathHero.toString());
            if (isHero != null) {
                images[0] = new Image(isHero);
            }

            // hero class start skill
            if (template.getStartSkill() != null) {
                StringBuilder ressourcePathStartSkill = new StringBuilder("/img/skills/");
                ressourcePathStartSkill.append(template.getStartSkill().getImagePath());
                ressourcePathStartSkill.append(gameDataModel.getImageSuffix());

                InputStream isStartSkill = getClass().getResourceAsStream(ressourcePathStartSkill.toString());
                if (isStartSkill != null) {
                    images[1] = new Image(isStartSkill);
                }
            }

            // hero class start item 1
            if (template.getStartWeapon() != null) {
                StringBuilder ressourcePathStartItem1 = new StringBuilder("/img/items/");
                ressourcePathStartItem1.append(template.getStartWeapon().getImagePath());
                ressourcePathStartItem1.append(gameDataModel.getImageSuffix());
                InputStream isStartItem1 = getClass().getResourceAsStream(ressourcePathStartItem1.toString());
                if (isStartItem1 != null) {
                    images[2] = new Image(isStartItem1);
                }
            }

            // hero class start item 2
            StringBuilder ressourcePathStartItem2 = new StringBuilder("/img/items/");
            if (template.getStartShield() != null) {
                ressourcePathStartItem2.append(template.getStartShield().getImagePath());
            } else {
                ressourcePathStartItem2.append(template.getStartTrinket().getImagePath());
            }
            ressourcePathStartItem2.append(gameDataModel.getImageSuffix());
            InputStream isStartItem2 = getClass().getResourceAsStream(ressourcePathStartItem2.toString());
            if (isStartItem2 != null) {
                images[3] = new Image(isStartItem2);
            }

            heroImages.put(template, images);
        }
    }

    private void updateHeroSelections() {
        heroSelectionModel.getHeroSelections().clear();

        List<WsHeroSelection> loadedSelections = null;
        try {
            loadedSelections = HeroSelectionClient.getCurrentSelections(
                    credentials.getUsername(), credentials.getPassword(),
                    gameDataModel.getCurrentCampaign());
        } catch (ServerException ex) {
            showError(ex);
        }

        if (loadedSelections != null && !loadedSelections.isEmpty()) {
            heroSelectionModel.getHeroSelections().addAll(loadedSelections);

            WsHeroSelection playerSelection = heroSelectionModel.getHeroSelections().stream()
                    .filter(selection -> Objects.equals(selection.getUsername(), credentials.getUsername()))
                    .findAny().orElse(null);
            heroSelectionModel.setCurrentSelection(playerSelection);
        }
    }
}
