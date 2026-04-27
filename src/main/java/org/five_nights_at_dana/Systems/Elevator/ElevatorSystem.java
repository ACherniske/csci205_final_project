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
    List<Student> elevatorList = new ArrayList<>();



    /**
     * Updates elevator logic.
     */
    public void update() {
        // TODO move elevator + handle states
        if (!elevatorList.isEmpty() && currentElevatorState == ElevatorState.FIRST_FLOOR) {
            currentElevatorState = ElevatorState.MOVING_UP;
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
        // TODO reset state
    }

    /**
     * This method is used for the testing file to add a student into
     * the elevatorList
     *
     */
    public void addStudent(Student student) {
        elevatorList.add(student);
    }

    /**
     * This method will return the elevator state
     * @return a ElevatorState that is the current elevator state
     */
    public ElevatorState getCurrentElevatorState() {
        return currentElevatorState;
    }
}