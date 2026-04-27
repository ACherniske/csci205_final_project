/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/27/2026
 * Time: 2:13 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Systems.Stairwells
 * Class: StairSystem
 *
 * Description:
 * Unified stair system managing all three stairwells.
 * Each stairwell features motion sensors and emergency light
 * deterrents used to manage student movement.
 *
 * ****************************************
 */

package org.five_nights_at_dana.Systems.Stairwells;

import org.five_nights_at_dana.AI.Student;
import org.five_nights_at_dana.AI.Location;
// TODO import org.five_nights_at_dana.Managers.AudioManager;

import java.util.*;

/**
 * Manages the "Stair" mechanic across the facility.
 * Tracks student movement, triggers audio sensors, and handles the
 * activation of shared emergency light charges to deter students.
 */
public class StairSystem {

    /** Total emergency light charges available per night. */
    private static final int TOTAL_LIGHT_CHARGES = 5;

    /** Duration lights stay on (5 seconds at 60 FPS). */
    private static final int LIGHT_DURATION = 300;

    /** Sensor cooldown (3 seconds at 60 FPS). */
    private static final int SENSOR_COOLDOWN = 180;

    private final Map<Stairwell, List<Student>> studentsInStairs;
    private final Map<SensorLocation, Integer> sensorLastTrigger;

    private Stairwell activeLightsStairwell;
    private int lightTimer;
    private int lightCharges;
    private int currentFrame;

    /**
     * Initializes the stair system with default values and empty occupancy lists.
     */
    public StairSystem() {
        this.studentsInStairs = new EnumMap<>(Stairwell.class);
        for (Stairwell s : Stairwell.values()) {
            studentsInStairs.put(s, new ArrayList<>());
        }

        this.sensorLastTrigger = new EnumMap<>(SensorLocation.class);
        for (SensorLocation sl : SensorLocation.values()) {
            sensorLastTrigger.put(sl, -SENSOR_COOLDOWN);
        }

        reset();
    }

    /**
     * Updates the stair system state, managing light times and frame counting.
     */
    public void update() {
        currentFrame++;

        if (activeLightsStairwell != null) {
            lightTimer--;
            if (lightTimer <= 0) {
                deactivateLights();
            }
        }
    }

    /**
     * Activates emergency lights in the specified stairwell.
     * Pushes all students back one floor.
     *
     * @param stairwell The stairwell to illuminate.
     * @return true if activation was successful, false if no charges remain or lights active.
     */
    public boolean activateLights(Stairwell stairwell) {
        if (!canActivateLights()) return false;

        lightCharges--;
        activeLightsStairwell = stairwell;
        lightTimer = LIGHT_DURATION;

        // TODO AudioManager.play("emergency_lights_on");
        System.out.println("StairSystem: Lights ON in " + stairwell + " (" + lightCharges + " left)");

        deterStudentsInStairwell(stairwell);
        return true;
    }

    /**
     * Internal logic to retreat all students in a stairwell.
     * * @param stairwell The stairwell to target.
     */
    private void deterStudentsInStairwell(Stairwell stairwell) {
        List<Student> students = studentsInStairs.get(stairwell);
        for (Student s : new ArrayList<>(students)) {
            Location backLoc = getPushedBackLocation(s.getCurrentLocation());
            if (backLoc != null) {
                s.setLocation(backLoc);
                // TODO AudioManager.play("student_retreat");
                System.out.println("StairSystem: DETERRED " + s.getName() + " to " + backLoc);
            }
        }
    }

    /**
     * Deactivates emergency lights and resets the timer.
     */
    private void deactivateLights() {
        activeLightsStairwell = null;
        lightTimer = 0;
        // TODO AudioManager.play("emergency_lights_off");
        System.out.println("StairSystem: Lights OFF");
    }

    /**
     * Determines the location one floor below the current one within the same stairwell.
     * * @param current The current student location.
     * @return The location one floor lower, or null if already at the bottom.
     */
    private Location getPushedBackLocation(Location current) {
        // Implementation logic for floor regression...
        return null;
    }

    /**
     * Resets the entire system to its initial state.
     */
    public void reset() {
        for (List<Student> list : studentsInStairs.values()) {
            list.clear();
        }
        sensorLastTrigger.replaceAll((s, v) -> -SENSOR_COOLDOWN);
        lightCharges = TOTAL_LIGHT_CHARGES;
        activeLightsStairwell = null;
        lightTimer = 0;
        currentFrame = 0;
    }

    /**
     * Checks if light activation is currently possible.
     * * @return true if charges exist and no other lights are active.
     */
    public boolean canActivateLights() {
        return lightCharges > 0 && activeLightsStairwell == null;
    }

    /**
     * Gets remaining light charges.
     * * @return Number of light activations remaining.
     */
    public int getLightCharges() {
        return lightCharges;
    }

    /**
     * Checks if lights are currently active in a specific stairwell.
     *
     * @param stairwell The stairwell to check.
     * @return true if lights are active, false otherwise.
     */
    public boolean areLightsActive(Stairwell stairwell) {
        return activeLightsStairwell == stairwell;
    }

    /**
     * Registers a student into the specified stairwell.
     * * @param student The student entering the stairwell.
     * @param stairwell The stairwell they entered.
     */
    public void studentEnterStairwell(Student student, Stairwell stairwell) {
        if (!studentsInStairs.get(stairwell).contains(student)) {
            studentsInStairs.get(stairwell).add(student);
        }
    }

    /**
     * Removes a student from the specified stairwell.
     * * @param student The student leaving the stairwell.
     * @param stairwell The stairwell they exited.
     */
    public void studentExitStairwell(Student student, Stairwell stairwell) {
        studentsInStairs.get(stairwell).remove(student);
    }

    /**
     * Returns a copy of the list of students in the given stairwell.
     * * @param stairwell The stairwell to check.
     * @return A list of students currently in that stairwell.
     */
    public List<Student> getStudentsInStairwell(Stairwell stairwell) {
        return new ArrayList<>(studentsInStairs.get(stairwell));
    }
}