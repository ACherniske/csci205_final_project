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
 *
 * ****************************************
 */

package org.five_nights_at_dana.Core;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

/**
 * Core game loop and state manager.
 */
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

    public GamePane() {
        canvas = new Canvas(1280, 720);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        initializeSystems();
    }

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

    public void render() {
        gc.clearRect(0, 0, 1280, 720);

        switch (currentState) {
            case PLAYING -> officeView.render(gc);
            case VIEWING_CAMERAS -> cameraSystem.render(gc);
            default -> {}
        }

        renderUI(gc);
    }

    public void handleClick(double x, double y) {}
    public void handleKeyPress(KeyCode key) {}
    public void refillCoffee(double amount) { coffeeLevel += amount; }

    public double getPower() { return power; }
    public double getCoffeeLevel() { return coffeeLevel; }
    public int getHour() { return hour; }
    public GameState getCurrentState() { return currentState; }
    public void setGameState(GameState state) { this.currentState = state; }
    public ClassroomMechanic getClassroom() { return classroom; }

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

    private void updatePower() {}
    private void updateCoffee() {}
    private void updateHour() {}
    private void checkWinLoseConditions() {}
    private void renderUI(GraphicsContext gc) {}
}
