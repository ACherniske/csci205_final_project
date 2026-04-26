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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents an individual student AI.
 * Each student uses a personality-based state machine to navigate
 * the building via different {@link PathType} options.
 */
public class Student {

    private String name;
    private String question;
    private Personality personality;
    private Location currentLocation;
    private PathType preferredPath;
    private int movementTimer;
    private int difficulty;
    private double awarenessLevel;
    private boolean sprinting;

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
        this.sprinting = false;

        if (personality == Personality.RUNNER) {
            this.currentLocation = Location.FLOOR3_COMPUTER_LAB;
        } else {
            this.currentLocation = Location.FLOOR1_ENTRANCE;
        }

        selectPreferredPath();
        resetMovementTimer();
    }

    /**
     * Assigns the {@link PathType} preference based on the student's personality.
     */
    private void selectPreferredPath() {
        switch (personality) {
            case EAGER:
                preferredPath = PathType.ELEVATOR;
                break;
            case SHY:
                preferredPath = PathType.VENT;
                break;
            case CONFUSED:
                preferredPath = PathType.MIDDLE_STAIRS;
                break;
            case PERSISTENT:
                preferredPath = PathType.LEFT_STAIRS;
                break;
            default:
                preferredPath = PathType.RIGHT_STAIRS;
        }
    }

    /**
     * Updates student behavior per frame. Decrements the movement timer
     * and triggers pathfinding attempts when the timer expires.
     */
    public void update() {
        if (personality == Personality.RUNNER && !sprinting) {
            return;
        }

        movementTimer--;
        if (movementTimer <= 0) {
            attemptMove();
            resetMovementTimer();
        }
    }

    /**
     * Logic to determine if the student moves this frame based on calculated
     * probability and updates the current location node.
     */
    public void attemptMove() {
        double moveChance = calculateMoveChance();

        if (Math.random() < moveChance) {
            Location nextLocation = calculateNextLocation();
            if (nextLocation != null) {
                currentLocation = nextLocation;
                System.out.println(name + "moved to" + currentLocation);
            }
        }
    }

    /**
     * Calculates the probability of moving this frame based on personality,
     * difficulty, and active states.
     * * @return A double between 0.0 and 1.0 representing movement chance.
     */
    private double calculateMoveChance() {
        double baseChance = 0.15 + (difficulty * 0.1);

        switch (personality) {
            case EAGER:
                return baseChance * 1.5;
            case SHY:
                return baseChance * 0.7;
            case CONFUSED:
                return baseChance * (Math.random() * 2);
            case PERSISTENT:
                return baseChance * 1.2;
            case RUNNER:
                return sprinting ? 1.0 : 0.0;
        }
        return baseChance;
    }

    /**
     * The core pathfinding state machine. Determines the next valid {@link Location}
     * based on the student's current node and preferred pathing.
     * * @return The next Location node, or null if movement is blocked.
     */
    private Location calculateNextLocation() {
        List<Location> neighbors = NavigationManager.getNeighbors(this.currentLocation);

        if (neighbors.isEmpty()) return null;

        // 1. Filter for valid moves
        List<Location> validMoves = new ArrayList<>();
        for (Location loc : neighbors) {
            // Add custom constraints (e.g., is vent sealed?)
            validMoves.add(loc);
        }

        // 2. Logic: Prioritize preferredPath, otherwise pick random
        for (Location loc : validMoves) {
            if (isLocMatchingPreferredPath(loc)) {
                return loc;
            }
        }

        // 3. Fallback to random valid move
        return validMoves.get(new Random().nextInt(validMoves.size()));
    }

    /**
     * Evaluates whether a target location aligns with the student's {@link PathType} preference.
     * <p>
     * If the target location is not specifically tied to a path (e.g., a neutral hallway),
     * the method returns {@code true} to allow for continuous movement.
     * </p>
     * @param loc The candidate {@link Location} the student is considering moving to.
     * @return {@code true} if the location matches the preferred path or is a neutral zone;
     * {@code false} otherwise.
     */
    private boolean isLocMatchingPreferredPath(Location loc) {
        // If the location has a path type, check if it matches the student's preference
        PathType locPath = loc.getPathType();

        // If locPath is null, it's likely a hallway or office;
        // we allow these as they are necessary for navigation.
        if (locPath == null) return true;

        return locPath == this.preferredPath;
    }

    /**
     * Determines initial pathing choice from the entrance.
     */
    private Location chooseFloor1Path() {
        // TODO logic
        return Location.FLOOR1_HALLWAY_LEFT;
    }

    // TODO logic
    private Location transitionToFloor2Stairs() { /* ... */ return null; }
    private Location transitionToFloor3Stairs() { /* ... */ return null; }

    /**
     * Resets the movement cooldown timer. Higher difficulty reduces the wait time,
     * making students move more frequently.
     */
    private void resetMovementTimer() {
        // TODO logic
    }

    /**
     * Increments the global difficulty level, speeding up AI behavior (called each hour).
     */
    public void increaseDifficulty() {
        difficulty = Math.min(6, difficulty + 1);
        System.out.println(name + " difficulty → " + difficulty);
    }

    /**
     * Triggers the sprint event for the RUNNER personality type.
     */
    public void startSprint() {
        if (personality == Personality.RUNNER) {
            sprinting = true;
            resetMovementTimer();
            System.out.println(name + " SPRINTING!");
        }
    }

    /**
     * Updates the student's location. Used by external system mechanics.
     * @param location The new {@link Location} for the student.
     */
    public void setLocation(Location location) {
        this.currentLocation = location;
    }

    public String getName() { return name; }
    public String getQuestion() { return question; }
    public Personality getPersonality() { return personality; }
    public Location getCurrentLocation() { return currentLocation; }
    public PathType getPreferredPath() { return preferredPath; }
    public int getDifficulty() { return difficulty; }
    public double getAwarenessLevel() { return awarenessLevel; }
    public boolean isSprinting() { return sprinting; }
}