/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 4:11 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Systems.Elevator
 * Class: ElevatorSystem
 *
 * Description:
 *      Controls elevator behavior.
 *
 * ****************************************
 */

package org.five_nights_at_dana.Systems.Elevator;

import org.five_nights_at_dana.AI.Location;
import org.five_nights_at_dana.AI.Student;
import org.five_nights_at_dana.Managers.Notification;
import org.five_nights_at_dana.Systems.SensorHelper;

/**
 * Controls elevator behavior.
 */
public class ElevatorSystem {

    private static final int ELEVATOR_TRAVEL_TIME = 600;
    private static final int EMERGENCY_STOP_DURATION = 600;
    private static final int EMERGENCY_STOP_COOLDOWN = 720;
    private static final double EMERGENCY_STOP_POWER_DRAIN = 0.08;

    private int emergencyStopCooldown;
    private int emergencyTimer;
    private int elevatorTimer;

    private boolean isEmergencyStopped;
    private Student studentInElevator;
    private Location elevatorEntry;

    private int currentFrame;

    /**
     * Gets the floor from a location a student enters the elevator from
     * for notifications
     * @param loc the location
     * @return String Floor + Floor Number
     */
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


    /**
     * Constructor
     */
    public ElevatorSystem() {
        reset();
    }

    /**
     * Updates elevator logic.
     */
    public void update() {
        currentFrame++;
        if (isEmergencyStopped) {
            emergencyTimer--;
            if (emergencyTimer <= 0) {
                unEmergencyStop();
            }
        }
        if (emergencyStopCooldown > 0) {
            emergencyStopCooldown--;
        }

        // Handle transit
        if (!isEmergencyStopped && studentInElevator != null) {
            elevatorTimer--;

            // Threshold warning
            if (elevatorTimer == 180) {
                System.out.println("ElevatorSystem: Student arriving in 3s");
                // TODO AudioManager.play("elevator_open");
            }

            // Transit complete
            if (elevatorTimer <= 0) {
                studentExitElevator();
            }
        }
    }


    /**
     * Attempts to register a student in the elevator system
     *
     * @param student The student object entering the elevator.
     * @param entryPoint The specific location the student is using
     * @return true if teh student succesfully entered, false if the elevator is stopped,
     *      already occupied, or entry point is invalid.
     */
    public boolean studentEnterElevator(Student student, Location entryPoint) {
        if (isEmergencyStopped || studentInElevator != null) {
            return false;
        }

        // Define valid entry points for the elevator
        if (entryPoint == Location.FLOOR1_ELEVATOR || entryPoint == Location.FLOOR3_ELEVATOR_EXIT) {
            elevatorTimer = ELEVATOR_TRAVEL_TIME;
            studentInElevator = student;
            elevatorEntry = entryPoint;
            System.out.println("ElevatorSystem: " + student.getName() + " entered elevator.");

            SensorHelper.trigger(
                    "elevator_enter",
                    student.getName() + " entered the Elevator ("
                            + floorFromLocation(entryPoint)
                            + ")",
                    Notification.Type.ELEVATOR,
                    currentFrame
            );
            // TODO AudioManager.play("elevator_enter");
            return true;
        }

        System.out.println("ElevatorSystem: Invalid entry attempt from " + entryPoint);
        return false;
    }

    /**
     * Handles the logic for when a student finishes their transit through the elevator.
     * Triggers the exit audio and clears the student from the system.
     */
    private void studentExitElevator() {
        if (studentInElevator == null) {
            return;
        }

        System.out.println("ElevatorSystem: "
                + studentInElevator.getName()
                + " arrived at destination!");
        // TODO AudioManager.play("elevator_exit");

        // Logic to determine destination based on entry
        if (elevatorEntry == Location.FLOOR1_ELEVATOR) {
            studentInElevator.setLocation(Location.FLOOR3_ELEVATOR_EXIT);
        } else {
            studentInElevator.setLocation(Location.FLOOR1_ELEVATOR);
        }

        SensorHelper.trigger(
                "elevator_exit",
                studentInElevator.getName()
                        + " exited the Elevator ("
                        + floorFromLocation(studentInElevator.getCurrentLocation())
                        + ")",
            Notification.Type.ELEVATOR,
                currentFrame
        );



        studentInElevator = null;
        elevatorEntry = null;
        elevatorTimer = 0;
    }


    /**
     * Emergency stops the vent preventing entry and ejecting any students
     *
     * @return true if the elevator was successfully stopped; false if the
     *      elevator is already sealed or the system is on cooldown.
     */
    public boolean emergencyStop() {
        if (emergencyStopCooldown > 0 || isEmergencyStopped) {
            return false;
        }

        isEmergencyStopped = true;
        emergencyTimer = EMERGENCY_STOP_DURATION;
        emergencyStopCooldown = EMERGENCY_STOP_COOLDOWN;

        //TODO AudioManager.play("Elevator_STOP);
        System.out.println("ElevatorSystem: STOPPED (10s)");

        SensorHelper.trigger(
            "elevator_stop",
            "Elevator Emergency Stop engaged (10s)",
            Notification.Type.ELEVATOR,
            currentFrame
        );

        if (studentInElevator != null) {
            ejectStudent();
        }

        return true;
    }


    /**
     * Forcefully ejects a student from the elevator if the seal is activated.
     */
    private void ejectStudent() {
        if (studentInElevator == null) {
            return;
        }

        System.out.println("ElevatorSystem: EJECTED " + studentInElevator.getName());
        // TODO AudioManager.play("elevator_eject");

        SensorHelper.trigger(
            "elevator_eject",
            "Elevator ejected "
                    + studentInElevator.getName()
                    + " ("
                    + floorFromLocation(elevatorEntry)
                    + ")",
            Notification.Type.ELEVATOR,
            currentFrame
        );

        studentInElevator.setLocation(elevatorEntry);

        studentInElevator = null;
        elevatorEntry = null;
        elevatorTimer = 0;
    }

    /**
     * Resets the emergency stop to clear all active times and student data.
     */
    void unEmergencyStop() {
        isEmergencyStopped = false;
        emergencyTimer = 0;
        // TODO AudioManager.play("unstop");
    }

    /**
     * Checks if the elevator is currently in a emergency state.
     *
     * @return true if sealed, false otherwise.
     */
    public boolean isEmergencyStopped() {
        return isEmergencyStopped;
    }

    /**
     * Checks if a student is currently inside the elevator.
     *
     * @return true if occupied, false otherwise.
     */
    public boolean hasStudent() {
        return studentInElevator != null;
    }

    /**
     * Retrieves the student currently inside the elevator.
     *
     * @return The Student object, or null if empty.
     */
    public Student getStudentInElevator() {
        return studentInElevator;
    }

    /**
     * Calculates the remaining travel time for the student currently in the elevator.
     *
     * @return Remaining time in seconds, or 0 if no student is inside.
     */
    public int getTravelTimeRemainingSeconds() {
        return studentInElevator == null ? 0 : elevatorTimer / 60;
    }


    /**
     * Returns the current power drain rate of the elevator system.
     *
     * @return The drain value when emergency-stopped, 0.0 otherwise.
     */
    public double getPowerDrain() {
        return isEmergencyStopped ? EMERGENCY_STOP_POWER_DRAIN : 0.0;
    }

    /**
     * Checks if the user is allowed to stop the elevator.
     *
     * @return true if stoppig is permitted.
     */
    public boolean canStop() {
        return emergencyStopCooldown == 0 && !isEmergencyStopped;
    }

    /**
     * Gets the remaining time on the seal cooldown.
     *
     * @return Cooldown remaining in seconds.
     */
    public int getSealCooldownSeconds() {
        return emergencyStopCooldown / 60;
    }

    /**
     * Resets the system to its initial state, useful for level transitions or game overs.
     */
    public void reset() {
        isEmergencyStopped = false;
        studentInElevator = null;
        elevatorEntry = null;
        elevatorTimer = 0;
        emergencyTimer = 0;
        emergencyStopCooldown = 0;
        currentFrame = 0;
    }
}