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
import org.five_nights_at_dana.Systems.Vent.VentSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents an individual student AI.
 * Handles personality-driven behavior, movement timing, and state.
 * Navigation decisions are delegated to NavigationManager.
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
        // RUNNER only moves when sprinting
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
            Location nextLocation = NavigationManager.getNextLocation(this);

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
     * Resets the movement cooldown timer. Higher difficulty reduces the wait time,
     * making students move more frequently.
     */
    private void resetMovementTimer() {
        int baseTimer = 180; // frames -> 3s

        // Difficulty speeds up movement
        baseTimer -= (difficulty * 20);

        switch (personality) {
            case EAGER:
                baseTimer = (int) (baseTimer * 0.7);
                break;
            case SHY:
                baseTimer = (int) (baseTimer * 1.3);
                break;
            case PERSISTENT:
                baseTimer = (int) (baseTimer * 0.9);
                break;
            case RUNNER:
                baseTimer = sprinting ? 10 : Integer.MAX_VALUE;
                break;
        }

        movementTimer = Math.max(30, baseTimer);
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
