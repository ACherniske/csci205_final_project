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
import org.five_nights_at_dana.Managers.Notification;
import org.five_nights_at_dana.Systems.SensorHelper;
// TODO import org.five_nights_at_dana.Managers.AudioManager;

/**
 * Manages the "Runner" mechanic in the Computer Lab.
 * Tracks activity levels and triggers a charge/sprint event if the student is not monitored.
 */
public class ClassroomMechanic {

    /** Maximum activity threshold before the runner charges. */
    private static final int MAX_ACTIVITY = 100;

    /** Rate at which activity increases per frame when not watched. */
    private static final double ACTIVITY_INCREASE_RATE = 0.05;

    /** Frames to wait before activity begins increasing (3s). */
    private static final int RESET_COOLDOWN = 180; // frames -> 3s

    private double activityLevel;
    private boolean eventTriggered;
    private boolean warningTriggered;
    private int framesSinceCheck;
    private int currentFrame;

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
     * handles the threshold triggers for warnings and charging.
     */
    public void update() {
        // Always increment frame for consistent timestamps
        currentFrame++;

        if (eventTriggered) return;

        framesSinceCheck++;

        if (framesSinceCheck > RESET_COOLDOWN) {
            activityLevel += ACTIVITY_INCREASE_RATE;

            if (activityLevel >= MAX_ACTIVITY) {
                activityLevel = MAX_ACTIVITY;
                triggerCharge();
            }
        }

        // Trigger warning once when crossing threshold
        if (!warningTriggered && activityLevel >= 75) {
            warningTriggered = true;

            SensorHelper.trigger(
                    "runner_warning",
                    "Runner is getting restless...",
                    Notification.Type.WARNING,
                    currentFrame
            );
        }
    }

    /**
     * Initiates the charging phase for the runner.
     */
    private void triggerCharge() {
        if (eventTriggered || runner == null) return;

        eventTriggered = true;
        runner.startSprint();

        SensorHelper.trigger(
                "runner_charge",
                "Runner is charging in the Computer Lab",
                Notification.Type.RUNNER_CHARGING,
                currentFrame
        );

        System.out.println("ClassroomMechanic: Runner CHARGING");
    }

    /**
     * Resets the activity level to 0. Must be called when the player monitors
     * the camera (CAM 3D). If the event has already triggered, this will fail.
     */
    public void resetActivity() {
        if (eventTriggered) {
            System.out.println("ClassroomMechanic: Too Late!");
            return;
        }

        activityLevel = 0;
        framesSinceCheck = 0;
        warningTriggered = false;

        System.out.println("ClassroomMechanic: Activity reset (CAM 3D checked)");
    }

    /**
     * Checks if the runner event has been triggered.
     *
     * @return true if triggered, false otherwise.
     */
    public boolean isEventTriggered() {
        return eventTriggered;
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
        eventTriggered = false;
        warningTriggered = false;
        framesSinceCheck = 0;
        currentFrame = 0;
        runner = null;
    }
}
