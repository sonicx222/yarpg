package de.pho.descent.fxclient.presentation.campaignselection;

import static de.pho.descent.fxclient.MainApp.showError;
import static de.pho.descent.fxclient.MainApp.switchFullscreenScene;
import de.pho.descent.fxclient.business.auth.Credentials;
import de.pho.descent.fxclient.business.ws.CampaignClient;
import de.pho.descent.fxclient.business.ws.ServerException;
import de.pho.descent.fxclient.presentation.general.GameDataModel;
import de.pho.descent.fxclient.presentation.heroselection.HeroSelectionView;
import de.pho.descent.fxclient.presentation.startmenu.StartMenuView;
import de.pho.descent.shared.dto.WsCampaign;
import de.pho.descent.shared.model.campaign.CampaignPhase;
import java.net.URL;
import java.util.Date;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
public class CampaignSelectionPresenter implements Initializable {

    @FXML
    private StackPane paneContinue;

    @FXML
    private TableView campaignsTableView;

    @FXML
    private TableColumn<WsCampaign, Long> campaignIDColumn;

    @FXML
    private TableColumn<WsCampaign, String> campaignOverlordColumn;

    @FXML
    private TableColumn<WsCampaign, CampaignPhase> campaignPhaseColumn;

    @FXML
    private TableColumn<WsCampaign, Date> campaignCreatedColumn;

    @FXML
    private TableColumn<WsCampaign, Date> campaignStartedColumn;

    @Inject
    private GameDataModel gameDataModel;

    @Inject
    private Credentials credentials;

    private LinearGradient gradientMenuItem;

    private ResourceBundle resources = null;

    private WsCampaign selectedCampaign;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        gradientMenuItem = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.DARKVIOLET),
            new Stop(0.1, Color.BLACK),
            new Stop(0.9, Color.BLACK),
            new Stop(1, Color.DARKVIOLET)
        });

        // disable continue button, until selection made
        paneContinue.setDisable(true);

        // populate table view
        campaignsTableView.setItems(gameDataModel.getPlayableCampaigns());
        campaignsTableView.setEditable(false);

        // columns
        campaignIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        campaignOverlordColumn.setCellValueFactory(new PropertyValueFactory<>("overlord"));
        campaignPhaseColumn.setCellValueFactory(new PropertyValueFactory<>("phase"));
        campaignCreatedColumn.setCellValueFactory(new PropertyValueFactory<>("createdOn"));
        campaignStartedColumn.setCellValueFactory(new PropertyValueFactory<>("startedOn"));

        // selection action
        campaignsTableView.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue observale, Object oldValue, Object newValue) -> {
                    selectedCampaign = (WsCampaign) campaignsTableView.getSelectionModel().getSelectedItem();

                    if (selectedCampaign != null) {
                        paneContinue.setDisable(false);
                    } else {
                        paneContinue.setDisable(true);
                    }
                }
                );
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
                    ((Text) node).setFill(Color.DARKGRAY);
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
    public void handleBack(MouseEvent event) {
        switchFullscreenScene(event, new StartMenuView());
    }

    @FXML
    public void handleRefresh(MouseEvent event) {
        updatePlayableCampaigns();
    }

    @FXML
    public void handleContinue(MouseEvent event) {
        requireNonNull(selectedCampaign);
        gameDataModel.setCurrentCampaign(selectedCampaign);
        switchFullscreenScene(event, new HeroSelectionView());
    }

    private void updatePlayableCampaigns() {
        gameDataModel.getPlayableCampaigns().clear();

        List<WsCampaign> playableCampaigns = null;
        try {
            playableCampaigns = CampaignClient.getActiveCampaigns(credentials.getUsername(), credentials.getPassword());
        } catch (ServerException ex) {
            showError(ex);
        }

        if (playableCampaigns != null && !playableCampaigns.isEmpty()) {
            gameDataModel.getPlayableCampaigns().addAll(playableCampaigns);
        }
    }

}
