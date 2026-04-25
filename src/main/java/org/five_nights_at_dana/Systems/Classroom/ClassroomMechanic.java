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
import org.five_nights_at_dana.Managers.AudioManager;

/**
 * Runner mechanic for Computer Lab (CAM 3D).
 * Student sits here and must be watched.
 */
public class ClassroomMechanic {

    private static final int MAX_ACTIVITY = 100;
    private static final double ACTIVITY_INCREASE_RATE = 0.05;
    private static final int RESET_COOLDOWN = 180; // frames -> 3s

    private double activityLevel;
    private boolean sprinting;
    private int framesSinceCheck;
    private Student runner;

    public ClassroomMechanic() {
        reset();
    }

    public void setRunner(Student runner) {
        this.runner = runner;
        System.out.println("ClassroomMechanic: Runner set to " + runner.getName());
    }

    /**
     * Updates classroom behavior.
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
            AudioManager.play("runner_warning");
        }
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
     * Triggers sprint event.
     */
    private void triggerSprint() {
        if (sprinting || runner == null) return;

        sprinting = true;
        runner.startSprint();
        AudioManager.play("runner_sprint");
        System.out.println("ClassroomMechanic: Runner SPRINTING");
    }

    /**
     * Resets activity (called when camera is checked).
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
     * Resets system.
     */
    public void reset() {
        activityLevel = 0;
        sprinting = false;
        framesSinceCheck = 0;
        runner = null;
    }
}
