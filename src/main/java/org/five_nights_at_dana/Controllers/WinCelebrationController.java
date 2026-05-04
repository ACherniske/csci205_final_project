/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Controllers
 * Class: WinCelebrationController
 *
 * Description:
 * Short win animation ("6:00 AM" + fireworks) then transitions to WinView.
 * ****************************************
 */

package org.five_nights_at_dana.Controllers;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.five_nights_at_dana.Managers.AudioManager;
import org.five_nights_at_dana.UI.FadeUtil;
import org.five_nights_at_dana.UI.FireworksPane;

/**
 * Controller for WinCelebration.fxml.
 */
public class WinCelebrationController {

    @FXML private AnchorPane root;
    @FXML private Label timeLabel;

    private boolean transitioning = false;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            FadeUtil.fadeIn(root, 0.35);

            FireworksPane fireworks = new FireworksPane();
            root.getChildren().add(fireworks);
            AnchorPane.setTopAnchor(fireworks, 0.0);
            AnchorPane.setBottomAnchor(fireworks, 0.0);
            AnchorPane.setLeftAnchor(fireworks, 0.0);
            AnchorPane.setRightAnchor(fireworks, 0.0);

            // Launch fireworks for a couple seconds then transition.
            fireworks.start(Duration.seconds(2.6), this::goToWinScreen);

            // Small extra hold so the player can read "6:00 AM".
            PauseTransition linger = new PauseTransition(Duration.seconds(3.1));
            linger.setOnFinished(e -> goToWinScreen());
            linger.play();
        });
        AudioManager.play("yippie", true);
    }

    @FXML
    private void onSkip(MouseEvent e) {
        goToWinScreen();
    }

    private void goToWinScreen() {
        if (transitioning) {
            return;
        }
        transitioning = true;

        try {
            Stage stage = (Stage) root.getScene().getWindow();
            AudioManager.play("hooray", true);
            Parent nextRoot = FXMLLoader.load(getClass().getResource("/org/five_nights_at_dana/WinView.fxml"));
            FadeUtil.fadeOutAndSwitch(root, nextRoot, 1280, 720, 0.30);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
