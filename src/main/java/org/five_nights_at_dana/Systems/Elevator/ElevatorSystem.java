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
import java.util.ArrayList;
import java.util.List;

/**
 * Controls elevator behavior.
 */
public class ElevatorSystem {

    private ElevatorState currentElevatorState = ElevatorState.FIRST_FLOOR;
    private ElevatorState previousElevatorState = ElevatorState.FIRST_FLOOR;

    private static final int ELEVATOR_MOVING = 600;
    private static final int EMERGENCY_STOPPING = 600;
    private static final int EMERGENCY_STOP_COOLDOWN = 720;
    private static final double EMERGENCY_STOP_POWER = 0.08;

    private int emergencyStopCooldown;
    private int emergencyTimer;
    private int elevatorMoving;

    private boolean isEmergencyStopped;
    private Student studentInElevator;
    private Location elevatorEntry;


    /**
     * Constructor
     */
    public ElevatorSystem() { reset(); }

    /**
     * Updates elevator logic.
     */
    public void update() {
        if (isEmergencyStopped) {
            emergencyTimer --;
            if (emergencyTimer <= 0) {
                unEmergencyStop();
            }
        }
        if (emergencyStopCooldown > 0) {
            emergencyStopCooldown --;
        }

        if (!isEmergencyStopped && studentInElevator != null) {
            elevatorMoving --;
        }

        if (elevatorMoving == 180) {
            System.out.println("ElevatorSystem: Student arriving in 3s");
            // TODO AudioManager.play("elevator_open");
        }
        if (elevatorMoving <= 0) {
            studentExitElevator();
        }
    }


    /**
     * Attempts to register a student in the elevator system
     *
     * @param student The student object entering the elevator.
     * @param entryPoint The specific location the student is using
     * @return true if teh student succesfully entered, false if the elevator is stopped,
     * already occupied, or entry point is invalid.
     */
    public boolean studentEnterElevator(Student student, Location entryPoint) {
        if (isEmergencyStopped || studentInElevator != null) {
            return false;
        }

        switch (entryPoint) {
            case FLOOR1_ELEVATOR:
                elevatorMoving = ELEVATOR_MOVING;
                previousElevatorState = ElevatorState.FIRST_FLOOR;
                System.out.println("ElevatorSystem: " + student.getName() + " entered Elevator On Floor 1");
                break;
            case FLOOR3_ELEVATOR_EXIT:
                elevatorMoving = ELEVATOR_MOVING;
                previousElevatorState = ElevatorState.THIRD_FLOOR;
                System.out.println("ElevatorSystem: " + student.getName() + " entered Elevator On Floor 3");
                break;
            default:
                System.out.println("VentSystem: Invalid entry attempt from " + entryPoint);
                return false;
        }

        studentInElevator = student;
        elevatorEntry = entryPoint;

        //TODO AudioManager.play("Elevator_enter")
        return true;

    }

    /**
     * Handles the logic for when a student finishes their transit through the elevator.
     * Triggers the exit audio and clears the student from the system.
     */
    private void studentExitElevator() {
        if (studentInElevator == null) return;

        System.out.println("VentSystem: " + studentInElevator.getName() + " exited at office!");
        // TODO AudioManager.play("elevator_exit");

        if (previousElevatorState == ElevatorState.FIRST_FLOOR) {
            studentInElevator.setLocation(Location.FLOOR3_ELEVATOR_EXIT);
        } else {
            studentInElevator.setLocation(Location.FLOOR1_ELEVATOR);
        }



        studentInElevator = null;
        elevatorEntry = null;
        elevatorMoving = 0;
    }


    /**
     * Emergency stops the vent preventing entry and ejecting any students
     *
     * @return true if the elevator was successfully stopped; false if the
     * elevator is already sealed or the system is on cooldown.
     */
    public boolean emergencyStop() {
        if (emergencyStopCooldown > 0 || isEmergencyStopped) {
            return false;
        }

        isEmergencyStopped = true;
        emergencyTimer = EMERGENCY_STOPPING;
        emergencyStopCooldown = EMERGENCY_STOP_COOLDOWN;

        //TODO AudioManager.play("Elevator_STOP);
        System.out.println("ElevatorSystem: STOPPED (10s)");

        if (studentInElevator != null) {
            ejectStudent();
        }

        return true;
    }


    /**
     * Forcefully ejects a student from the elevator if the seal is activated.
     */
    public void ejectStudent() {
        if (studentInElevator == null) return;

        System.out.println("ElevatorSystem: EJECTED");
        //TODO.AudioManager.play("elevator_eject");

        //TODO studentInElevator.setLocation(elevatorEntryPoint);

        studentInElevator = null;
        elevatorMoving = 0;
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
        return studentInElevator == null ? 0 : elevatorMoving / 60;
    }


    /**
     * Checks if the user is allowed to stop the elevator.
     *
     * @return true if stoppig is permitted.
     */
    public boolean canStop() { return emergencyStopCooldown == 0 && !isEmergencyStopped;}

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
        elevatorMoving = 0;
        emergencyTimer = 0;
        emergencyStopCooldown = 0;
    }
}