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

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.five_nights_at_dana.Core.GameSession;
import org.five_nights_at_dana.Managers.AudioManager;
import org.five_nights_at_dana.UI.FadeUtil;

/**
 * Controller for MainMenu.fxml.
 * Hook point: implement onStartGame() to launch the gameplay scene.
 */
public class MainMenuController {

    @FXML private ImageView backgroundImage;
    @FXML private Label titleLabel;
    @FXML private Button startButton;
    @FXML private Slider masterVolSlider;
    @FXML private Slider sfxVolSlider;
    @FXML private Slider musicVolSlider;

    /**
     * Initializes the main menu scene after FXML load.
     * Sets the background image and provides a hook point for intro audio/animations.
     * Sets Sliders for adjusting volume levels
     */
    @FXML
    public void initialize() {
        backgroundImage.setImage(new Image(
                getClass().getResourceAsStream("/assets/images/MainMenuBackground.png")));

        masterVolSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            AudioManager.setMasterVolume(newValue.doubleValue());
        });

        sfxVolSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            AudioManager.setSfxVolume(newValue.doubleValue());
        });

        musicVolSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            AudioManager.setMusicVolume(newValue.doubleValue());
        });

        // Add intro animations or sound here.
        AudioManager.playLoop("menu", 0.4, false);
        Platform.runLater(() -> FadeUtil.fadeIn(startButton.getParent(), 0.35));
    }

    /**
     * Triggered by the "START GAME" button.
     * Load GameView.fxml and switch the scene on the primary stage.
     */
    @FXML
    private void onStartGame(ActionEvent event) {
        try {
            AudioManager.stopLoop("menu");
            GameSession.getInstance().startNight();
            Parent root = FXMLLoader.load(
                    getClass().getResource("/org/five_nights_at_dana/GameView.fxml"));

            FadeUtil.fadeOutAndSwitch(startButton, root, 1280, 720);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
