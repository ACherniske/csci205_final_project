package org.five_nights_at_dana.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Controller for GameView.fxml — the main office gameplay screen.
 *
 * Each method below is a hook for a specific game mechanic.
 * Wire the actual logic by calling into the relevant system class
 * (Player, Power, VentSystem, etc.) from inside these methods.
 */
public class GameViewController {

    // ── HUD ──────────────────────────────────────────────────────────────
    @FXML private Label timeLabel;
    @FXML private ProgressBar powerBar;
    @FXML private Label powerLabel;
    @FXML private ProgressBar classroomActivityBar;
    @FXML private Label stairChargesLabel;

    // ── OFFICE OBJECTS ────────────────────────────────────────────────────
    @FXML private ImageView officeBackground;
    @FXML private ImageView coffeeMugImage;

    // ── DOOR BUTTONS ──────────────────────────────────────────────────────
    @FXML private Button leftDoorButton;
    @FXML private Button rightDoorButton;

    // ── SYSTEM BUTTONS ────────────────────────────────────────────────────
    @FXML private Button cameraButton;
    @FXML private Button ventSealButton;
    @FXML private Button elevatorStopButton;
    @FXML private Button leftStairwellButton;
    @FXML private Button middleStairwellButton;
    @FXML private Button rightStairwellButton;

    @FXML
    public void initialize() {
        officeBackground.setImage(new Image(
                getClass().getResourceAsStream("/assets/images/OfficeView.png")));
        // Start game loop / timers here.
    }

    // ── HOOK: update HUD every game tick ─────────────────────────────────
    /**
     * Call this each game tick to refresh all HUD displays.
     * Example: controller.updateHUD(power.getPercent(), hour, minute, classActivity, charges);
     */
    public void updateHUD(double powerPercent, int hour, int minute,
                          double classActivity, int stairCharges) {
        powerBar.setProgress(powerPercent);
        powerLabel.setText((int)(powerPercent * 100) + "%");
        timeLabel.setText(String.format("%d:%02d AM", hour, minute));
        classroomActivityBar.setProgress(classActivity);
        stairChargesLabel.setText("Stair Charges: " + "● ".repeat(stairCharges).trim());
    }

    // ── DOOR HOOKS ────────────────────────────────────────────────────────

    /** Hook: toggle the left office door open/closed. */
    @FXML
    private void onToggleLeftDoor(ActionEvent event) {
        // TODO: call Player.toggleLeftDoor() or DoorWithBlinds logic
        // Update button text to reflect state:
        // leftDoorButton.setText(isDoorClosed ? "OPEN LEFT DOOR" : "CLOSE LEFT DOOR");
    }

    /** Hook: toggle the right office door open/closed. */
    @FXML
    private void onToggleRightDoor(ActionEvent event) {
        // TODO: call Player.toggleRightDoor() or DoorWithBlinds logic
    }

    // ── CAMERA HOOK ───────────────────────────────────────────────────────

    /** Hook: flip up the camera tablet — switch to CameraView scene. */
    @FXML
    private void onOpenCameras(ActionEvent event) {
        try {
            Stage stage = (Stage) cameraButton.getScene().getWindow();
            Parent root = FXMLLoader.load(
                    getClass().getResource("/org/five_nights_at_dana/CameraView.fxml"));
            stage.setScene(new Scene(root, 1280, 720));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── VENT HOOK ─────────────────────────────────────────────────────────

    /** Hook: toggle the vent seal on/off (VentSystem). */
    @FXML
    private void onToggleVentSeal(ActionEvent event) {
        // TODO: call VentSystem.toggleSeal()
        // Update label: ventSealButton.setText(sealed ? "UNSEAL VENT" : "SEAL VENT");
    }

    // ── COFFEE MUG HOOK ───────────────────────────────────────────────────

    /** Hook: player clicks the coffee mug to restore alertness. */
    @FXML
    private void onCoffeeMugClicked(MouseEvent event) {
        // TODO: call CoffeeMugObject.interact() or Player.drinkCoffee()
    }

    // ── STAIRWELL & ELEVATOR HOOKS ────────────────────────────────────────

    /** Hook: activate emergency lights in the left stairwell (limited uses). */
    @FXML
    private void onLeftStairwellLights(ActionEvent event) {
        // TODO: call LeftStairwell.activateEmergencyLights()
    }

    /** Hook: activate emergency lighting in the middle stairwell (stuns students). */
    @FXML
    private void onMiddleStairwellLights(ActionEvent event) {
        // TODO: call MiddleStairwell.activateEmergencyLighting()
    }

    /** Hook: query the right stairwell motion sensor. */
    @FXML
    private void onRightStairwellSensor(ActionEvent event) {
        // TODO: call RightStairwell.querySensor() and display result
    }

    /** Hook: trigger an elevator emergency stop. */
    @FXML
    private void onElevatorEmergencyStop(ActionEvent event) {
        // TODO: call ElevatorSystem.emergencyStop()
    }

    // ── JUMPSCARE TRIGGER (called by game logic, not a button) ────────────

    /** Call this from the game loop when a student reaches the office. */
    public void triggerJumpscare(String studentName) {
        try {
            Stage stage = (Stage) officeBackground.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/five_nights_at_dana/JumpscareView.fxml"));
            Parent root = loader.load();
            JumpscareController jc = loader.getController();
            jc.startJumpscare(studentName);
            stage.setScene(new Scene(root, 1280, 720));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
