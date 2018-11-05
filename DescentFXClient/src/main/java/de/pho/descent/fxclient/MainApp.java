package de.pho.descent.fxclient;

import com.airhacks.afterburner.injection.Injector;
import com.airhacks.afterburner.views.FXMLView;
import static de.pho.descent.fxclient.business.config.Configuration.*;
import de.pho.descent.fxclient.presentation.loginscreen.LoginscreenView;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

public class MainApp extends Application {

    private static final LogManager LOGMANAGER = LogManager.getLogManager();
    private static final Logger LOGGER = Logger.getLogger(MainApp.class.getName());
    public static Font gameFont;
    public static Stage currentStage;

    //setup logging configuration
    static {
        try {
            LOGMANAGER.readConfiguration(MainApp.class.getClassLoader()
                    .getResourceAsStream(LOGGING_PROPERTIES_FILE));
        } catch (IOException ioex) {
            LOGGER.log(Level.SEVERE, ERROR_LOGGING_PROPERTIES, ioex);
        }
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            showError(throwable);
        });

        loadProperties();

        // load Font
        gameFont = Font.loadFont(MainApp.class.getClassLoader().getResourceAsStream("fonts/Garamond_Premier_Pro.ttf"), 18);

        currentStage = stage;
        LoginscreenView loginscreenView = new LoginscreenView();
        Scene scene = new Scene(loginscreenView.getView());

        showSceneInFullscreen(stage, scene);
    }

    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
        LOGMANAGER.reset();
    }

    public static void switchToNewStageScene(final Stage previousStage, FXMLView viewOfScene) {
        if (previousStage != null) {
            previousStage.close();
        }

        if (viewOfScene != null) {
            showSceneInStage(new Stage(), new Scene(viewOfScene.getView()));
        }
    }

    public static void switchFullscreenScene(FXMLView viewOfScene) {
        currentStage.getScene().setRoot(viewOfScene.getView());
    }

    public static void switchFullscreenScene(final Event event, FXMLView viewOfScene) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.getScene().setRoot(viewOfScene.getView());
    }

    public static void switchFullscreenScene(final Node node, FXMLView viewOfScene) {
        Stage currentStage = (Stage) (node).getScene().getWindow();
        currentStage.getScene().setRoot(viewOfScene.getView());
    }

    private static void showSceneInStage(Stage stage, Scene scene) {
        stage.setScene(scene);

        stage.show();
    }

    private static void showSceneInFullscreen(Stage stage, Scene scene) {
        stage.setFullScreen(false);
        // disable popup message
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setScene(scene);

        stage.show();
    }

    public static void showError(Throwable t) {
        LOGGER.log(Level.SEVERE, t.getMessage(), t);

        Notifications tmp = Notifications.create();
        tmp.darkStyle().position(Pos.TOP_RIGHT).text(t.getMessage()).showError();
    }

    public static void showError(String errorMsg) {
        LOGGER.log(Level.SEVERE, errorMsg);

        Notifications tmp = Notifications.create();
        tmp.darkStyle().position(Pos.TOP_RIGHT).text(errorMsg).showError();
    }
}
