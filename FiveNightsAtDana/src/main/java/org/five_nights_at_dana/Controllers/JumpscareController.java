package org.five_nights_at_dana.Controllers;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for JumpscareView.fxml.
 *
 * Call startJumpscare(studentName) from GameViewController when a student
 * reaches the office. The sequence runs automatically then returns to the
 * main menu (or a dedicated Game Over screen when one is added).
 */
public class JumpscareController {

    @FXML private ImageView jumpscareImage;
    @FXML private Label gameOverLabel;
    @FXML private Label studentNameLabel;

    @FXML
    public void initialize() {
        jumpscareImage.setImage(new Image(
                getClass().getResourceAsStream("/assets/images/JumpscareScreen.png")));
    }

    /**
     * Hook: called by GameViewController when the jumpscare triggers.
     * @param studentName the name of the student who caught the player
     */
    public void startJumpscare(String studentName) {
        studentNameLabel.setText("Caught by " + studentName);

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

    /** Hook: override or extend to go to a custom Game Over screen instead. */
    private void returnToMainMenu() {
        try {
            Stage stage = (Stage) jumpscareImage.getScene().getWindow();
            Parent root = FXMLLoader.load(
                    getClass().getResource("/org/five_nights_at_dana/MainMenu.fxml"));
            stage.setScene(new Scene(root, 1280, 720));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
