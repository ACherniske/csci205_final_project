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
import org.five_nights_at_dana.Managers.AudioManager;

public class VentSystem {

    private static final int SEAL_DURATION = 480; // frames -> 8s
    private static final int SEAL_COOLDOWN = 720; //frames -> 12s
    private static final double SEAL_POWER_DRAIN = 0.08;

    // Travel times
    private static final int V1_TRAVEL_TIME = 900; // frames -> 15s
    private static final int V2_TRAVEL_TIME = 600; // frames -> 10s

    private boolean ventSealed;
    private Student studentInVent;
    private Student.Location ventEntryPoint;
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
        if (ventSealed) {
            sealTimer--;
            if (sealTimer <= 0) {
                unsealVent();
            }
        }

        if (sealCooldown > 0) {
            sealCooldown--;
        }

        if (studentInVent != null && !ventSealed) {
            travelTimer--;

            if (travelTimer == 180) {
                AudioManager.play("vent_close");
                System.out.println("VentSystem: Student exiting in 3s");
            }

            if (travelTimer <= 0) {
                studentExitVent();
            }
        }
    }

    public boolean studentEnterVent(Student student, Student.Location entryPoint) {
        if (ventSealed || studentInVent != null) {
            return false;
        }

        studentInVent = student;
        ventEntryPoint = entryPoint;

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

        AudioManager.play("vent_enter");
        return true;
    }

    private void studentExitVent() {
        if (studentInVent == null) return;

        System.out.println("VentSystem: " + studentInVent.getName() + " exited at office!");
        AudioManager.play("vent_exit");

        // Student now at door
        // TODO: studentInVent.setLocation(Student.Location.FLOOR3_AT_DOOR);

        studentInVent = null;
        ventEntryPoint = null;
        travelTimer = 0;
    }

    /**
     * Seals the vent.
     *
     * @return true if successful
     */
    public boolean sealVent() {
        if (sealCooldown > 0 || ventSealed) {
            return false;
        }

        ventSealed = true;
        sealTimer = SEAL_DURATION;
        sealCooldown = SEAL_COOLDOWN;

        AudioManager.play("vent_seal");
        System.out.println("VentSystem: SEALED (8s)");

        if (studentInVent != null) {
            ejectStudent();
        }

        return true;
    }

    /**
     * Ejects students in the vent.
     */
    private void ejectStudent() {
        if (studentInVent == null) return;

        System.out.println("VentSystem: EJECTED " + studentInVent.getName());
        AudioManager.play("vent_eject");

        // TODO: studentInVent.setLocation(ventEntryPoint);

        studentInVent = null;
        ventEntryPoint = null;
        travelTimer = 0;
    }

    /**
     * Unseals the vent.
     */
    private void unsealVent() {
        ventSealed = false;
        sealTimer = 0;
        AudioManager.play("vent_unseal");
    }

    /**
     * @return whether vent is sealed
     */
    public boolean isSealed() {
        return ventSealed;
    }

    public boolean hasStudent() {
        return studentInVent != null;
    }

    public Student getStudentInVent() {
        return studentInVent;
    }

    public int getTravelTimeRemainingSeconds() {
        return studentInVent == null ? 0 : travelTimer / 60;
    }

    /**
     * @return power drain
     */
    public double getPowerDrain() {
        return ventSealed ? SEAL_POWER_DRAIN : 0.0;
    }

    public boolean canSeal() {
        return sealCooldown == 0 && !ventSealed;
    }

    public int getSealCooldownSeconds() {
        return sealCooldown / 60;
    }

    /**
     * Resets system.
     */
    public void reset() {
        ventSealed = false;
        studentInVent = null;
        ventEntryPoint = null;
        travelTimer = 0;
        sealTimer = 0;
        sealCooldown = 0;
    }
}
