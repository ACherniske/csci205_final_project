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
import org.five_nights_at_dana.AI.Location;
import org.five_nights_at_dana.AI.Student;
import org.five_nights_at_dana.Managers.Notification;
import org.five_nights_at_dana.Managers.NotificationManager;
import org.five_nights_at_dana.Managers.StudentManager;
import org.five_nights_at_dana.Rendering.Camera.CameraSystem;
import org.five_nights_at_dana.Systems.Classroom.ClassroomMechanic;
import org.five_nights_at_dana.Systems.Elevator.ElevatorSystem;
import org.five_nights_at_dana.Systems.Stairwells.StairSystem;
import org.five_nights_at_dana.Systems.Stairwells.Stairwell;
import org.five_nights_at_dana.Systems.Vent.VentSystem;

import org.five_nights_at_dana.Systems.SensorHelper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class GameSession {

    // Seconds of real time per in-game minute.
    // 0.5 → each hour takes 30 s → full night ≈ 3 min (testing)
    // 1.0 → each hour takes 60 s → full night ≈ 6 min
    public static final double SECONDS_PER_GAME_MINUTE = 1.0;

    /** Simulation tick-rate. All per-"frame" mechanics assume 60 ticks/sec. */
    public static final int TICKS_PER_SECOND = 60;
    private static final long TICK_NANOS = 1_000_000_000L / TICKS_PER_SECOND;
    private static final long MAX_ACCUMULATED_NANOS = 250_000_000L; // clamp to avoid spiral-of-death

    /**
     * Total available battery capacity in "power units".
     * The HUD reads power as a fraction via {@link #getPower()}.
     */
    private static final double BATTERY_CAPACITY = 2.0;

    /**
     * Multiplier applied to subsystem power drains.
     * Subsystems report drain values on a 0..1-ish scale; applying
     * a multiplier keeps gameplay playable without changing subsystem APIs/tests.
     */
    private static final double SYSTEM_POWER_DRAIN_MULTIPLIER = 0.05;

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
    private double power       = BATTERY_CAPACITY;
    private double coffeeLevel = 1.0;
    private int frameCount     = 0;

    // Difficulty ramp tracking (FX thread)
    private int lastDifficultyIncreaseHour = 12;

    // ── Office mechanics ─────────────────────────────────────────────
    private boolean leftDoorClosed = false;

    // ── Callbacks (all invoked on the FX thread) ──────────────────────
    private Consumer<String> onJumpscare;
    private Runnable onWin;
    private Runnable onGameOver;
    private Runnable onFrameRender;

    // ── Timers ────────────────────────────────────────────────────────
    private ScheduledExecutorService nightClock;
    private AnimationTimer gameLoop;

    // Fixed-step timekeeping for AnimationTimer
    private long lastNowNanos = 0L;
    private long accumulatedNanos = 0L;

    /**
     * Constructs the singleton game session and initializes all subsystems.
     * Use {@link #getInstance()} to access the shared instance.
     */
    private GameSession() {
        studentManager = new StudentManager();
        cameraSystem   = new CameraSystem(studentManager);
        elevator       = new ElevatorSystem();
        stairSystem    = new StairSystem();
        vents          = new VentSystem();
        classroom      = new ClassroomMechanic();
    }

    /**
     * Returns the singleton GameSession instance.
     *
     * @return the shared GameSession
     */
    public static GameSession getInstance() {
        if (instance == null) instance = new GameSession();
        return instance;
    }

    // ── Public API ────────────────────────────────────────────────────

    /** Resets all state and starts both the clock and the frame loop. */
    public void startNight() {
        // Enable verbose AI movement roll logging for debugging.
        Student.setDebugMoveLogs(true);

        hour       = 12;
        minute     = 0;
        power      = BATTERY_CAPACITY;
        coffeeLevel= 1.0;
        frameCount = 0;
        active     = true;
        currentState = GameState.PLAYING;
        leftDoorClosed = false;
        lastDifficultyIncreaseHour = 12;

        elevator.reset();
        stairSystem.reset();
        vents.reset();
        classroom.reset();
        studentManager.reset();
        studentManager.applyNight1StartingAiLevels();
        NotificationManager.clear();
        SensorHelper.reset();
        wireRunner();

        // Runner is dormant at the start of Night 1.
        classroom.setRunnerActive(false);
        classroom.setRunnerDifficultyMultiplier(1.0);

        startNightClock();
        startGameLoop();
    }

    /** Stops the frame loop and the background clock. */
    public void stopNight() {
        active = false;
        if (nightClock != null) { nightClock.shutdownNow(); nightClock = null; }
        if (gameLoop   != null) { gameLoop.stop();          gameLoop   = null; }
        lastNowNanos = 0L;
        accumulatedNanos = 0L;
    }

    // ── Internal update (called every frame by AnimationTimer) ────────

    /**
     * Advances the simulation by exactly one fixed tick.
     * This method is called by the AnimationTimer-backed fixed-step loop.
     */
    private void updateTick() {
        frameCount++;
        NotificationManager.update();

        maybeIncreaseDifficulty();

        // Runner should only become active once the night is underway (2 AM+),
        // and the activity meter should scale up with difficulty.
        syncRunnerDifficulty();

        // Door can prevent office entry by pushing students back before AI updates
        applyLeftDoorBlock();

        studentManager.update();

        emitDoorArrivalWarnings();

        integrateSpecialTransitions();
        integrateStairTransitions();
        applyLeftDoorBlock();

        elevator.update();
        stairSystem.update();
        vents.update();
        classroom.update();

        // Catch arrivals to the door caused by subsystem transitions (e.g., vent exit).
        emitDoorArrivalWarnings();

        updatePower();
        updateCoffee();
        checkConditions();
    }

    /**
     * Emits a warning toast when a student first reaches the office door.
     * Uses a one-shot flag stored on each Student to avoid spam.
     */
    private void emitDoorArrivalWarnings() {
        for (Student s : studentManager.getAllStudents()) {
            if (s.consumeJustArrivedAtDoor()) {
                SensorHelper.emit(s.getName() + " is at the door!", Notification.Type.DANGER);
            }
        }
    }

    /**
     * Applies power drain for this tick (base drain + subsystem drain + door drain).
     */
    private void updatePower() {
        // Base drain + per-system drain converted from per-second to per-frame
        power -= 0.000003;
        power -= (vents.getPowerDrain()    * SYSTEM_POWER_DRAIN_MULTIPLIER) / (double)TICKS_PER_SECOND;
        power -= (elevator.getPowerDrain() * SYSTEM_POWER_DRAIN_MULTIPLIER) / (double)TICKS_PER_SECOND;
        power -= (stairSystem.getPowerDrain() * SYSTEM_POWER_DRAIN_MULTIPLIER) / (double)TICKS_PER_SECOND;
        if (leftDoorClosed) {
            // Small continuous drain when the door is shut
            power -= 0.008 / (double)TICKS_PER_SECOND;
        }
        power  = Math.max(0.0, power);
    }

    /**
     * Increases AI difficulty on the hour (starting at 1 AM).
     * This runs on the FX tick thread to avoid mutating AI from the clock thread.
     */
    private void maybeIncreaseDifficulty() {
        // hour/minute are updated by the background clock thread; keep AI mutation here.
        if (minute != 0) return;
        if (hour == lastDifficultyIncreaseHour) return;

        lastDifficultyIncreaseHour = hour;

        // FNAF-style Night 1 aggression growth: only at 2AM, 3AM, 4AM.
        if (hour == 2 || hour == 3 || hour == 4) {
            studentManager.applyNight1AggressionGrowth(hour);
            NotificationManager.push("Students are getting bolder...", Notification.Type.SYSTEM);
        }
    }

    /**
     * Keeps the runner activity meter aligned with the difficulty schedule.
     * Dormant until 2 AM; then scales the fill rate as hours progress.
     */
    private void syncRunnerDifficulty() {
        // 12 AM and 1 AM: dormant.
        boolean activeNow = (hour >= 2 && hour <= 6);
        classroom.setRunnerActive(activeNow);

        // Simple difficulty scale by hour: 2AM=1.0x, 3AM=1.3x, 4AM=1.6x, 5AM=1.9x.
        int hourIndex = Math.max(0, hour - 2);
        double multiplier = 1.0 + 0.30 * hourIndex;
        classroom.setRunnerDifficultyMultiplier(multiplier);
    }

    /**
     * Applies coffee depletion for this tick.
     */
    private void updateCoffee() {
        coffeeLevel -= 0.000008;
        coffeeLevel  = Math.max(0.0, coffeeLevel);
    }

    /**
     * Checks end-game conditions (jumpscare, power out, win).
     * Runs while playing and while viewing cameras.
     */
    private void checkConditions() {
        // The player can lose/win while the camera tablet is up.
        // Only suppress checks in non-gameplay states (menus, already-ended states).
        if (currentState != GameState.PLAYING && currentState != GameState.VIEWING_CAMERAS) return;

        // Jumpscare checked first — student in office always wins.
        Student inOffice = studentManager.getStudentInOffice();
        if (inOffice != null) {
            String question = inOffice.getQuestion();
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

    /**
     * Advances the in-game clock by one minute.
     * Invoked on a background scheduled executor.
     */
    private void tick() {
        minute++;
        if (minute >= 60) {
            minute = 0;
            hour = (hour == 12) ? 1 : hour + 1;
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────

    /**
     * Wires the runner student (if present) into the classroom mechanic.
     */
    private void wireRunner() {
        Student runner = studentManager.getRunnerStudent();
        if (runner != null) classroom.setRunner(runner);
    }

    /**
     * Starts the background in-game clock executor.
     */
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

    /**
     * Starts the AnimationTimer loop which drives the fixed-step simulation and render callback.
     */
    private void startGameLoop() {
        if (gameLoop != null) gameLoop.stop();
        gameLoop = new AnimationTimer() {
            /**
             * JavaFX frame callback.
             * Accumulates real time and advances the simulation in fixed 60Hz ticks.
             *
             * @param now current timestamp in nanoseconds
             */
            @Override
            public void handle(long now) {
                if (!active) return;

                if (lastNowNanos == 0L) {
                    lastNowNanos = now;
                    return;
                }

                long delta = now - lastNowNanos;
                lastNowNanos = now;

                // Clamp huge frame stalls (breakpoints, window drag, etc.)
                accumulatedNanos += Math.min(delta, MAX_ACCUMULATED_NANOS);

                while (accumulatedNanos >= TICK_NANOS) {
                    updateTick();
                    accumulatedNanos -= TICK_NANOS;
                }

                if (onFrameRender != null) onFrameRender.run();
            }
        };
        gameLoop.start();
    }

    /**
     * Registers students who have entered special transitional states (vent/elevator)
     * with the relevant system. If the system refuses the transition (sealed/occupied),
     * the student is pushed back to their entry point.
     */
    private void integrateSpecialTransitions() {
        for (Student s : studentManager.getAllStudents()) {
            switch (s.getCurrentLocation()) {
                case IN_VENT -> {
                    if (vents.getStudentInVent() != s) {
                        boolean accepted = vents.studentEnterVent(s, s.getPreviousLocation());
                        if (!accepted && s.getPreviousLocation() != null) {
                            // Revert to entry point if vent is sealed/occupied/invalid
                            s.setLocation(s.getPreviousLocation());
                        }
                    }
                }
                case IN_ELEVATOR -> {
                    if (elevator.getStudentInElevator() != s) {
                        boolean accepted = elevator.studentEnterElevator(s, s.getPreviousLocation());
                        if (!accepted && s.getPreviousLocation() != null) {
                            s.setLocation(s.getPreviousLocation());
                        }
                    }
                }
                default -> {
                }
            }
        }
    }

    /**
     * Wires AI location changes into the stair system so sensors + deterrents work.
     * StairSystem manages its own per-stairwell cooldown via SensorHelper.
     */
    private void integrateStairTransitions() {
        for (Student s : studentManager.getAllStudents()) {
            Stairwell prev = stairwellFromLocation(s.getPreviousLocation());
            Stairwell cur = stairwellFromLocation(s.getCurrentLocation());

            if (prev != null && prev != cur) {
                stairSystem.studentExitStairwell(s, prev);
            }
            if (cur != null && cur != prev) {
                stairSystem.studentEnterStairwell(s, cur);
            }
        }
    }

    /**
     * Converts a Location enum into a Stairwell identifier (or null if not a stair location).
     *
     * @param loc location to map
     * @return the stairwell, or null if the location is not a stairwell node
     */
    private Stairwell stairwellFromLocation(Location loc) {
        if (loc == null) return null;
        String name = loc.name();
        if (name.contains("STAIR_LEFT")) return Stairwell.LEFT;
        if (name.contains("STAIR_MID")) return Stairwell.MIDDLE;
        if (name.contains("STAIR_RIGHT")) return Stairwell.RIGHT;
        return null;
    }

    /**
     * If the left door is closed and a student reaches the door, push them back.
     * This keeps the door mechanic meaningful without forcing dynamic graph edits.
     */
    private void applyLeftDoorBlock() {
        if (!leftDoorClosed) return;

        Student atDoor = studentManager.getStudentAtDoor();
        if (atDoor == null) return;

        // Bounce the student back into the hallway.
        atDoor.setLocation(Location.FLOOR3_HALLWAY_RIGHT);

        // Rate-limited message
        SensorHelper.trigger(
                "left_door_block",
                "Left door blocked a student.",
                Notification.Type.SYSTEM,
                frameCount
        );
    }

    // ── Getters ───────────────────────────────────────────────────────

    /** @return the StudentManager backing all AI updates */
    public StudentManager getStudentManager() { return studentManager; }

    /** @return the camera system used by the camera tablet */
    public CameraSystem getCameraSystem() { return cameraSystem; }

    /** @return the elevator subsystem */
    public ElevatorSystem getElevator() { return elevator; }

    /** @return the stair system subsystem */
    public StairSystem getStairSystem() { return stairSystem; }

    /** @return the vent subsystem */
    public VentSystem getVents() { return vents; }

    /** @return the classroom mechanic */
    public ClassroomMechanic getClassroom() { return classroom; }

    /** @return current in-game hour */
    public int getHour() { return hour; }

    /** @return current in-game minute */
    public int getMinute() { return minute; }

    /** @return true if the night is currently active */
    public boolean isActive() { return active; }

    /** @return current power fraction (0..1) */
    public double getPower() {
        return Math.max(0.0, Math.min(1.0, power / BATTERY_CAPACITY));
    }

    /** @return current coffee level fraction (0..1) */
    public double getCoffeeLevel() { return coffeeLevel; }

    /** @return total tick counter since night start */
    public int getFrameCount() { return frameCount; }

    /** @return the current game state */
    public GameState getCurrentState() { return currentState; }

    /** @return true if the night has reached 6 AM */
    public boolean isNightOver() { return hour == 6; }

    // ── Setters / callbacks ───────────────────────────────────────────

    /**
     * Sets the current state of the session.
     *
     * @param s new state
     */
    public void setCurrentState(GameState s) { currentState = s; }

    /**
     * Refills coffee by a given fraction.
     *
     * @param amount amount to add (clamped so coffee stays within 0..1)
     */
    public void refillCoffee(double amount) { coffeeLevel = Math.min(1.0, coffeeLevel + amount); }

    /** @return true if the left door is currently closed */
    public boolean isLeftDoorClosed() { return leftDoorClosed; }

    /** Toggle the left door state. Returns the new state (true = closed). */
    public boolean toggleLeftDoor() {
        leftDoorClosed = !leftDoorClosed;
        return leftDoorClosed;
    }

    /**
     * Registers a callback for jumpscare events.
     *
     * @param c consumer receiving the student question text
     */
    public void setOnJumpscare(Consumer<String> c) { onJumpscare = c; }

    /**
     * Registers a callback invoked when the player survives until 6 AM.
     *
     * @param r runnable callback
     */
    public void setOnWin(Runnable r) { onWin = r; }

    /**
     * Registers a callback invoked when power reaches 0.
     *
     * @param r runnable callback
     */
    public void setOnGameOver(Runnable r) { onGameOver = r; }

    /**
     * Registers a per-frame render callback (invoked once per JavaFX frame).
     *
     * @param r runnable callback
     */
    public void setOnFrameRender(Runnable r) { onFrameRender = r; }
}
