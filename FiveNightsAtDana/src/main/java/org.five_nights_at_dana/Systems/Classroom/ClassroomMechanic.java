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
 *
 * ****************************************
 */

package org.five_nights_at_dana.Systems.Classroom;

import org.five_nights_at_dana.AI.Student;

public class ClassroomMechanic {

    private double activityLevel;
    private boolean sprinting;
    private int framesSinceCheck;
    private Student runner;

    private static final int MAX_ACTIVITY = 100;
    private static final double ACTIVITY_INCREASE_RATE = 0.5;
    private static final int RESET_COOLDOWN = 60;

    public ClassroomMechanic() {
        reset();
    }

    /**
     * Updates classroom behavior.
     */
    public void update() {
        // TODO increase activity and trigger sprint
    }

    /**
     * Resets activity (called when camera is checked).
     */
    public void resetActivity() {
        activityLevel = 0;
        framesSinceCheck = 0;
    }

    /**
     * @return true if sprinting
     */
    public boolean isSprinting() {
        return sprinting;
    }

    /**
     * @return raw activity value
     */
    public double getActivityLevel() {
        return activityLevel;
    }

    /**
     * @return activity percentage
     */
    public double getActivityPercentage() {
        return activityLevel / MAX_ACTIVITY;
    }

    /**
     * Resets system.
     */
    public void reset() {
        activityLevel = 0;
        sprinting = false;
        framesSinceCheck = 0;
        runner = null;
    }

    /**
     * Triggers sprint event.
     */
    private void triggerSprint() {
        // TODO set sprinting state and notify systems
    }
}
