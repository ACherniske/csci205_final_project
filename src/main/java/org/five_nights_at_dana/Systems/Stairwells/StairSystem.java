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

import java.util.*;
import org.five_nights_at_dana.AI.Location;
import org.five_nights_at_dana.AI.Student;
import org.five_nights_at_dana.Managers.AudioManager;
import org.five_nights_at_dana.Managers.Notification;
import org.five_nights_at_dana.Systems.SensorHelper;

/**
 * Manages the "Stair" mechanic across the facility.
 * Tracks student movement, triggers audio sensors, and handles the
 * activation of shared emergency light charges to deter students.
 */
public class StairSystem {

    private static String floorFromLocation(Location loc) {
        if (loc == null) {
            return "Unknown floor";
        }
        String name = loc.name();
        if (name.startsWith("FLOOR1_")) {
            return "Floor 1";
        }
        if (name.startsWith("FLOOR2_")) {
            return "Floor 2";
        }
        if (name.startsWith("FLOOR3_")) {
            return "Floor 3";
        }
        return "Unknown floor";
    }

    /** Power drain reported while emergency lights are active. */
    private static final double LIGHTS_POWER_DRAIN = 0.12;

    /** Duration lights stay on (5 seconds at 60 FPS). */
    private static final int LIGHT_DURATION = 300;

    /** Sensor cooldown (3 seconds at 60 FPS). */
    private static final int SENSOR_COOLDOWN = 180;

    private final Map<Stairwell, List<Student>> studentsInStairs;
    private final Map<SensorLocation, Integer> sensorLastTrigger;

    private Stairwell activeLightsStairwell;
    private int lightTimer;
    private int currentFrame;

    /**
     * Initializes the stair system with default values and empty occupancy lists.
     */
    public StairSystem() {
        studentsInStairs = new EnumMap<>(Stairwell.class);
        for (Stairwell s : Stairwell.values()) {
            studentsInStairs.put(s, new ArrayList<>());
        }

        sensorLastTrigger = new EnumMap<>(SensorLocation.class);
        for (SensorLocation sl : SensorLocation.values()) {
            sensorLastTrigger.put(sl, -SENSOR_COOLDOWN);
        }

        reset();
    }

    /**
     * Updates the stair system state, managing light timing.
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
     * Pushes students backward using memory-based movement.
     *
     * @param stairwell The stairwell to illuminate.
     * @return true if activation was successful.
     */
    public boolean activateLights(Stairwell stairwell) {
        if (!canActivateLights()) return false;
        activeLightsStairwell = stairwell;
        lightTimer = LIGHT_DURATION;
        AudioManager.play("light_switch", true);


        System.out.println("StairSystem: Lights ON in " + stairwell);

        deterStudentsInStairwell(stairwell);
        return true;
    }

    /**
     * Applies pushback to all students currently in the given stairwell.
     */
    private void deterStudentsInStairwell(Stairwell stairwell) {
        List<Student> students = studentsInStairs.get(stairwell);

        for (Student s : new ArrayList<>(students)) {

            int min = 1;
            int max = getPushbackStrength(stairwell, s);

            s.pushBackRandom(min, max);

            System.out.println("StairSystem: DETERRED " + s.getName() +
                    " in " + stairwell + " (-" + min + " to -" + max + " steps)");
        }
    }

    /**
     * Determines how strong the pushback should be based on stairwell and personality.
     */
    private int getPushbackStrength(Stairwell stairwell, Student s) {
        int base = switch (stairwell) {
            case LEFT -> 2;
            case MIDDLE -> 2;
            case RIGHT -> 3;
        };

        return switch (s.getPersonality()) {
            case SHY -> base + 1;
            case PERSISTENT -> Math.max(1, base - 1);
            case CONFUSED -> base + new Random().nextInt(2);
            case RUNNER -> base + 2;
            default -> base;
        };
    }

    /**
     * Deactivates emergency lights.
     */
    private void deactivateLights() {
        activeLightsStairwell = null;
        lightTimer = 0;

        System.out.println("StairSystem: Lights OFF");
    }

    /**
     * Resets the system to its initial state.
     */
    public void reset() {
        for (List<Student> list : studentsInStairs.values()) {
            list.clear();
        }

        sensorLastTrigger.replaceAll((s, v) -> -SENSOR_COOLDOWN);
        activeLightsStairwell = null;
        lightTimer = 0;
        currentFrame = 0;
    }

    /**
     * Checks if lights can currently be activated.
     */
    public boolean canActivateLights() {
        return activeLightsStairwell == null;
    }

    /**
     * Returns the power drain rate of the stair system.
     *
     * @return drain value when lights are active, 0.0 otherwise
     */
    public double getPowerDrain() {
        return activeLightsStairwell != null ? LIGHTS_POWER_DRAIN : 0.0;
    }

    /**
     * Checks if lights are active in a given stairwell.
     */
    public boolean areLightsActive(Stairwell stairwell) {
        return activeLightsStairwell == stairwell;
    }

    /**
     * Registers a student entering a stairwell.
     */
    public void studentEnterStairwell(Student student, Stairwell stairwell) {
        List<Student> list = studentsInStairs.get(stairwell);
        if (!list.contains(student)) {
            list.add(student);

            String floor = floorFromLocation(student.getCurrentLocation());
            AudioManager.play("stairs_up", true);

            SensorHelper.trigger(
                    "stair_" + stairwell,
                    student.getName() + " moved to " + floor + " (" + stairwell + " stairs)",
                    Notification.Type.STAIR_SENSOR,
                    currentFrame
            );
        }
    }

    /**
     * Removes a student from a stairwell.
     */
    public void studentExitStairwell(Student student, Stairwell stairwell) {
        studentsInStairs.get(stairwell).remove(student);
    }

    /**
     * Returns a copy of students in a stairwell.
     */
    public List<Student> getStudentsInStairwell(Stairwell stairwell) {
        return new ArrayList<>(studentsInStairs.get(stairwell));
    }

    // ===== Optional Getters for Debug / UI =====

    /**
     * Gets the stairwell whose emergency lights are currently active.
     *
     * @return active stairwell, or null if lights are off
     */
    public Stairwell getActiveLightsStairwell() {
        return activeLightsStairwell;
    }

    /**
     * Gets the remaining light duration in frames.
     *
     * @return remaining frames until lights auto-deactivate
     */
    public int getLightTimer() {
        return lightTimer;
    }

    /**
     * Gets the current internal frame counter for this system.
     *
     * @return frame counter
     */
    public int getCurrentFrame() {
        return currentFrame;
    }
}
