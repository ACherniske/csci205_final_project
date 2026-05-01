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
 * Controller for GameView.fxml — the main office gameplay screen.
 * Embeds the GamePane rendering canvas, wires all player actions to
 * their respective subsystems in GameSession, and updates the HUD.
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
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.five_nights_at_dana.Core.GamePane;
import org.five_nights_at_dana.Core.GameSession;
import org.five_nights_at_dana.Core.GameState;
import org.five_nights_at_dana.Systems.Stairwells.Stairwell;

public class GameViewController {

    // ── HUD ──────────────────────────────────────────────────────────
    @FXML private Label       timeLabel;
    @FXML private ProgressBar powerBar;
    @FXML private Label       powerLabel;
    @FXML private ProgressBar classroomActivityBar;
    @FXML private Label       stairChargesLabel;

    // ── OFFICE OBJECTS ────────────────────────────────────────────────
    @FXML private ImageView officeBackground;
    @FXML private ImageView coffeeMugImage;

    // ── BUTTONS ───────────────────────────────────────────────────────
    @FXML private Button leftDoorButton;
    @FXML private Button cameraButton;
    @FXML private Button ventSealButton;
    @FXML private Button elevatorStopButton;
    @FXML private Button leftStairwellButton;
    @FXML private Button middleStairwellButton;
    @FXML private Button rightStairwellButton;

    private GamePane    gamePane;
    private GameSession session;

    @FXML
    public void initialize() {
        officeBackground.setImage(new Image(
                getClass().getResourceAsStream("/assets/images/OfficeView.png")));

        session = GameSession.getInstance();
        // Do not override state here — startNight() sets PLAYING for a fresh game,
        // and CameraViewController.onLowerCameras() restores it when returning from cameras.

        // Add the rendering canvas between the background ImageView and HUD controls
        gamePane = new GamePane();
        AnchorPane root = (AnchorPane) timeLabel.getParent();
        root.getChildren().add(1, gamePane);
        AnchorPane.setTopAnchor(gamePane,    0.0);
        AnchorPane.setBottomAnchor(gamePane, 0.0);
        AnchorPane.setLeftAnchor(gamePane,   0.0);
        AnchorPane.setRightAnchor(gamePane,  0.0);

        // Register event callbacks with the session
        session.setOnFrameRender(this::onFrame);
        session.setOnJumpscare(this::triggerJumpscare);
        session.setOnWin(this::handleWin);
        session.setOnGameOver(this::handleGameOver);

        refreshDoorButton();
    }

    // ── Frame callback (called every frame by GameSession's AnimationTimer) ──

    private void onFrame() {
        gamePane.render();
        refreshHUD();
    }

    private void refreshHUD() {
        powerBar.setProgress(session.getPower());
        powerLabel.setText((int)(session.getPower() * 100) + "%");
        classroomActivityBar.setProgress(session.getClassroom().getActivityPercentage());
        int charges = session.getStairSystem().getLightCharges();
        stairChargesLabel.setText("Stair Charges: " + "● ".repeat(charges).trim());
        timeLabel.setText(String.format("%d:%02d AM", session.getHour(), session.getMinute()));
    }

    // ── DOOR ──────────────────────────────────────────────────────────

    @FXML
    private void onToggleLeftDoor(ActionEvent event) {
        session.toggleLeftDoor();
        refreshDoorButton();
    }

    private void refreshDoorButton() {
        boolean closed = session.isLeftDoorClosed();
        leftDoorButton.setText((closed ? "OPEN" : "CLOSE") + "\nDOOR");
    }

    // ── CAMERAS ───────────────────────────────────────────────────────

    @FXML
    private void onOpenCameras(ActionEvent event) {
        if (session.getCurrentState() != GameState.PLAYING) return;
        session.setCurrentState(GameState.VIEWING_CAMERAS);
        // Pause the render callback while the camera scene is visible
        session.setOnFrameRender(null);
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/five_nights_at_dana/CameraView.fxml"));
            Parent root = loader.load();
            CameraViewController controller = loader.getController();
            controller.setCameraSystem(session.getCameraSystem());
            Stage stage = (Stage) cameraButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 720));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── VENT ──────────────────────────────────────────────────────────

    @FXML
    private void onToggleVentSeal(ActionEvent event) {
        session.getVents().sealVent();
    }

    // ── COFFEE MUG ────────────────────────────────────────────────────

    @FXML
    private void onCoffeeMugClicked(MouseEvent event) {
        session.refillCoffee(0.3);
    }

    // ── STAIRWELLS ────────────────────────────────────────────────────

    @FXML
    private void onLeftStairwellLights(ActionEvent event) {
        session.getStairSystem().activateLights(Stairwell.LEFT);
    }

    @FXML
    private void onMiddleStairwellLights(ActionEvent event) {
        session.getStairSystem().activateLights(Stairwell.MIDDLE);
    }

    @FXML
    private void onRightStairwellSensor(ActionEvent event) {
        session.getStairSystem().activateLights(Stairwell.RIGHT);
    }

    // ── ELEVATOR ──────────────────────────────────────────────────────

    @FXML
    private void onElevatorEmergencyStop(ActionEvent event) {
        session.getElevator().emergencyStop();
    }

    // ── WIN / LOSE ────────────────────────────────────────────────────

    private void handleWin() {
        // TODO: load dedicated win screen
        System.out.println("YOU SURVIVED THE NIGHT!");
        returnToMainMenu();
    }

    private void handleGameOver() {
        System.out.println("GAME OVER — power out");
        returnToMainMenu();
    }

    private void returnToMainMenu() {
        try {
            Stage stage = (Stage) officeBackground.getScene().getWindow();
            Parent root = FXMLLoader.load(
                    getClass().getResource("/org/five_nights_at_dana/MainMenu.fxml"));
            stage.setScene(new Scene(root, 1280, 720));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── JUMPSCARE ─────────────────────────────────────────────────────

    public void triggerJumpscare(String studentQuestion) {
        try {
            Stage stage = (Stage) officeBackground.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/five_nights_at_dana/JumpscareView.fxml"));
            Parent root = loader.load();
            JumpscareController jc = loader.getController();
            jc.startJumpscare(studentQuestion);
            stage.setScene(new Scene(root, 1280, 720));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** External hook to push HUD values if needed outside the frame callback. */
    public void updateHUD(double powerPercent, double classActivity, int stairCharges) {
        powerBar.setProgress(powerPercent);
        powerLabel.setText((int)(powerPercent * 100) + "%");
        classroomActivityBar.setProgress(classActivity);
        stairChargesLabel.setText("Stair Charges: " + "● ".repeat(stairCharges).trim());
    }
}
