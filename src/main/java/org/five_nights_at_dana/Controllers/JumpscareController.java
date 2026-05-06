/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/26/2026
 * Time: 9:22 PM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Controllers;
 * Class: JumpscareController
 *
 * Description:
 * Controller for JumpscareView.fxml scene which plays the jumpscare animation
 * ****************************************
 */

package org.five_nights_at_dana.Controllers;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.five_nights_at_dana.Managers.AudioManager;
import org.five_nights_at_dana.UI.FadeUtil;

/**
 * Controller for JumpscareView.fxml.
 * Call startJumpscare(studentName) from GameViewController when a student
 * reaches the office. The sequence runs automatically then returns to the
 * main menu (or a dedicated Game Over screen when one is added).
 */
public class JumpscareController {

    @FXML private ImageView jumpscareImage;
    @FXML private Label gameOverLabel;
    @FXML private Label studentNameLabel;

    /**
     * Initializes the jumpscare scene after FXML load.
     * Preloads the scare image so it can display immediately.
     */
    @FXML
    public void initialize() {
        jumpscareImage.setImage(new Image(
                getClass().getResourceAsStream("/assets/images/JumpscareScreen.png")));

        Platform.runLater(() -> {
            if (jumpscareImage.getScene() != null) {
                FadeUtil.fadeIn(jumpscareImage.getScene().getRoot(), 0.18);
            }
        });
    }

    /**
     * Hook: called by GameViewController when the jumpscare triggers.
     * @param studentName the name of the student who caught the player
     */
    public void startJumpscare(String studentName) {
        // If called before this view is attached to a scene (common during fade scene switches),
        // defer until the next FX pulse.
        if (jumpscareImage.getScene() == null) {
            Platform.runLater(() -> startJumpscare(studentName));
            return;
        }

        studentNameLabel.setText("Caught by " + studentName);
        AudioManager.play("jumpscare", true);

        // 1s scare image shown → fade in "GAME OVER" → wait → return to menu
        PauseTransition holdScare = new PauseTransition(Duration.seconds(1.5));

        FadeTransition fadeGameOver = new FadeTransition(Duration.seconds(1.0), gameOverLabel);
        fadeGameOver.setFromValue(0.0);
        fadeGameOver.setToValue(1.0);

        FadeTransition fadeStudent = new FadeTransition(Duration.seconds(0.8), studentNameLabel);
        fadeStudent.setFromValue(0.0);
        fadeStudent.setToValue(1.0);

        PauseTransition holdGameOver = new PauseTransition(Duration.seconds(3.0));

        SequentialTransition sequence = new SequentialTransition(
                holdScare, fadeGameOver, fadeStudent, holdGameOver);

        sequence.setOnFinished(e -> returnToMainMenu());
        sequence.play();
    }

    /**
     * Returns to the main menu after the jumpscare sequence finishes.
     * Hook point: replace this with a dedicated Game Over screen if desired.
     */
    private void returnToMainMenu() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/org/five_nights_at_dana/MainMenu.fxml"));
            FadeUtil.fadeOutAndSwitch(jumpscareImage, root, 1280, 720, 0.25);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
