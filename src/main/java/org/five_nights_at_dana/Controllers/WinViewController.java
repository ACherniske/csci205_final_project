/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Controllers
 * Class: WinViewController
 *
 * Description:
 * Dedicated win screen after the end-of-night celebration.
 * ****************************************
 */

package org.five_nights_at_dana.Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.five_nights_at_dana.UI.FadeUtil;

/**
 * Controller for WinView.fxml.
 */
public class WinViewController {

    @FXML private AnchorPane root;
    @FXML private Button mainMenuButton;

    @FXML
    public void initialize() {
        Platform.runLater(() -> FadeUtil.fadeIn(root, 0.35));
    }

    @FXML
    private void onReturnToMainMenu(ActionEvent e) {
        try {
            Stage stage = (Stage) mainMenuButton.getScene().getWindow();
            Parent nextRoot = FXMLLoader.load(getClass().getResource("/org/five_nights_at_dana/MainMenu.fxml"));
            FadeUtil.fadeOutAndSwitch(root, nextRoot, 1280, 720, 0.30);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
