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

import org.five_nights_at_dana.AI.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * Controls elevator behavior.
 */
public class ElevatorSystem {

    private ElevatorState currentElevatorState = ElevatorState.FIRST_FLOOR;
    private ElevatorState previousElevatorState = ElevatorState.FIRST_FLOOR;
    List<Student> elevatorList = new ArrayList<>();



    /**
     * Updates elevator logic.
     */
    public void update() {
        //if the elevator is empty and on the first or third floor open the doors
        if (currentElevatorState == ElevatorState.FIRST_FLOOR ||
                currentElevatorState == ElevatorState.THIRD_FLOOR) {
            currentElevatorState = ElevatorState.DOORS_OPENING;
            //if the elevator it has a student in it and it is opening the door then
            //it will start moving up or down depending on where it was
        } else if (!elevatorList.isEmpty() && currentElevatorState == ElevatorState.DOORS_OPENING) {
            if (previousElevatorState == ElevatorState.FIRST_FLOOR) {
                currentElevatorState = ElevatorState.MOVING_UP;
            } else {
                currentElevatorState = ElevatorState.MOVING_DOWN;
            }
            //if the elevator is moving up then it will stop at third floor
        } else if (currentElevatorState == ElevatorState.MOVING_UP) {
            currentElevatorState = ElevatorState.THIRD_FLOOR;
            previousElevatorState = ElevatorState.MOVING_UP;
            //if elevator is moving down it will stop on the first floor
        } else if (currentElevatorState == ElevatorState.MOVING_DOWN) {
            currentElevatorState = ElevatorState.FIRST_FLOOR;
            previousElevatorState = ElevatorState.MOVING_DOWN;
        } else if (currentElevatorState == ElevatorState.STOPPED_EMERGENCY) {
            reset();
        }
    }

    /**
     * Activates emergency stop.
     * @return success
     */
    public boolean activateEmergencyStop() {
        if (currentElevatorState.canTransitionTo(ElevatorState.STOPPED_EMERGENCY)) {
            currentElevatorState = ElevatorState.STOPPED_EMERGENCY;
            return true;
        } else {
            return false;
        }


    }


    /**
     * @return power drain
     */
    public double getPowerDrain() {
        // TODO calculate drain
        return 0;
    }

    /** Resets system. */
    public void reset() {
        currentElevatorState = ElevatorState.DOORS_OPENING;
        previousElevatorState = ElevatorState.FIRST_FLOOR;
    }

    /**
     * This method is used for the testing file to add a student into
     * the elevatorList
     *
     */
    public void addStudent(Student student) {
        if (canStudentEnter()) {
            elevatorList.add(student);
        }

    }

    /**
     * This method will return true or false depending on if the
     * elevator list is empty or not
     * @return a boolean representing if a student is in the elevator or not
     */
    public boolean isStudentInElevator() {
        return !elevatorList.isEmpty();
    }

    /**
     * This method will return the elevator state
     * @return a ElevatorState that is the current elevator state
     */
    public ElevatorState getCurrentElevatorState() {
        return currentElevatorState;

    }

    /**
     * This method will get the previous elevator state
     * @return a elevator state representing the previous elevator state
     */
    public ElevatorState getPreviousElevatorState() {
        return previousElevatorState;

    }

    /**
     * This method will remove the current student in the elevatorList
     */
    public void removeStudent() {
        if (canStudentLeave()) {
            elevatorList.clear();
        }

    }

    /**
     * This method will tell if a student can enter the elevator
     * @return a boolean representing if a student can enter the elevator
     */
    public boolean canStudentEnter() {
        if (currentElevatorState == ElevatorState.DOORS_OPENING && elevatorList.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * This method will tell if a student can leave the elevator
     * @return a boolean representing if a student can leave or not
     */
    public boolean canStudentLeave() {
        if (currentElevatorState == ElevatorState.DOORS_OPENING && !elevatorList.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }


}