/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/22/2026
 * Time: 9:30 PM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Controllers;
 * Class: CameraViewController
 *
 * Description:
 * Controller for CameraView.fxml scene which displays camera UI.
 * ****************************************
 */

package org.five_nights_at_dana.Controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.five_nights_at_dana.Core.GameSession;
import org.five_nights_at_dana.Core.GameState;
import org.five_nights_at_dana.Managers.AudioManager;
import org.five_nights_at_dana.Managers.ObservationManager;
import org.five_nights_at_dana.Rendering.Camera.CameraConfig;
import org.five_nights_at_dana.Rendering.Camera.CameraSystem;
import org.five_nights_at_dana.UI.FadeUtil;
import org.five_nights_at_dana.UI.NotificationToastOverlay;


/**
 * Controller for CameraView.fxml — the security camera tablet.
 * Floor selection swaps the background map image.
 * Camera buttons swap the feed image and update the active camera label.
 * "Lower Cameras" returns to GameView.
 */
public class CameraViewController {

    private CameraSystem cameraSystem;
    private boolean jumpscareTransitionInProgress = false;

    /**
     * Injects the camera system dependency and wires button actions.
     * Also registers session callbacks so jumpscare/win/game-over
     * can transition while cameras are up.
     *
     * @param system active camera system
     */
    public void setCameraSystem(CameraSystem system) {
        this.cameraSystem = system;

        buttonToIdMap.forEach((btn, id) -> btn.setOnAction(e -> {
            cameraSystem.setActiveCamera(id);
            updateView();
        }));

        // Handle jumpscare/win/gameover while camera view is active
        GameSession session = GameSession.getInstance();
        session.setOnJumpscare(this::triggerJumpscare);
        session.setOnWin(this::goToWinCelebration);
        session.setOnGameOver(this::returnToMainMenu);

        updateView();
    }

    // ── UI ELEMENTS ──────────────────────────────────────────────────────
    @FXML private ImageView floorMapImage;
    @FXML private ImageView cameraFeedImage;
    @FXML private Label activeCameraLabel;
    @FXML private Button floor1Button, floor2Button, floor3Button, lowerCamerasButton;

    // Floor Panes for quick visibility toggling
    @FXML private Pane floor1Pane, floor2Pane, floor3Pane;

    // Camera Buttons
    @FXML private Button cam1A, cam1B, cam1C, cam1D, cam1E, cam1F, camSL1, camSM1, camSR1;
    @FXML private Button cam2A, cam2B, cam2C, camBucky, camSL2, camSM2, camSR2;
    @FXML private Button cam3A, cam3B, cam3C, cam3D, camSL3, camSM3, camSR3;

    private final Map<Button, String> buttonToIdMap = new HashMap<>();

    /**
     * Initializes the camera tablet controller after FXML load.
     * Sets up the button->camera mapping and installs the toast overlay.
     */
    @FXML
    public void initialize() {
        initializeButtonMap();
        onSelectFloor1(null);

        AnchorPane root = (AnchorPane) activeCameraLabel.getParent();
        NotificationToastOverlay.install(root);
    }

    /**
     * Builds the mapping from each camera button to its logical camera ID.
     */
    private void initializeButtonMap() {
        // Floor 1
        buttonToIdMap.put(cam1A, "1A");
        buttonToIdMap.put(cam1B, "1B");
        buttonToIdMap.put(cam1C, "1C");
        buttonToIdMap.put(cam1D, "1D");
        buttonToIdMap.put(cam1E, "1E");
        buttonToIdMap.put(cam1F, "1F");
        buttonToIdMap.put(camSL1, "SL1");
        buttonToIdMap.put(camSM1, "SM1");
        buttonToIdMap.put(camSR1, "SR1");
        // Floor 2
        buttonToIdMap.put(cam2A, "2A");
        buttonToIdMap.put(cam2B, "2B");
        buttonToIdMap.put(cam2C, "2C");
        buttonToIdMap.put(camBucky, "BUCKY");
        buttonToIdMap.put(camSL2, "SL2");
        buttonToIdMap.put(camSM2, "SM2");
        buttonToIdMap.put(camSR2, "SR2");
        // Floor 3
        buttonToIdMap.put(cam3A, "3A");
        buttonToIdMap.put(cam3B, "3B");
        buttonToIdMap.put(cam3C, "3C");
        buttonToIdMap.put(cam3D, "3D");
        buttonToIdMap.put(camSL3, "SL3");
        buttonToIdMap.put(camSM3, "SM3");
        buttonToIdMap.put(camSR3, "SR3");
    }

    /**
     * Refreshes the feed image and label to match the currently active camera.
     */
    private void updateView() {
        cameraFeedImage.setImage(cameraSystem.getFeedImage());
        activeCameraLabel.setText(cameraSystem.getActiveCamera().label());

        // Mark the current camera location as being "watched" for AI stalling.
        ObservationManager.setCamerasUp(true);
        ObservationManager.setWatchedLocation(CameraConfig.getLocation(cameraSystem.getActiveCamera().id()));
        AudioManager.play("change_cams", true);
        // Watching CAM 3D resets the runner's activity meter
        if ("3D".equals(cameraSystem.getActiveCamera().id())) {
            GameSession.getInstance().getClassroom().resetActivity();
        }
    }

    // ── FLOOR SELECTION ───────────────────────────────────────────────────

    /**
     * Switches the map UI to floor 1.
     *
     * @param e action event
     */
    @FXML private void onSelectFloor1(ActionEvent e) {
        switchFloor(1, "Map_Floor1.png", floor1Pane);
    }

    /**
     * Switches the map UI to floor 2.
     *
     * @param e action event
     */
    @FXML private void onSelectFloor2(ActionEvent e) {
        switchFloor(2, "Map_Floor2.png", floor2Pane);
    }

    /**
     * Switches the map UI to floor 3.
     *
     * @param e action event
     */
    @FXML private void onSelectFloor3(ActionEvent e) {
        switchFloor(3, "Map_Floor3.png", floor3Pane);
    }

    /**
     * Updates the floor tab styles, visible button pane, and map image.
     *
     * @param floor      selected floor number
     * @param mapName    map asset filename
     * @param activePane the pane containing the active floor's camera buttons
     */
    private void switchFloor(int floor, String mapName, Pane activePane) {
        floorMapImage.setImage(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/assets/images/" + mapName)))
        );

        // Update tab styles
        floor1Button.setStyle(floor == 1 ? STYLE_ACTIVE : STYLE_INACTIVE);
        floor2Button.setStyle(floor == 2 ? STYLE_ACTIVE : STYLE_INACTIVE);
        floor3Button.setStyle(floor == 3 ? STYLE_ACTIVE : STYLE_INACTIVE);

        // Update visibility
        floor1Pane.setVisible(activePane == floor1Pane);
        floor2Pane.setVisible(activePane == floor2Pane);
        floor3Pane.setVisible(activePane == floor3Pane);
    }

    // ── LOWER CAMERAS ─────────────────────────────────────────────────────

    /**
     * Lowers the camera tablet and returns to the office view.
     *
     * @param event action event
     */
    @FXML
    private void onLowerCameras(ActionEvent event) {
        GameSession.getInstance().setCurrentState(GameState.PLAYING);
        ObservationManager.setCamerasUp(false);
        returnToGameView();
    }

    /**
     * Loads the office scene.
     */
    private void returnToGameView() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/five_nights_at_dana/GameView.fxml"));
            // Lower cameras should be instant (no fade).
            Stage stage = (Stage) lowerCamerasButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 720));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the main menu scene.
     */
    private void returnToMainMenu() {
        try {
            ObservationManager.setCamerasUp(false);
            Parent root = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource("/org/five_nights_at_dana/MainMenu.fxml"))
            );
            FadeUtil.fadeOutAndSwitch(lowerCamerasButton, root, 1280, 720, 0.30);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Goes to the short win animation scene ("6:00 AM" + fireworks), then to the win screen.
     */
    private void goToWinCelebration() {
        try {
            ObservationManager.setCamerasUp(false);
            Parent root = FXMLLoader.load(getClass().getResource("/org/five_nights_at_dana/WinCelebration.fxml"));
            FadeUtil.fadeOutAndSwitch(lowerCamerasButton, root, 1280, 720, 0.30);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Triggers a jumpscare transition while the camera tablet is visible.
     * A short static flash is shown before switching to the jumpscare scene.
     *
     * @param studentQuestion question text displayed during the jumpscare
     */
    private void triggerJumpscare(String studentQuestion) {
        if (jumpscareTransitionInProgress) {
            return;
        }
        jumpscareTransitionInProgress = true;

        // Freeze camera interaction and flash static briefly (FNAF-style)
        setInputsDisabled(true);
        cameraFeedImage.setImage(generateStaticImage(320, 180));
        activeCameraLabel.setText("SIGNAL LOST");

        PauseTransition holdStatic = new PauseTransition(Duration.millis(500));
        holdStatic.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/five_nights_at_dana/JumpscareView.fxml"));
                Parent root = loader.load();
                JumpscareController jc = loader.getController();
                jc.startJumpscare(studentQuestion);

                ObservationManager.setCamerasUp(false);
                FadeUtil.fadeOutAndSwitch(lowerCamerasButton, root, 1280, 720, 0.18);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        holdStatic.play();
    }

    /**
     * Enables or disables all camera UI inputs (floor tabs, lower button, and camera buttons).
     *
     * @param disabled true to disable inputs
     */
    private void setInputsDisabled(boolean disabled) {
        floor1Button.setDisable(disabled);
        floor2Button.setDisable(disabled);
        floor3Button.setDisable(disabled);
        lowerCamerasButton.setDisable(disabled);

        for (Button b : buttonToIdMap.keySet()) {
            b.setDisable(disabled);
        }
    }

    /**
     * Generates a small grayscale noise image to simulate camera static.
     * The ImageView will scale it up automatically.
     *
     * @param width  image width in pixels
     * @param height image height in pixels
     * @return generated noise image
     */
    private Image generateStaticImage(int width, int height) {
        WritableImage image = new WritableImage(width, height);
        PixelWriter writer = image.getPixelWriter();

        Random r = new Random();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int v = r.nextInt(256);
                // Slightly reduce brightness to keep it less "white flash"
                double g = (v / 255.0) * 0.85;
                writer.setColor(x, y, Color.color(g, g, g));
            }
        }

        return image;
    }

    private static final String STYLE_ACTIVE = "-fx-font-family: 'Courier New'; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ffcc00; -fx-background-color: #2d2d2d; -fx-border-color: #ffcc00; -fx-border-width: 2px; -fx-cursor: hand;";
    private static final String STYLE_INACTIVE = "-fx-font-family: 'Courier New'; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #aaaaaa; -fx-background-color: #1a1a1a; -fx-border-color: #555555; -fx-border-width: 2px; -fx-cursor: hand;";
}
