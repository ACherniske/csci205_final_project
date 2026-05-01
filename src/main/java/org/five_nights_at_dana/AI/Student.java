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
import java.util.*;

/**
 * Represents an individual student AI.
 * Handles personality-driven behavior, movement timing, state transitions,
 * and tracks historical location memory to assist in pathing decisions.
 */
public class Student {

    private final String name;
    private final String question;
    private final Personality personality;
    private Location currentLocation;
    private Location previousLocation;
    private PathType preferredPath;

    private int movementTimer;
    private int difficulty;
    private double awarenessLevel;
    private boolean charging;
    private int chargeTimer;
    private boolean sprinting;

    // Memory system for tracking recent path history
    private static final int MEMORY_SIZE = 5;
    private final Deque<Location> recentLocations = new ArrayDeque<>();

    // Controlled randomness for deterministic testing
    private static Random rand = new Random();

    // Tuning constants
    private static final int CHARGE_DURATION = 120; // frames -> 2s

    /**
     * Allows tests to inject a deterministic Random instance.
     * @param r Random instance (use new Random(seed) in tests)
     */
    public static void setRandom(Random r) {
        rand = r;
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

        this.difficulty = 0;
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
     * Updates student behavior per frame. Decrements the movement timer
     * and triggers pathfinding attempts when the timer expires.
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
     * Logic to determine if the student moves this frame based on calculated
     * probability and updates the current location node.
     */
    public void attemptMove() {
        double moveChance = calculateMoveChance();

        if (rand.nextDouble() < moveChance) {
            Location nextLocation = NavigationManager.getNextLocation(this);

            if (nextLocation != null) {
                this.previousLocation = this.currentLocation;
                this.currentLocation = nextLocation;
                rememberLocation(this.currentLocation);
                System.out.println(name + " moved to " + currentLocation);
            }
        }

        if (currentLocation == Location.IN_OFFICE) {
            sprinting = false;
        }
    }

    /**
     * Calculates the probability that this student will move when their timer elapses.
     *
     * @return movement probability in the range [0, 1]
     */
    private double calculateMoveChance() {
        double baseChance = 0.15 + (difficulty * 0.1);
        return switch (personality) {
            case EAGER -> baseChance * 1.5;
            case SHY -> baseChance * 0.7;
            case CONFUSED -> baseChance * (rand.nextDouble() * 2);
            case PERSISTENT -> baseChance * 1.2;
            case RUNNER -> sprinting ? 1.0 : 0.0;
        };
    }

    /**
     * Chooses the preferred movement path based on personality.
     */
    private void selectPreferredPath() {
        preferredPath = personality.getPreferredPath();
    }

    /**
     * Resets the movement cooldown timer. Higher difficulty reduces the wait time.
     */
    private void resetMovementTimer() {
        int baseTimer = Math.max(60, 180 - (difficulty * 20));
        movementTimer = switch (personality) {
            case EAGER -> (int) (baseTimer * 0.7);
            case SHY -> (int) (baseTimer * 1.3);
            case PERSISTENT -> (int) (baseTimer * 0.9);
            case RUNNER -> sprinting ? 10 : Integer.MAX_VALUE;
            default -> baseTimer;
        };
    }

    // ========== ACTIONS ==========

    /**
     * Increases the student's difficulty level, capped at a maximum value.
     */
    public void increaseDifficulty() {
        difficulty = Math.min(6, difficulty + 1);
        System.out.println(name + " difficulty: " + difficulty);
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

    /** @return current difficulty level */
    public int getDifficulty() { return difficulty; }

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