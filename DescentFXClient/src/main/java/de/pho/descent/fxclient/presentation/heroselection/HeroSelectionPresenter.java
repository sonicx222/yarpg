package de.pho.descent.fxclient.presentation.heroselection;

import static de.pho.descent.fxclient.MainApp.showError;
import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.HeroSelectionClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.startmenu.StartMenuView;
import de.pho.descent.shared.dto.WsHeroSelection;
import de.pho.descent.shared.model.hero.HeroTemplate;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javax.inject.Inject;
import jersey.repackaged.com.google.common.base.Objects;

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
    private ImageView heroSheetView;

    @FXML
    private Text textAction;

    @FXML
    private TableColumn<WsHeroSelection, HeroTemplate> selectionHeroColumn;

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
    private HeroSelectionModel heroSelectionModel;

    private LinearGradient gradientMenuItem;

    private final Map<HeroTemplate, Image> heroImages = new HashMap<>();

    private String buttonReadyText;
    private String buttonCancelText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gradientMenuItem = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.DARKVIOLET),
            new Stop(0.1, Color.BLACK),
            new Stop(0.9, Color.BLACK),
            new Stop(1, Color.DARKVIOLET)
        });

        // setup buttons
        if (Objects.equal(credentials.getPlayer().getUsername(),
                gameDataModel.getCurrentCampaign().getOverlord())) {
            // overlord screen
            vboxAction.setVisible(false);
            vboxStart.setVisible(true);
        } else {
            // hero player screen
            vboxAction.setVisible(true);
            vboxStart.setVisible(false);
        }

        buttonReadyText = resources.getString("button.hero.selection.ready.toggle");
        buttonCancelText = resources.getString("button.hero.selection.cancel.toggle");

        // setup label properties
        labelAnnounce.setFont(Font.font("Verdana", FontPosture.ITALIC, 13));

        // populate hero selections
        updateHeroSelections();
        activeSelectionsTableView.setItems(heroSelectionModel.getHeroSelections());
        activeSelectionsTableView.setEditable(false);

        // columns
        selectionHeroColumn.setCellValueFactory(new PropertyValueFactory<>("selectedHero"));
        selectionPlayerColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        selectionReadyColumn.setCellValueFactory(new PropertyValueFactory<>("ready"));

        preloadHeroImages();

        // reset current user hero selection
        heroSelectionModel.setCurrentSelection(null);

        heroSelectionModel.getHeroes().clear();
        heroSelectionModel.getHeroes().addAll(HeroTemplate.values());

        heroesListView.setItems(heroSelectionModel.getHeroes());
        heroesListView.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                    heroSelectionModel.setSelectedTemplate((HeroTemplate) newValue);
                    HeroTemplate template = heroSelectionModel.getSelectedTemplate();

                    labelArchetype.setText(template.getHeroArchetype().name());
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
                    heroImageView.setImage(heroImages.get(template));
                }
                );
        // preselect first element
        heroesListView.getSelectionModel().select(0);

    }

    @FXML
    public void handleOnMouseEntered(MouseEvent event) {
        if (event.getSource() instanceof StackPane) {
            StackPane menuItem = (StackPane) event.getSource();

            menuItem.getChildren().forEach((node) -> {
                if (node instanceof Rectangle) {
                    ((Rectangle) node).setFill(gradientMenuItem);
                } else if (node instanceof Text) {
                    ((Text) node).setFill(Color.WHITE);
                }
            });
        }
    }

    @FXML
    public void handleOnMouseExited(MouseEvent event) {
        if (event.getSource() instanceof StackPane) {
            StackPane menuItem = (StackPane) event.getSource();

            menuItem.getChildren().forEach((node) -> {
                if (node instanceof Rectangle) {
                    ((Rectangle) node).setFill(Color.BLACK);
                } else if (node instanceof Text) {
                    ((Text) node).setFill(Color.DARKGREY);
                }
            });
        }
    }

    @FXML
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

    @FXML
    public void handleOnMouseReleased(MouseEvent event) {
        if (event.getSource() instanceof StackPane) {
            StackPane menuItem = (StackPane) event.getSource();

            menuItem.getChildren().forEach((node) -> {
                if (node instanceof Rectangle) {
                    ((Rectangle) node).setFill(gradientMenuItem);
                }
            });
        }
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
        WsHeroSelection selection = heroSelectionModel.getCurrentSelection();
        HeroTemplate template = heroSelectionModel.getSelectedTemplate();

        // check if there is already a user made selection
        if (selection != null && selection.getId() > 0) {
            // selection already done in the past
            selection.setSelectedHero(template);
        } else {
            // new selection
            selection = new WsHeroSelection(credentials.getUsername(), template);
        }
        selection.setReady(true);

        // update selections and check if other user already selected same hero
        updateHeroSelections();
//        for (WsHeroSelection hs : heroSelectionModel.getHeroSelections()) {
//            if (hs.getSelectedHero() == selection.getSelectedHero()
//                    && !hs.getUsername().equals(selection.getUsername())) {
//                showError("");
//            }
//        }

        WsHeroSelection updatedSelection = null;
        try {
            updatedSelection = HeroSelectionClient.saveSelection(credentials, selection, gameDataModel.getCurrentCampaign());
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
        WsHeroSelection selection = heroSelectionModel.getCurrentSelection();
        selection.setReady(false);

        WsHeroSelection updatedSelection = null;
        try {
            updatedSelection = HeroSelectionClient.saveSelection(credentials, selection, gameDataModel.getCurrentCampaign());
        } catch (ServerException ex) {
            showError(ex);
        }

        if (updatedSelection != null) {
            heroSelectionModel.setCurrentSelection(updatedSelection);
            updateHeroSelections();
            textAction.setText(buttonReadyText);
        }
    }

    @FXML
    public void handleRefresh(MouseEvent event) {
        updateHeroSelections();
    }

    @FXML
    public void handleStart(MouseEvent event) {
        // start campaign
    }

    @FXML
    public void handleNavigationBack(MouseEvent event) {
        //setCancelState();
        switchFullscreenScene(event, new StartMenuView());
    }

    private void preloadHeroImages() {
        heroImages.clear();
        for (HeroTemplate template : HeroTemplate.values()) {
            InputStream is = getClass().getResourceAsStream("/img/heroes/" + template.getImageName() + ".png");
            if (is != null) {
                Image img = new Image(is);
                heroImages.put(template, img);
            }
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
        }
    }
}
