/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 4:48 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Systems.Classroom
 * Class: ClassroomMechanic
 *
 * Description:
 * Runner mechanic for Computer Lab (CAM 3D).
 * Student sits here and must be watched.
 * ****************************************
 */

package org.five_nights_at_dana.Systems.Classroom;

import org.five_nights_at_dana.AI.Student;
// TODO import org.five_nights_at_dana.Managers.AudioManager;

/**
 * Manages the "Runner" mechanic in the Computer Lab.
 * Tracks activity levels and triggers a sprint event if the student is not monitored.
 */
public class ClassroomMechanic {

    /** Maximum activity threshold before the runner sprints. */
    private static final int MAX_ACTIVITY = 100;

    /** Rate at which activity increases per frame when not watched. */
    private static final double ACTIVITY_INCREASE_RATE = 0.05;

    /** Frames to wait before activity begins increasing (3s). */
    private static final int RESET_COOLDOWN = 180; // frames -> 3s

    private double activityLevel;
    private boolean sprinting;
    private int framesSinceCheck;
    private Student runner;

    /**
     * Constructs a new ClassroomMechanic and initializes default state.
     */
    public ClassroomMechanic() {
        reset();
    }

    /**
     * Assigns the runner student to this mechanic.
     *
     * @param runner The Student object representing the runner.
     */
    public void setRunner(Student runner) {
        this.runner = runner;
        System.out.println("ClassroomMechanic: Runner set to " + runner.getName());
    }

    /**
     * Updates the classroom behavior logic. Increments activity levels and
     * handles the threshold triggers for warnings and sprinting.
     */
    public void update() {
        if (sprinting) return;

        framesSinceCheck++;

        if (framesSinceCheck > RESET_COOLDOWN) {
            activityLevel += ACTIVITY_INCREASE_RATE;

            if (activityLevel >= MAX_ACTIVITY) {
                activityLevel = MAX_ACTIVITY;
                triggerSprint();
            }
        }

        if (activityLevel >= 75 && activityLevel < 75.1) {
            // TODO AudioManager.play("runner_warning");
        }
    }

    /**
     * Initiates the sprint event for the runner if conditions are met.
     */
    private void triggerSprint() {
        if (sprinting || runner == null) return;

        sprinting = true;
        runner.startSprint();
        // TODO AudioManager.play("runner_sprint");
        System.out.println("ClassroomMechanic: Runner SPRINTING");
    }

    /**
     * Resets the activity level to 0. Must be called when the player monitors
     * the camera (CAM 3D). If the runner is already sprinting, this will fail.
     */
    public void resetActivity() {
        if (sprinting) {
            System.out.println("ClassroomMechanic: Too Late!");
            return;
        }

        activityLevel = 0;
        framesSinceCheck = 0;
        System.out.println("ClassroomMechanic: Activity reset (CAM 3D checked)");
    }

    /**
     * Checks if the runner is currently sprinting.
     *
     * @return true if sprinting, false otherwise.
     */
    public boolean isSprinting() {
        return sprinting;
    }

    /**
     * Gets the raw accumulated activity value.
     *
     * @return The current activity level.
     */
    public double getActivityLevel() {
        return activityLevel;
    }

    /**
     * Calculates the activity level as a percentage (0.0 to 1.0).
     *
     * @return Activity level percentage.
     */
    public double getActivityPercentage() {
        return activityLevel / MAX_ACTIVITY;
    }

    /**
     * Resets the entire system to its initial state, clearing activity,
     * resetting the timer, and removing the reference to the runner.
     */
    public void reset() {
        activityLevel = 0;
        sprinting = false;
        framesSinceCheck = 0;
        runner = null;
    }
}
