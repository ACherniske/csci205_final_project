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

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.five_nights_at_dana.Core.GamePane;
import org.five_nights_at_dana.Core.GameSession;
import org.five_nights_at_dana.Core.GameState;
import org.five_nights_at_dana.Managers.AudioManager;
import org.five_nights_at_dana.UI.FadeUtil;
import org.five_nights_at_dana.UI.NotificationToastOverlay;
import org.five_nights_at_dana.Systems.Stairwells.Stairwell;

public class GameViewController {

    // ── HUD ──────────────────────────────────────────────────────────
    @FXML private Label       timeLabel;
    @FXML private Canvas      powerBatteryCanvas;
    @FXML private Label       powerLabel;
    @FXML private ProgressBar classroomActivityBar;

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

    /**
     * Initializes the office view controller after FXML loading.
     * Wires callbacks to the active {@link GameSession} and installs the toast overlay.
     */
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

        // Discord-style toasts for the notification system
        NotificationToastOverlay.install(root);

        refreshDoorButton();
        drawBattery(session.getPower());

        Platform.runLater(() -> FadeUtil.fadeIn(root, 0.35));
    }

    // ── Frame callback (called every frame by GameSession's AnimationTimer) ──

    /**
     * Per-frame render callback registered with {@link GameSession}.
     */
    private void onFrame() {
        gamePane.render();
        refreshHUD();
    }

    /**
        * Refreshes HUD elements (power, time, activity).
     */
    private void refreshHUD() {
        double power = session.getPower();
        drawBattery(power);
        powerLabel.setText((int)(power * 100) + "%");
        classroomActivityBar.setProgress(session.getClassroom().getActivityPercentage());
        timeLabel.setText(String.format("%d:%02d AM", session.getHour(), session.getMinute()));
    }

    private void drawBattery(double powerFraction) {
        if (powerBatteryCanvas == null) return;
        double clamped = Math.max(0.0, Math.min(1.0, powerFraction));

        GraphicsContext gc = powerBatteryCanvas.getGraphicsContext2D();
        double w = powerBatteryCanvas.getWidth();
        double h = powerBatteryCanvas.getHeight();
        gc.clearRect(0, 0, w, h);

        Color color;
        if (clamped <= 0.20) {
            color = Color.web("#ff4444");
        } else if (clamped <= 0.50) {
            color = Color.web("#ffcc00");
        } else {
            color = Color.web("#00ff41");
        }

        double tipW = Math.max(6.0, Math.round(w * 0.10));
        double bodyW = w - tipW - 1.0;
        double bodyX = 0.5;
        double bodyY = 1.5;
        double bodyH = h - 3.0;

        // Outline
        gc.setStroke(color);
        gc.setLineWidth(2.0);
        gc.strokeRoundRect(bodyX, bodyY, bodyW, bodyH, 3.0, 3.0);

        double tipX = bodyX + bodyW;
        double tipH = bodyH * 0.45;
        double tipY = bodyY + (bodyH - tipH) / 2.0;
        gc.strokeRoundRect(tipX, tipY, tipW, tipH, 2.0, 2.0);

        // Segment fill
        int segments = 4;
        double padding = 3.0;
        double innerX = bodyX + padding;
        double innerY = bodyY + padding;
        double innerW = bodyW - 2.0 * padding;
        double innerH = bodyH - 2.0 * padding;
        double gap = 2.0;
        double segW = (innerW - gap * (segments - 1)) / segments;

        int filled = (int) Math.floor(clamped * segments + 1e-9);
        if (clamped > 0 && filled == 0) filled = 1;

        gc.setFill(color);
        for (int i = 0; i < filled; i++) {
            double x = innerX + i * (segW + gap);
            gc.fillRoundRect(x, innerY, segW, innerH, 2.0, 2.0);
        }
    }

    // ── DOOR ──────────────────────────────────────────────────────────

    /**
     * Handles the left door toggle button.
     *
     * @param event button event
     */
    @FXML
    private void onToggleLeftDoor(ActionEvent event) {
        session.toggleLeftDoor();
        AudioManager.play("door_lock", true);
        refreshDoorButton();
    }

    /**
     * Updates the left door button label to match the current door state.
     */
    private void refreshDoorButton() {
        boolean closed = session.isLeftDoorClosed();
        leftDoorButton.setText((closed ? "OPEN" : "CLOSE") + "\nDOOR");
    }

    // ── CAMERAS ───────────────────────────────────────────────────────

    /**
     * Opens the camera tablet view.
     *
     * @param event button event
     */
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

            // Camera open should be instant (no fade).
            Stage stage = (Stage) cameraButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 720));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── VENT ──────────────────────────────────────────────────────────

    /**
     * Attempts to seal the vents.
     *
     * @param event button event
     */
    @FXML
    private void onToggleVentSeal(ActionEvent event) {
        session.getVents().sealVent();
    }

    // ── COFFEE MUG ────────────────────────────────────────────────────

    /**
     * Handles clicking the coffee mug to refill coffee.
     *
     * @param event mouse event
     */
    @FXML
    private void onCoffeeMugClicked(MouseEvent event) {
        session.refillCoffee(0.3);
    }

    // ── STAIRWELLS ────────────────────────────────────────────────────

    /**
     * Activates emergency lights in the left stairwell.
     *
     * @param event button event
     */
    @FXML
    private void onLeftStairwellLights(ActionEvent event) {
        session.getStairSystem().activateLights(Stairwell.LEFT);
    }

    /**
     * Activates emergency lights in the middle stairwell.
     *
     * @param event button event
     */
    @FXML
    private void onMiddleStairwellLights(ActionEvent event) {
        session.getStairSystem().activateLights(Stairwell.MIDDLE);
    }

    /**
     * Activates emergency lights in the right stairwell.
     *
     * @param event button event
     */
    @FXML
    private void onRightStairwellSensor(ActionEvent event) {
        session.getStairSystem().activateLights(Stairwell.RIGHT);
    }

    // ── ELEVATOR ──────────────────────────────────────────────────────

    /**
     * Triggers an emergency stop in the elevator subsystem.
     *
     * @param event button event
     */
    @FXML
    private void onElevatorEmergencyStop(ActionEvent event) {
        session.getElevator().emergencyStop();
    }

    // ── WIN / LOSE ────────────────────────────────────────────────────

    /**
     * Handles the win condition callback (survive until 6 AM).
     */
    private void handleWin() {
        System.out.println("YOU SURVIVED THE NIGHT!");
        goToWinCelebration();
    }

    /**
     * Handles the game over callback (power out).
     */
    private void handleGameOver() {
        System.out.println("GAME OVER — power out");
        returnToMainMenu();
    }

    /**
     * Goes to the short win animation scene ("6:00 AM" + fireworks), then to the win screen.
     */
    private void goToWinCelebration() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/org/five_nights_at_dana/WinCelebration.fxml"));
            FadeUtil.fadeOutAndSwitch(officeBackground, root, 1280, 720, 0.30);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns to the main menu scene.
     */
    private void returnToMainMenu() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/org/five_nights_at_dana/MainMenu.fxml"));
            FadeUtil.fadeOutAndSwitch(officeBackground, root, 1280, 720, 0.30);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── JUMPSCARE ─────────────────────────────────────────────────────

    /**
     * Transitions into the jumpscare scene.
     *
     * @param studentQuestion the question displayed during the jumpscare
     */
    public void triggerJumpscare(String studentQuestion) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/five_nights_at_dana/JumpscareView.fxml"));
            Parent root = loader.load();
            JumpscareController jc = loader.getController();
            jc.startJumpscare(studentQuestion);

            FadeUtil.fadeOutAndSwitch(officeBackground, root, 1280, 720, 0.18);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** External hook to push HUD values if needed outside the frame callback. */
    public void updateHUD(double powerPercent, double classActivity) {
        drawBattery(powerPercent);
        powerLabel.setText((int)(powerPercent * 100) + "%");
        classroomActivityBar.setProgress(classActivity);
    }
}
