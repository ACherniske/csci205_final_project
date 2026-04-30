/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/25/2026
 * Time: 9:09 PM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Core;
 * Class: GameSession
 *
 * Description:
 * Singleton that owns the game clock, all subsystems, and the
 * frame-by-frame AnimationTimer loop. Call startNight() to begin;
 * the loop runs until 6 AM, power-out, or a jumpscare.
 * ****************************************
 */

package org.five_nights_at_dana.Core;

import javafx.animation.AnimationTimer;
import org.five_nights_at_dana.AI.Student;
import org.five_nights_at_dana.Managers.StudentManager;
import org.five_nights_at_dana.Rendering.Camera.CameraSystem;
import org.five_nights_at_dana.Systems.Classroom.ClassroomMechanic;
import org.five_nights_at_dana.Systems.Elevator.ElevatorSystem;
import org.five_nights_at_dana.Systems.Stairwells.StairSystem;
import org.five_nights_at_dana.Systems.Vent.VentSystem;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class GameSession {

    // Seconds of real time per in-game minute.
    // 0.5 → each hour takes 30 s → full night ≈ 3 min (testing)
    // 1.0 → each hour takes 60 s → full night ≈ 6 min
    public static final double SECONDS_PER_GAME_MINUTE = 1.0;

    private static GameSession instance;

    // ── Subsystems ────────────────────────────────────────────────────
    private final StudentManager studentManager;
    private final CameraSystem cameraSystem;
    private final ElevatorSystem elevator;
    private final StairSystem stairSystem;
    private final VentSystem vents;
    private final ClassroomMechanic classroom;

    // ── Clock state (volatile: written by background thread) ──────────
    private volatile int hour   = 12;
    private volatile int minute = 0;
    private volatile boolean active = false;

    // ── Game state (FX thread only) ───────────────────────────────────
    private GameState currentState = GameState.MAIN_MENU;
    private double power       = 1.0;
    private double coffeeLevel = 1.0;
    private int frameCount     = 0;

    // ── Callbacks (all invoked on the FX thread) ──────────────────────
    private Consumer<String> onJumpscare;
    private Runnable onWin;
    private Runnable onGameOver;
    private Runnable onFrameRender;

    // ── Timers ────────────────────────────────────────────────────────
    private ScheduledExecutorService nightClock;
    private AnimationTimer gameLoop;

    private GameSession() {
        studentManager = new StudentManager();
        cameraSystem   = new CameraSystem(studentManager);
        elevator       = new ElevatorSystem();
        stairSystem    = new StairSystem();
        vents          = new VentSystem();
        classroom      = new ClassroomMechanic();
        wireRunner();
    }

    public static GameSession getInstance() {
        if (instance == null) instance = new GameSession();
        return instance;
    }

    // ── Public API ────────────────────────────────────────────────────

    /** Resets all state and starts both the clock and the frame loop. */
    public void startNight() {
        hour       = 12;
        minute     = 0;
        power      = 1.0;
        coffeeLevel= 1.0;
        frameCount = 0;
        active     = true;
        currentState = GameState.PLAYING;

        elevator.reset();
        stairSystem.reset();
        vents.reset();
        classroom.reset();
        studentManager.reset();
        wireRunner();

        startNightClock();
        startGameLoop();
    }

    /** Stops the frame loop and the background clock. */
    public void stopNight() {
        active = false;
        if (nightClock != null) { nightClock.shutdownNow(); nightClock = null; }
        if (gameLoop   != null) { gameLoop.stop();          gameLoop   = null; }
    }

    // ── Internal update (called every frame by AnimationTimer) ────────

    private void update() {
        frameCount++;

        studentManager.update();
        elevator.update();
        stairSystem.update();
        vents.update();
        classroom.update();

        updatePower();
        updateCoffee();
        checkConditions();

        if (onFrameRender != null) onFrameRender.run();
    }

    private void updatePower() {
        // Base drain + per-system drain converted from per-second to per-frame
        power -= 0.000005;
        power -= vents.getPowerDrain()    / 60.0;
        power -= elevator.getPowerDrain() / 60.0;
        power  = Math.max(0.0, power);
    }

    private void updateCoffee() {
        coffeeLevel -= 0.000008;
        coffeeLevel  = Math.max(0.0, coffeeLevel);
    }

    private void checkConditions() {
        if (currentState != GameState.PLAYING) return;

        // Jumpscare checked first — if a student is at the door, that always wins
        // regardless of whether power also hit 0 in the same frame.
        Student atDoor = studentManager.getStudentAtDoor();
        if (atDoor != null) {
            String question = atDoor.getQuestion();
            Consumer<String> cb = onJumpscare;
            endGame(GameState.JUMPSCARE);
            if (cb != null) cb.accept(question);
            return;
        }

        if (power <= 0) {
            Runnable cb = onGameOver;
            endGame(GameState.GAME_OVER);
            if (cb != null) cb.run();
            return;
        }

        if (isNightOver()) {
            Runnable cb = onWin;
            endGame(GameState.WIN);
            if (cb != null) cb.run();
        }
    }

    /**
     * Atomically sets the final state, stops all timers, and clears every
     * callback before the winning callback is invoked.  Prevents any second
     * end-condition from firing if the callback triggers additional FX work.
     */
    private void endGame(GameState finalState) {
        currentState   = finalState;
        onJumpscare    = null;
        onWin          = null;
        onGameOver     = null;
        onFrameRender  = null;   // stops render from running on a detached scene
        stopNight();
    }

    // ── Clock tick (background thread) ────────────────────────────────

    private void tick() {
        minute++;
        if (minute >= 60) {
            minute = 0;
            hour = (hour == 12) ? 1 : hour + 1;
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private void wireRunner() {
        Student runner = studentManager.getRunnerStudent();
        if (runner != null) classroom.setRunner(runner);
    }

    private void startNightClock() {
        if (nightClock != null) nightClock.shutdownNow();
        long intervalMs = Math.max(1, (long)(SECONDS_PER_GAME_MINUTE * 1000));
        nightClock = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "game-clock");
            t.setDaemon(true);
            return t;
        });
        nightClock.scheduleAtFixedRate(this::tick, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
    }

    private void startGameLoop() {
        if (gameLoop != null) gameLoop.stop();
        gameLoop = new AnimationTimer() {
            @Override public void handle(long now) { update(); }
        };
        gameLoop.start();
    }

    // ── Getters ───────────────────────────────────────────────────────

    public StudentManager    getStudentManager() { return studentManager; }
    public CameraSystem      getCameraSystem()   { return cameraSystem;   }
    public ElevatorSystem    getElevator()       { return elevator;       }
    public StairSystem       getStairSystem()    { return stairSystem;    }
    public VentSystem        getVents()          { return vents;          }
    public ClassroomMechanic getClassroom()      { return classroom;      }

    public int       getHour()         { return hour;         }
    public int       getMinute()       { return minute;       }
    public boolean   isActive()        { return active;       }
    public double    getPower()        { return power;        }
    public double    getCoffeeLevel()  { return coffeeLevel;  }
    public int       getFrameCount()   { return frameCount;   }
    public GameState getCurrentState() { return currentState; }

    public boolean isNightOver() { return hour == 6; }

    // ── Setters / callbacks ───────────────────────────────────────────

    public void setCurrentState(GameState s)        { currentState = s;     }
    public void refillCoffee(double amount)         { coffeeLevel = Math.min(1.0, coffeeLevel + amount); }

    public void setOnJumpscare(Consumer<String> c) { onJumpscare   = c; }
    public void setOnWin(Runnable r)               { onWin         = r; }
    public void setOnGameOver(Runnable r)          { onGameOver    = r; }
    public void setOnFrameRender(Runnable r)       { onFrameRender = r; }
}
