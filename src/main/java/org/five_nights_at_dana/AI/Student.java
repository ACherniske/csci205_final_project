/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 3:44 AM
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
import java.util.Random;

/**
 * Represents an individual student AI.
 * Handles personality-driven behavior, movement timing, and state.
 * Navigation decisions are delegated to NavigationManager.
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

    // Controlled randomness for deterministic testing
    private static Random rand = new Random();

    // tuning constants
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
            this.currentLocation = Location.FLOOR3_COMPUTER_LAB; // Like Foxy's Cove
        } else {
            this.currentLocation = Location.FLOOR1_ENTRANCE; // Like FNAF Stage
        }

        selectPreferredPath();
        resetMovementTimer();
    }

    /**
     * Updates student behavior per frame. Decrements the movement timer
     * and triggers pathfinding attempts when the timer expires.
     */
    public void update() {
        // RUNNER special state machine
        if (personality == Personality.RUNNER) {

            // Charging phase
            if (isRunnerDoneCharging()) return; // do not move while charging

            // Idle phase
            if (!sprinting) {
                return;
            }
        }

        movementTimer--;
        if (movementTimer <= 0) {
            attemptMove();
            resetMovementTimer();
        }
    }

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
                previousLocation = currentLocation;
                currentLocation = nextLocation;
                System.out.println(name + " moved to " + currentLocation);
            }
        }

        if (currentLocation == Location.IN_OFFICE) {
            sprinting = false; // for resetting runner
        }
    }

    /**
     * Calculates the probability of moving this frame based on personality,
     * difficulty, and active states.
     * * @return A double between 0.0 and 1.0 representing movement chance.
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
     * Assigns the {@link PathType} preference based on the student's personality.
     */
    private void selectPreferredPath() {
        preferredPath = personality.getPreferredPath();
    }

    /**
     * Resets the movement cooldown timer. Higher difficulty reduces the wait time,
     * making students move more frequently.
     */
    private void resetMovementTimer() {
        int baseTimer = 180; // frames -> 3s

        // Difficulty speeds up movement
        baseTimer -= (difficulty * 20);

        baseTimer = switch (personality) {
            case EAGER -> (int) (baseTimer * 0.7);
            case SHY -> (int) (baseTimer * 1.3);
            case PERSISTENT -> (int) (baseTimer * 0.9);
            case RUNNER -> sprinting ? 10 : Integer.MAX_VALUE;
            default -> baseTimer;
        };

        if (personality == Personality.RUNNER && sprinting) {
            movementTimer = baseTimer; // allow fast movement (e.g., 10)
        } else {
            movementTimer = Math.max(30, baseTimer);
        }
    }

    /**
     * Increments the global difficulty level, speeding up AI behavior (called each hour).
     */
    public void increaseDifficulty() {
        difficulty = Math.min(6, difficulty + 1);
        System.out.println(name + " difficulty: " + difficulty);
    }

    /**
     * Triggers the sprint event for the RUNNER personality type.
     */
    public void startSprint() {
        if (personality == Personality.RUNNER && !sprinting && !charging) {
            charging = true;
            chargeTimer = CHARGE_DURATION;
            System.out.println(name + " is CHARGING!");
        }
    }

    /**
     * Updates the student's location. Used by external system mechanics.
     * @param location The new {@link Location} for the student.
     */
    public void setLocation(Location location) {
        this.previousLocation = this.currentLocation;
        this.currentLocation = location;
    }

    void setMovementTimer(int t) {
        this.movementTimer = t;
    }

    public String getName() { return name; }
    public String getQuestion() { return question; }
    public Personality getPersonality() { return personality; }
    public Location getCurrentLocation() { return currentLocation; }
    public Location getPreviousLocation() { return previousLocation; }
    public PathType getPreferredPath() { return preferredPath; }
    public int getDifficulty() { return difficulty; }
    public double getAwarenessLevel() { return awarenessLevel; }
    public boolean isSprinting() { return sprinting; }
    public int getMovementTimer() { return movementTimer; }
    public boolean isCharging() { return charging; }
    public int getChargeTimer() { return chargeTimer; }

    /**
     * Debug-friendly string output for simulation/testing.
     */
    @Override
    public String toString() {
        return name + " (" + personality + ") @ " + currentLocation;
    }
}