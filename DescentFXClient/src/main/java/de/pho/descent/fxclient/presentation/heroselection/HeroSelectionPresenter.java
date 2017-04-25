package de.pho.descent.fxclient.presentation.heroselection;

import de.pho.descent.shared.model.hero.HeroTemplate;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    @Inject
    private HeroSelectionModel heroSelectionModel;

    private Map<HeroTemplate, Image> heroImages = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        preloadHeroImages();

        heroSelectionModel.getHeroes().clear();
        heroSelectionModel.getHeroes().addAll(HeroTemplate.values());

        heroesListView.setItems(heroSelectionModel.getHeroes());
        heroesListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue observale, Object oldValue, Object newValue) -> {
            heroImageView.setImage(heroImages.get((HeroTemplate) newValue));
        } // this method will be called whenever user selected row
        );

        heroSheetView.setImage(new Image(getClass().getResourceAsStream("/img/layout/Hero_Sheet_Template.png")));
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
