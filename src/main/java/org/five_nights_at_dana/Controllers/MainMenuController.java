/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/24/2026
 * Time: 8:46 PM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Controllers;
 * Class: MainMenuController
 *
 * Description:
 * Controller for MainMenu.fxml scene which displays the main menu screen.
 * ****************************************
 */

package org.five_nights_at_dana.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.five_nights_at_dana.Core.GameSession;

/**
 * Controller for MainMenu.fxml.
 * Hook point: implement onStartGame() to launch the gameplay scene.
 */
public class MainMenuController {

    @FXML private ImageView backgroundImage;
    @FXML private Label titleLabel;
    @FXML private Button startButton;

    @FXML
    public void initialize() {
        backgroundImage.setImage(new Image(
                getClass().getResourceAsStream("/assets/images/MainMenu.png")));
        // Add intro animations or sound here.
    }

    /**
     * Triggered by the "START GAME" button.
     * Load GameView.fxml and switch the scene on the primary stage.
     */
    @FXML
    private void onStartGame(ActionEvent event) {
        try {
            GameSession.getInstance().startNight();
            Stage stage = (Stage) startButton.getScene().getWindow();
            Parent root = FXMLLoader.load(
                    getClass().getResource("/org/five_nights_at_dana/GameView.fxml"));
            stage.setScene(new Scene(root, 1280, 720));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
