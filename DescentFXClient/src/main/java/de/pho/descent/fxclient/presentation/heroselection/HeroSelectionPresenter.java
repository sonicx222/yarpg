package de.pho.descent.fxclient.presentation.heroselection;

import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.presentation.startmenu.StartMenuView;
import de.pho.descent.shared.model.hero.HeroTemplate;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class HeroSelectionPresenter implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(HeroSelectionPresenter.class.getName());

    @FXML
    private ListView heroesListView;

    @FXML
    private ImageView heroImageView;

    @FXML
    private ImageView heroSheetView;
    
    @FXML
    private Text textAction;

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
        buttonReadyText = resources.getString("button.hero.selection.ready.toggle");
        buttonCancelText = resources.getString("button.hero.selection.cancel.toggle");

        preloadHeroImages();

        heroSelectionModel.getHeroes().clear();
        heroSelectionModel.getHeroes().addAll(HeroTemplate.values());

        heroesListView.setItems(heroSelectionModel.getHeroes());
        heroesListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            heroImageView.setImage(heroImages.get((HeroTemplate) newValue));
        } // this method will be called whenever user selected row
        );

        heroSheetView.setImage(new Image(getClass().getResourceAsStream("/img/layout/Hero_Sheet_Template.png")));
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
           textAction.setText(buttonCancelText);          
           //setReadyState();
       } else {
           textAction.setText(buttonReadyText);
           //setCancelState();
       }
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
}
