/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/22/2026
 * Time: 10:34 PM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Controllers;
 * Class: GameViewController
 *
 * Description:
 * Controller for GameView.fxml scene which displays the main office gameplay screen.
 * ****************************************
 */

package org.five_nights_at_dana.Controllers;

import javafx.animation.AnimationTimer;
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
import org.five_nights_at_dana.Core.GameSession;

/**
 * Controller for GameView.fxml — the main office gameplay screen.
 */
public class GameViewController {

    // ── HUD
    @FXML private Label timeLabel;
    @FXML private ProgressBar powerBar;
    @FXML private Label powerLabel;
    @FXML private ProgressBar classroomActivityBar;
    @FXML private Label stairChargesLabel;

    // ── OFFICE OBJECTS
    @FXML private ImageView officeBackground;
    @FXML private ImageView coffeeMugImage;

    // ── DOOR BUTTON
    @FXML private Button leftDoorButton;

    // ── SYSTEM BUTTONS
    @FXML private Button cameraButton;
    @FXML private Button ventSealButton;
    @FXML private Button elevatorStopButton;
    @FXML private Button leftStairwellButton;
    @FXML private Button middleStairwellButton;
    @FXML private Button rightStairwellButton;

    // Polls GameSession each frame and refreshes the label only when the time changes
    private AnimationTimer displayUpdater;
    private int lastDisplayedHour = -1;
    private int lastDisplayedMinute = -1;

    @FXML
    public void initialize() {
        officeBackground.setImage(new Image(
                getClass().getResourceAsStream("/assets/images/OfficeView.png")));
        startDisplayUpdater();
    }

    // ── CLOCK DISPLAY
    private void startDisplayUpdater() {
        GameSession session = GameSession.getInstance();
        lastDisplayedHour = session.getHour();
        lastDisplayedMinute = session.getMinute();
        updateTimeLabel(lastDisplayedHour, lastDisplayedMinute);

        displayUpdater = new AnimationTimer() {
            @Override
            public void handle(long now) {
                int h = GameSession.getInstance().getHour();
                int m = GameSession.getInstance().getMinute();
                if (h != lastDisplayedHour || m != lastDisplayedMinute) {
                    lastDisplayedHour = h;
                    lastDisplayedMinute = m;
                    updateTimeLabel(h, m);
                }
            }
        };
        displayUpdater.start();
    }

    private void stopDisplayUpdater() {
        if (displayUpdater != null) {
            displayUpdater.stop();
            displayUpdater = null;
        }
    }

    private void updateTimeLabel(int hour, int minute) {
        timeLabel.setText(String.format("%d:%02d AM", hour, minute));
    }

    // ── HOOK: update full HUD each game tick
    public void updateHUD(double powerPercent, double classActivity, int stairCharges) {
        powerBar.setProgress(powerPercent);
        powerLabel.setText((int)(powerPercent * 100) + "%");
        classroomActivityBar.setProgress(classActivity);
        stairChargesLabel.setText("Stair Charges: " + "● ".repeat(stairCharges).trim());
    }

    // ── DOOR HOOK

    @FXML
    private void onToggleLeftDoor(ActionEvent event) {
        // TODO: call Player.toggleLeftDoor() or DoorWithBlinds logic
    }

    // ── CAMERA HOOK

    @FXML
    private void onOpenCameras(ActionEvent event) {
        stopDisplayUpdater();
        try {
            // Load using instance to access controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/five_nights_at_dana/CameraView.fxml"));
            Parent root = loader.load();

            // Inject system
            CameraViewController controller = loader.getController();
            controller.setCameraSystem(GameSession.getInstance().getCameraSystem());

            Stage stage = (Stage) cameraButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 720));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── VENT HOOK

    @FXML
    private void onToggleVentSeal(ActionEvent event) {
        // TODO: call VentSystem.toggleSeal()
    }

    // ── COFFEE MUG HOOK

    @FXML
    private void onCoffeeMugClicked(MouseEvent event) {
        // TODO: call CoffeeMugObject.interact() or Player.drinkCoffee()
    }

    // ── STAIRWELL & ELEVATOR HOOKS

    @FXML
    private void onLeftStairwellLights(ActionEvent event) {
        // TODO: call LeftStairwell.activateEmergencyLights()
    }

    @FXML
    private void onMiddleStairwellLights(ActionEvent event) {
        // TODO: call MiddleStairwell.activateEmergencyLighting()
    }

    @FXML
    private void onRightStairwellSensor(ActionEvent event) {
        // TODO: call RightStairwell.querySensor() and display result
    }

    @FXML
    private void onElevatorEmergencyStop(ActionEvent event) {
        // TODO: call ElevatorSystem.emergencyStop()
    }

    // ── JUMPSCARE TRIGGER

    public void triggerJumpscare(String studentName) {
        stopDisplayUpdater();
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