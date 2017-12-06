package de.pho.descent.fxclient.presentation.game;

import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.startmenu.StartMenuView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javax.inject.Inject;

/**
 *
 * @author pho
 */
public class GamePresenter implements Initializable {

    @FXML
    private ScrollPane mapScrollPane;

    @FXML
    private StackPane mapStackPane;

    @FXML
    private ImageView mapImageView;

    @FXML
    private GridPane mapGridPane;

    @Inject
    private GameDataModel gameDataModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mapImageView.setImage(new Image(getClass().getResourceAsStream("/img/encounter/First_Blood2.png")));
    }

    @FXML
    public void handleBackNavigation(MouseEvent event) {
        switchFullscreenScene(event, new StartMenuView());
    }
    
    private void resizeGridPane() {
        
    }
}
