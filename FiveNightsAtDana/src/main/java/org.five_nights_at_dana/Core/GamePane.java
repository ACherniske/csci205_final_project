/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 3:35 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Core
 * Class: GamePane
 *
 * Description:
 *      Central game loop controller.
 *      Handles updating, rendering, and coordinating all systems.
 * ****************************************
 */

package org.five_nights_at_dana.Core;

import javafx.scene.canvas.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import org.five_nights_at_dana.Managers.StudentManager;
import org.five_nights_at_dana.Rendering.Camera.CameraSystem;
import org.five_nights_at_dana.Rendering.Office.OfficeViewManager;
import org.five_nights_at_dana.Systems.Classroom.ClassroomMechanic;
import org.five_nights_at_dana.Systems.Elevator.ElevatorSystem;
import org.five_nights_at_dana.Systems.Stairwells.LeftStairwell;
import org.five_nights_at_dana.Systems.Stairwells.MiddleStairwell;
import org.five_nights_at_dana.Systems.Stairwells.RightStairwell;
import org.five_nights_at_dana.Systems.Vent.VentSystem;

public class GamePane extends Pane {

    private Canvas canvas;
    private GraphicsContext gc;

    private GameState currentState;
    private int hour;
    private double power;
    private double coffeeLevel;
    private int frameCounter;

    private OfficeViewManager officeView;
    private StudentManager studentManager;
    private CameraSystem cameraSystem;
    private ElevatorSystem elevator;
    private LeftStairwell leftStairs;
    private MiddleStairwell middleStairs;
    private RightStairwell rightStairs;
    private VentSystem vents;
    private ClassroomMechanic classroom;

    /**
     * Initializes rendering surface and all subsystems.
     */
    public GamePane() {
        canvas = new Canvas(1280, 720);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
        initializeSystems();
    }

    /**
     * Updates all systems in correct execution order.
     */
    public void update() {
        studentManager.update();
        officeView.update();
        cameraSystem.update();

        elevator.update();
        leftStairs.update();
        middleStairs.update();
        rightStairs.update();
        vents.update();
        classroom.update();

        updatePower();
        updateCoffee();
        updateHour();
        checkWinLoseConditions();
    }

    /**
     * Renders frame based on current GameState.
     */
    public void render() {
        gc.clearRect(0, 0, 1280, 720);

        switch (currentState) {
            case PLAYING -> officeView.render(gc);
            case VIEWING_CAMERAS -> cameraSystem.render(gc);
            default -> {}
        }

        renderUI(gc);
    }

    /**
     * Handles mouse click input.
     * @param x screen x
     * @param y screen y
     */
    public void handleClick(double x, double y) {
        // TODO route click to office objects or UI
    }

    /**
     * Handles keyboard input.
     * @param key key pressed
     */
    public void handleKeyPress(KeyCode key) {
        // TODO handle rotation, camera toggles, etc.
    }

    /**
     * Refills coffee resource.
     * @param amount amount to add
     */
    public void refillCoffee(double amount) {
        coffeeLevel += amount;
    }

    public double getPower() { return power; }
    public double getCoffeeLevel() { return coffeeLevel; }
    public int getHour() { return hour; }
    public GameState getCurrentState() { return currentState; }
    public void setGameState(GameState s) { this.currentState = s; }
    public ClassroomMechanic getClassroom() { return classroom; }

    /**
     * Initializes all gameplay systems.
     */
    private void initializeSystems() {
        officeView = new OfficeViewManager();
        studentManager = new StudentManager();
        cameraSystem = new CameraSystem(this);

        elevator = new ElevatorSystem();
        leftStairs = new LeftStairwell();
        middleStairs = new MiddleStairwell();
        rightStairs = new RightStairwell();
        vents = new VentSystem();
        classroom = new ClassroomMechanic();

        currentState = GameState.MAIN_MENU;
    }

    /** Updates total power drain. */
    private void updatePower() {
        // TODO sum power drain from all systems
    }

    /** Updates coffee depletion. */
    private void updateCoffee() {
        // TODO implement decay + refill effects
    }

    /** Advances in-game time. */
    private void updateHour() {
        // TODO implement time progression
    }

    /** Checks win/lose conditions. */
    private void checkWinLoseConditions() {
        // TODO evaluate power, survival time, jumpscare triggers
    }

    /**
     * Renders UI overlay.
     * @param gc graphics context
     */
    private void renderUI(GraphicsContext gc) {
        // TODO draw HUD (power, coffee, time)
    }
}
