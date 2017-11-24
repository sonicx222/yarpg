package de.pho.descent.fxclient.presentation.game;

import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.presentation.startmenu.StartMenuView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author pho
 */
public class GamePresenter implements Initializable {

        @FXML
    private ImageView mapImageView;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mapImageView.setImage(new Image(getClass().getResourceAsStream("/img/encounter/First_Blood2.png")));
    }
    
    @FXML
    public void handleBackNavigation(MouseEvent event) {
        switchFullscreenScene(event, new StartMenuView());
    }
}
