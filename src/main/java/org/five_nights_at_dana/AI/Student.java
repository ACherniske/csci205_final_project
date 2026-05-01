/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.AI
 * Class: Student
 *
 * Description:
 * Represents a student AI trying to reach Professor Lily's office.
 * Manages pathfinding logic, movement timers based on difficulty/personality,
 * and state transitions through the building's location nodes.
 *
 * ****************************************
 */

package org.five_nights_at_dana.AI;

import org.five_nights_at_dana.Managers.NavigationManager;
import org.five_nights_at_dana.Managers.ObservationManager;
import java.util.*;

/**
 * Represents an individual student AI.
 * Handles personality-driven behavior, movement timing, state transitions,
 * and tracks historical location memory to assist in pathing decisions.
 */
public class Student {

    /**
     * AI level in the classic 0..20 range.
     * 0 => will never move on a movement opportunity.
     * 20 => will always move on a movement opportunity (unless stalled/blocked).
     */
    private static final int MAX_AI_LEVEL = 20;

    /** Assumed sim tick-rate for converting seconds -> frames. */
    private static final int TICKS_PER_SECOND = 60;

    /**
     * Global scaling for move-opportunity timing.
     * The original FNAF-style intervals were designed for a much smaller map; this project
     * has 3 floors, so we shorten intervals so students can realistically reach Floor 3.
     */
    private static final double BUILDING_INTERVAL_MULTIPLIER = 0.65;

    /** Additional per-AI-level speedup applied to the movement-opportunity interval. */
    private static final double INTERVAL_REDUCTION_PER_AI_LEVEL = 0.01;

    /** Hard minimum interval to avoid absurdly fast movement at high AI. */
    private static final double MIN_MOVE_INTERVAL_SECONDS = 0.75;

    private final String name;
    private final String question;
    private final Personality personality;
    private Location currentLocation;
    private Location previousLocation;
    private PathType preferredPath;

    /** Frames remaining until the next "movement opportunity" check. */
    private int movementTimer;

    /** FNAF-style aggressiveness level (0..20). */
    private int aiLevel;
    private double awarenessLevel;
    private boolean charging;
    private int chargeTimer;
    private boolean sprinting;

    /** When a student reaches the office door, they must "linger" for N movement opportunities. */
    private int doorLingerMovesRemaining = 0;

    /** One-shot flag used by GameSession to emit a warning toast when a student arrives at the door. */
    private boolean justArrivedAtDoor = false;

    // Memory system for tracking recent path history
    private static final int MEMORY_SIZE = 5;
    private final Deque<Location> recentLocations = new ArrayDeque<>();

    // Controlled randomness for deterministic testing
    private static Random rand = new Random();

    /** Debug toggle for printing every move roll/comparison to the console. */
    private static boolean debugMoveLogs = false;

    // Tuning constants
    private static final int CHARGE_DURATION = 120; // frames -> 2s

    /**
     * Allows tests to inject a deterministic Random instance.
     * @param r Random instance (use new Random(seed) in tests)
     */
    public static void setRandom(Random r) {
        rand = r;
    }

    /** Enables/disables verbose movement roll debug logging. */
    public static void setDebugMoveLogs(boolean enabled) {
        debugMoveLogs = enabled;
    }

    /**
     * Constructs a new Student and determines their starting location and
     * preferred path based on their personality profile.
     * @param name        The display name of the student.
     * @param question    The dialogue triggered during a jumpscare.
     * @param personality The {@link Personality} governing movement speed and pathing.
     */
    public Student(String name, String question, Personality personality) {
        this.name = name;
        this.question = question;
        this.personality = personality;

    this.aiLevel = 0;
        this.awarenessLevel = 0.5;
        this.charging = false;
        this.chargeTimer = 0;
        this.sprinting = false;

        // Starting location
        if (personality == Personality.RUNNER) {
            this.currentLocation = Location.FLOOR3_COMPUTER_LAB;
        } else {
            this.currentLocation = Location.FLOOR1_ENTRANCE;
        }

        rememberLocation(this.currentLocation);
        selectPreferredPath();
        resetMovementTimer();
    }

    // ========== MEMORY LOGIC ==========

    /**
     * Adds a location to the student's memory, evicting the oldest if capacity is reached.
     * @param loc The location to remember.
     */
    public void rememberLocation(Location loc) {
        // Only remember if it's different from the last remembered location
        if (!recentLocations.isEmpty() && recentLocations.peekLast() == loc) {
            return;
        }

        if (recentLocations.size() >= MEMORY_SIZE) {
            recentLocations.removeFirst();
        }
        recentLocations.addLast(loc);
    }

    /**
     * Returns a set of the most recently visited locations.
     * @return A set of locations currently in memory.
     */
    public Set<Location> getRecentLocations() {
        return new HashSet<>(recentLocations);
    }

    /**
     * Pushes the student back a specified number of steps in their recent path history.
     * If steps exceed available history, safely falls back as far as possible.
     *
     * @param steps Number of locations to move back.
     */
    public void pushBack(int steps) {
        if (steps <= 0 || recentLocations.isEmpty()) return;

        List<Location> history = new ArrayList<>(recentLocations);

        int targetIndex = Math.max(0, history.size() - 1 - steps);
        Location fallback = history.get(targetIndex);

        if (fallback != null && fallback != currentLocation) {
            this.previousLocation = this.currentLocation;
            this.currentLocation = fallback;

            rememberLocation(fallback); // keep memory consistent

            System.out.println(name + " pushed back " + steps +
                    " step(s) to " + currentLocation);

            resetMovementTimer(); // movement penalty
        }
    }

    /**
     * Pushes the student back by one step in their recent path history.
     */
    public void pushBack() {
        pushBack(1);
    }

    /**
     * Pushes the student back a random number of steps.
     *
     * @param min Minimum steps (inclusive)
     * @param max Maximum steps (inclusive)
     */
    public void pushBackRandom(int min, int max) {
        if (min < 1) min = 1;

        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }

        int steps = rand.nextInt(max - min + 1) + min;
        pushBack(steps);
    }

    // ========== MOVEMENT LOGIC ==========

    /**
     * Updates student behavior per sim tick.
     * A "movement opportunity" occurs whenever the timer elapses.
     * On a movement opportunity, a 1..20 roll is compared to AI level.
     */
    public void update() {
        // Transitional states are controlled by their respective systems.
        if (currentLocation == Location.IN_VENT || currentLocation == Location.IN_ELEVATOR) {
            return;
        }

        // RUNNER special state machine
        if (personality == Personality.RUNNER) {
            if (isRunnerDoneCharging()) return;
            if (!sprinting) return;
        }

        movementTimer--;
        if (movementTimer <= 0) {
            attemptMove();
            resetMovementTimer();
        }
    }

    /**
     * Advances the RUNNER charge timer and transitions to sprinting when complete.
     *
     * @return true if the runner is currently charging (i.e., movement should be blocked)
     */
    private boolean isRunnerDoneCharging() {
        if (charging) {
            chargeTimer--;
            if (chargeTimer <= 0) {
                charging = false;
                sprinting = true;
                resetMovementTimer();
                System.out.println(name + " SPRINTING!");
            }
            return true;
        }
        return false;
    }

    /**
     * Movement-opportunity logic.
     * Rolls 1..20 and moves iff roll <= AI level, unless stalled by cameras.
     */
    public void attemptMove() {
        Location before = currentLocation;

        // Camera stalling: some personalities cannot move while being watched.
        if (personality.stallsWhenWatched() && ObservationManager.isWatching(currentLocation)) {
            if (debugMoveLogs) {
                System.out.println("[AI-MOVE] " + name + " (" + personality + ") @ " + before +
                        " | STALL watched");
            }
            return;
        }

        // FNAF-style door timing: when a student reaches the door, they "hang" there
        // for one full movement opportunity before attempting to enter.
        if (currentLocation == Location.FLOOR3_AT_DOOR && doorLingerMovesRemaining > 0) {
            doorLingerMovesRemaining--;
            if (debugMoveLogs) {
                System.out.println("[AI-MOVE] " + name + " (" + personality + ") @ " + before +
                        " | DOOR linger (remaining=" + doorLingerMovesRemaining + ")");
            }
            return;
        }

        // RUNNER sprint is deterministic and always succeeds.
        if (personality == Personality.RUNNER) {
            if (!sprinting) return;
            doMoveStep();
            if (currentLocation == Location.IN_OFFICE) {
                sprinting = false;
            }
            if (debugMoveLogs) {
                System.out.println("[AI-MOVE] " + name + " (RUNNER) " + before + " -> " + currentLocation +
                        " | sprinting=" + sprinting);
            }
            return;
        }

        if (aiLevel <= 0) {
            if (debugMoveLogs) {
                System.out.println("[AI-MOVE] " + name + " (" + personality + ") @ " + before +
                        " | AI=" + aiLevel + " (no move)");
            }
            return;
        }

        int roll = rand.nextInt(20) + 1; // 1..20
        boolean willMove = roll <= aiLevel;

        if (willMove) doMoveStep();

        if (debugMoveLogs) {
            System.out.println("[AI-MOVE] " + name + " (" + personality + ") " + before +
                    " | roll=" + roll + " <= AI=" + aiLevel + " ? " + (willMove ? "MOVE" : "STAY") +
                    (willMove ? (" -> " + currentLocation) : ""));
        }

        if (currentLocation == Location.IN_OFFICE) {
            sprinting = false;
        }
    }

    private void doMoveStep() {
        Location nextLocation = NavigationManager.getNextLocation(this);
        if (nextLocation == null) return;

        setLocation(nextLocation);
        System.out.println(name + " moved to " + currentLocation);
    }

    /**
     * Chooses the preferred movement path based on personality.
     */
    private void selectPreferredPath() {
        preferredPath = personality.getPreferredPath();
    }

    /**
     * Resets the movement-opportunity timer.
     * Interval is character/personality-specific (FNAF-style).
     */
    private void resetMovementTimer() {
        if (personality == Personality.RUNNER) {
            // Runner only moves while sprinting; charging/idle runner is locked in place.
            movementTimer = sprinting ? 10 : Integer.MAX_VALUE;
            return;
        }

        double seconds = personality.getMoveOpportunityIntervalSeconds();
        seconds *= BUILDING_INTERVAL_MULTIPLIER;

        // As AI rises, checks happen slightly more frequently (more aggressive progression).
        seconds *= (1.0 - (Math.max(0, aiLevel) * INTERVAL_REDUCTION_PER_AI_LEVEL));
        seconds = Math.max(MIN_MOVE_INTERVAL_SECONDS, seconds);

        int frames = (int) Math.round(seconds * (double) TICKS_PER_SECOND);
        movementTimer = Math.max(1, frames);
    }

    // ========== ACTIONS ==========

    /**
     * Increases the student's AI level (0..20), capped at 20.
     */
    public void increaseDifficulty() {
        setAiLevel(aiLevel + 1);
        System.out.println(name + " AI level: " + aiLevel);
    }

    /**
     * Increases AI level by a delta amount.
     */
    public void increaseAiLevel(int delta) {
        if (delta <= 0) return;
        setAiLevel(aiLevel + delta);
    }

    /**
     * Sets the AI level, clamped to [0..20].
     */
    public void setAiLevel(int newLevel) {
        aiLevel = Math.max(0, Math.min(MAX_AI_LEVEL, newLevel));
    }

    /**
     * Starts the RUNNER charging phase, which later transitions into sprinting.
     * No effect for non-RUNNER students.
     */
    public void startSprint() {
        if (personality == Personality.RUNNER && !sprinting && !charging) {
            charging = true;
            chargeTimer = CHARGE_DURATION;
            System.out.println(name + " is CHARGING!");
        }
    }

    /**
     * Sets the student's current location and updates history.
     *
     * @param location new location
     */
    public void setLocation(Location location) {
        this.previousLocation = this.currentLocation;
        this.currentLocation = location;
        rememberLocation(location);

        // Door linger + warning flag. Only fires on transitions INTO the door node.
        if (location == Location.FLOOR3_AT_DOOR && previousLocation != Location.FLOOR3_AT_DOOR) {
            doorLingerMovesRemaining = 1;
            justArrivedAtDoor = true;
        }

        // Reset door state when leaving.
        if (location != Location.FLOOR3_AT_DOOR) {
            doorLingerMovesRemaining = 0;
        }
    }

    /**
     * Returns true exactly once when this student transitions into the office door node.
     */
    public boolean consumeJustArrivedAtDoor() {
        if (!justArrivedAtDoor) return false;
        justArrivedAtDoor = false;
        return true;
    }

    /**
     * Sets the internal movement timer.
     * Package-private for deterministic tests and simulation tools.
     *
     * @param t new movement timer value (frames)
     */
    void setMovementTimer(int t) { this.movementTimer = t; }

    // ========== GETTERS ==========

    /** @return student display name */
    public String getName() { return name; }

    /** @return student jumpscare question/dialogue */
    public String getQuestion() { return question; }

    /** @return student personality */
    public Personality getPersonality() { return personality; }

    /** @return current location */
    public Location getCurrentLocation() { return currentLocation; }

    /** @return previous location (may be null at start) */
    public Location getPreviousLocation() { return previousLocation; }

    /** @return preferred path type derived from personality */
    public PathType getPreferredPath() { return preferredPath; }

    /** @return current AI level (0..20) */
    public int getDifficulty() { return aiLevel; }

    /** @return current AI level (0..20) */
    public int getAiLevel() { return aiLevel; }

    /** @return awareness level (tuning value used by AI) */
    public double getAwarenessLevel() { return awarenessLevel; }

    /** @return true if currently sprinting */
    public boolean isSprinting() { return sprinting; }

    /** @return movement timer value in frames */
    public int getMovementTimer() { return movementTimer; }

    /** @return true if currently charging (RUNNER only) */
    public boolean isCharging() { return charging; }

    /** @return remaining charge timer in frames */
    public int getChargeTimer() { return chargeTimer; }

    /**
     * Counts how often a location appears in the student's short-term memory window.
     *
     * @param loc location to count
     * @return visit count within the last {@link #MEMORY_SIZE} remembered locations
     */
    public int getRecentVisitCount(Location loc) {
        int count = 0;
        for (Location l : recentLocations) {
            if (l == loc) count++;
        }
        return count;
    }

    /**
     * Returns a concise debug string.
     *
     * @return string form
     */
    @Override
    public String toString() {
        return name + " (" + personality + ") @ " + currentLocation;
    }
}