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

/**
 * Controller for CameraView.fxml — the security camera tablet.
 *
 * Floor selection swaps the background map image.
 * Camera buttons swap the feed image and update the active camera label.
 * "Lower Cameras" returns to GameView.
 */
public class CameraViewController {

    // ── FLOOR MAP & FEED ──────────────────────────────────────────────────
    @FXML private ImageView floorMapImage;
    @FXML private ImageView cameraFeedImage;
    @FXML private Label activeCameraLabel;

    // ── FLOOR TABS ────────────────────────────────────────────────────────
    @FXML private Button floor1Button;
    @FXML private Button floor2Button;
    @FXML private Button floor3Button;
    @FXML private Button lowerCamerasButton;

    // ── FLOOR 1 CAMERA BUTTONS ────────────────────────────────────────────
    @FXML private Button cam1Button;
    @FXML private Button cam2Button;
    @FXML private Button cam3Button;
    @FXML private Button cam4Button;

    // ── FLOOR 2 CAMERA BUTTONS ────────────────────────────────────────────
    @FXML private Button cam5Button;
    @FXML private Button cam6Button;
    @FXML private Button cam7Button;

    // ── FLOOR 3 CAMERA BUTTONS ────────────────────────────────────────────
    @FXML private Button cam8Button;
    @FXML private Button cam9Button;
    @FXML private Button cam10Button;
    @FXML private Button cam11Button;
    @FXML private Button cam12Button;

    private static final String STYLE_ACTIVE_FLOOR =
            "-fx-font-family: 'Courier New'; -fx-font-size: 14px; -fx-font-weight: bold;" +
            "-fx-text-fill: #ffcc00; -fx-background-color: #2d2d2d;" +
            "-fx-border-color: #ffcc00; -fx-border-width: 2px; -fx-cursor: hand;";
    private static final String STYLE_INACTIVE_FLOOR =
            "-fx-font-family: 'Courier New'; -fx-font-size: 14px; -fx-font-weight: bold;" +
            "-fx-text-fill: #aaaaaa; -fx-background-color: #1a1a1a;" +
            "-fx-border-color: #555555; -fx-border-width: 2px; -fx-cursor: hand;";

    @FXML
    public void initialize() {
        onSelectFloor1(null); // default to floor 1
    }

    // ── FLOOR SELECTION ───────────────────────────────────────────────────

    @FXML
    private void onSelectFloor1(ActionEvent event) {
        floorMapImage.setImage(new Image(getClass().getResourceAsStream(
                "/assets/images/FirstFloorCameraSelectionView.png")));
        setFloorButtonStyles(1);
        setFloorCamerasVisible(1);
    }

    @FXML
    private void onSelectFloor2(ActionEvent event) {
        floorMapImage.setImage(new Image(getClass().getResourceAsStream(
                "/assets/images/SecondFloorCameraSelectionView.png")));
        setFloorButtonStyles(2);
        setFloorCamerasVisible(2);
    }

    @FXML
    private void onSelectFloor3(ActionEvent event) {
        floorMapImage.setImage(new Image(getClass().getResourceAsStream(
                "/assets/images/ThirdFloorCameraSelectionView.png")));
        setFloorButtonStyles(3);
        setFloorCamerasVisible(3);
    }

    private void setFloorButtonStyles(int active) {
        floor1Button.setStyle(active == 1 ? STYLE_ACTIVE_FLOOR : STYLE_INACTIVE_FLOOR);
        floor2Button.setStyle(active == 2 ? STYLE_ACTIVE_FLOOR : STYLE_INACTIVE_FLOOR);
        floor3Button.setStyle(active == 3 ? STYLE_ACTIVE_FLOOR : STYLE_INACTIVE_FLOOR);
    }

    private void setFloorCamerasVisible(int floor) {
        cam1Button.setVisible(floor == 1);
        cam2Button.setVisible(floor == 1);
        cam3Button.setVisible(floor == 1);
        cam4Button.setVisible(floor == 1);
        cam5Button.setVisible(floor == 2);
        cam6Button.setVisible(floor == 2);
        cam7Button.setVisible(floor == 2);
        cam8Button.setVisible(floor == 3);
        cam9Button.setVisible(floor == 3);
        cam10Button.setVisible(floor == 3);
        cam11Button.setVisible(floor == 3);
        cam12Button.setVisible(floor == 3);
    }

    // ── CAMERA FEED SELECTION ─────────────────────────────────────────────

    /** Call this to show a specific camera feed. */
    private void showCameraFeed(String imageName, String label) {
        cameraFeedImage.setImage(new Image(getClass().getResourceAsStream(
                "/assets/images/" + imageName)));
        activeCameraLabel.setText(label);
        // TODO: notify CameraSystem which camera is active (costs power)
    }

    @FXML private void onSelectCamera1(ActionEvent e)  { showCameraFeed("Camera1EntranceHall.png",      "CAM 1 — Entrance Hall"); }
    @FXML private void onSelectCamera2(ActionEvent e)  { showCameraFeed("Camera2MiddleStairWell1.png",   "CAM 2 — Middle Stairwell 1"); }
    @FXML private void onSelectCamera3(ActionEvent e)  { showCameraFeed("Camera3Hallway1A.png",          "CAM 3 — Hallway 1A"); }
    @FXML private void onSelectCamera4(ActionEvent e)  { showCameraFeed("Camera4Elevator1.png",          "CAM 4 — Elevator 1"); }
    @FXML private void onSelectCamera5(ActionEvent e)  { showCameraFeed("Camera5Leftstairwell2.png",     "CAM 5 — Left Stairwell 2"); }
    @FXML private void onSelectCamera6(ActionEvent e)  { showCameraFeed("Camera6Hallway2A.png",          "CAM 6 — Hallway 2A"); }
    @FXML private void onSelectCamera7(ActionEvent e)  { showCameraFeed("Camera7Hallway2B.png",          "CAM 7 — Hallway 2B"); }
    @FXML private void onSelectCamera8(ActionEvent e)  { showCameraFeed("Camera8MiddleStairwell3.png",   "CAM 8 — Middle Stairwell 3"); }
    @FXML private void onSelectCamera9(ActionEvent e)  { showCameraFeed("Camera9Hallway3B.png",          "CAM 9 — Hallway 3B"); }
    @FXML private void onSelectCamera10(ActionEvent e) { showCameraFeed("Camera10RightStairwell3.png",   "CAM 10 — Right Stairwell 3"); }
    @FXML private void onSelectCamera11(ActionEvent e) { showCameraFeed("Camera11OfficeDoor.png",        "CAM 11 — Office Door"); }
    @FXML private void onSelectCamera12(ActionEvent e) { showCameraFeed("Camera12Classroom.png",         "CAM 12 — Classroom"); }

    // ── LOWER CAMERAS ─────────────────────────────────────────────────────

    /** Hook: lower the camera tablet and return to the office. */
    @FXML
    private void onLowerCameras(ActionEvent event) {
        try {
            Stage stage = (Stage) lowerCamerasButton.getScene().getWindow();
            Parent root = FXMLLoader.load(
                    getClass().getResource("/org/five_nights_at_dana/GameView.fxml"));
            stage.setScene(new Scene(root, 1280, 720));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
