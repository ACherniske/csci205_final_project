/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 4:47 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Systems.Vent
 * Class: VentSystem
 *
 * Description:
 *      Vent system.
 *      Controls vent sealing and hidden student movement.
 *
 * ****************************************
 */

package org.five_nights_at_dana.Systems.Vent;

import org.five_nights_at_dana.AI.Student;
import org.five_nights_at_dana.AI.Location;
// TODO import org.five_nights_at_dana.Managers.AudioManager;

public class VentSystem {

    private static final int SEAL_DURATION = 480; // frames -> 8s
    private static final int SEAL_COOLDOWN = 720; //frames -> 12s
    private static final double SEAL_POWER_DRAIN = 0.08;

    // Travel times
    private static final int V1_TRAVEL_TIME = 900; // frames -> 15s
    private static final int V2_TRAVEL_TIME = 600; // frames -> 10s

    private boolean isSealed;
    private Student studentInVent;
    private Location ventEntryPoint;
    private int travelTimer;
    private int sealTimer;
    private int sealCooldown;

    public VentSystem() {
        reset();
    }

    /**
     * Updates vent state.
     */
    public void update() {
        if (isSealed) {
            sealTimer--;
            if (sealTimer <= 0) {
                unsealVent();
            }
        }

        if (sealCooldown > 0) {
            sealCooldown--;
        }

        if (studentInVent != null && !isSealed) {
            travelTimer--;

            if (travelTimer == 180) {
                // TODO AudioManager.play("vent_close");
                System.out.println("VentSystem: Student exiting in 3s");
            }

            if (travelTimer <= 0) {
                studentExitVent();
            }
        }
    }

    /**
     * Attempts to register a student into the vent system.
     *
     * @param student    The student object entering the vent.
     * @param entryPoint The specific location (vent entrance) the student is using.
     * @return true if the student successfully entered; false if the vent is sealed,
     * already occupied, or the entry point is invalid.
     */
    public boolean studentEnterVent(Student student, Location entryPoint) {
        if (isSealed || studentInVent != null) {
            return false;
        }

        switch (entryPoint) {
            case FLOOR1_GARDNER:  // V1
                travelTimer = V1_TRAVEL_TIME;
                System.out.println("VentSystem: " + student.getName() + " entered V1 (15s)");
                break;
            case FLOOR2_CLASSROOM:  // V2
                travelTimer = V2_TRAVEL_TIME;
                System.out.println("VentSystem: " + student.getName() + " entered V2 (10s)");
                break;
            default:
                System.out.println("VentSystem: Invalid entry attempt from " + entryPoint);
                return false;
        }

        studentInVent = student;
        ventEntryPoint = entryPoint;

        // TODO AudioManager.play("vent_enter");
        return true;
    }

    /**
     * Handles the logic for when a student finishes their transit through the vent.
     * Triggers the exit audio and clears the student from the system.
     */
    private void studentExitVent() {
        if (studentInVent == null) return;

        System.out.println("VentSystem: " + studentInVent.getName() + " exited at office!");
        // TODO AudioManager.play("vent_exit");

        // Student now at door
        // TODO: studentInVent.setLocation(Location.FLOOR3_AT_DOOR);

        studentInVent = null;
        ventEntryPoint = null;
        travelTimer = 0;
    }

    /**
     * Seals the vent, preventing student entry and ejecting any student currently inside.
     *
     * @return true if the seal was successfully applied; false if the vent is already
     * sealed or the system is on cooldown.
     */
    public boolean sealVent() {
        if (sealCooldown > 0 || isSealed) {
            return false;
        }

        isSealed = true;
        sealTimer = SEAL_DURATION;
        sealCooldown = SEAL_COOLDOWN;

        // TODO AudioManager.play("vent_seal");
        System.out.println("VentSystem: SEALED (8s)");

        if (studentInVent != null) {
            ejectStudent();
        }

        return true;
    }

    /**
     * Forcefully ejects a student from the vent if the seal is activated.
     */
    private void ejectStudent() {
        if (studentInVent == null) return;

        System.out.println("VentSystem: EJECTED " + studentInVent.getName());
        // TODO AudioManager.play("vent_eject");

        // TODO: studentInVent.setLocation(ventEntryPoint);

        studentInVent = null;
        ventEntryPoint = null;
        travelTimer = 0;
    }

    /**
     * Resets the vent state to unsealed and clears all active timers and student data.
     */
    void unsealVent() {
        isSealed = false;
        sealTimer = 0;
        // TODO AudioManager.play("vent_unseal");
    }

    /**
     * Checks if the vent is currently in a sealed state.
     *
     * @return true if sealed, false otherwise.
     */
    public boolean isSealed() {
        return isSealed;
    }

    /**
     * Checks if a student is currently inside the vent.
     *
     * @return true if occupied, false otherwise.
     */
    public boolean hasStudent() {
        return studentInVent != null;
    }

    /**
     * Retrieves the student currently inside the vent.
     *
     * @return The Student object, or null if empty.
     */
    public Student getStudentInVent() {
        return studentInVent;
    }

    /**
     * Calculates the remaining travel time for the student currently in the vent.
     *
     * @return Remaining time in seconds, or 0 if no student is inside.
     */
    public int getTravelTimeRemainingSeconds() {
        return studentInVent == null ? 0 : travelTimer / 60;
    }

    /**
     * Returns the current power drain rate of the vent system.
     *
     * @return The power drain value if sealed, 0.0 otherwise.
     */
    public double getPowerDrain() {
        return isSealed() ? SEAL_POWER_DRAIN : 0.0;
    }

    /**
     * Checks if the user is allowed to seal the vent (not currently sealed and no cooldown).
     *
     * @return true if sealing is permitted.
     */
    public boolean canSeal() {
        return sealCooldown == 0 && !isSealed;
    }

    /**
     * Gets the remaining time on the seal cooldown.
     *
     * @return Cooldown remaining in seconds.
     */
    public int getSealCooldownSeconds() {
        return sealCooldown / 60;
    }

    /**
     * Resets the system to its initial state, useful for level transitions or game overs.
     */
    public void reset() {
        isSealed = false;
        studentInVent = null;
        ventEntryPoint = null;
        travelTimer = 0;
        sealTimer = 0;
        sealCooldown = 0;
    }
}
