/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Name: Mason Barlow
 * Date: 4/23/26
 * Time: 11:38 AM
 *
 * Project: csci205_final_project
 * Package: java.Systems.Elevator
 * Class: ElevatorTest
 *
 * Description: This file will work to test the functionality of the elevator.
 *
 * ****************************************
 */

package org.five_nights_at_dana.Systems.Elevator;

import static org.junit.jupiter.api.Assertions.*;

import org.five_nights_at_dana.AI.Personality;
import org.five_nights_at_dana.AI.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * This will test the functionality of the elevator system
 */
public class ElevatorTest {

    ElevatorSystem elevator;
    Student student1;
    Student student2;

    /**
     * This will set up two students and the elevator for testing
     */
    @BeforeEach
    void setUp() {
        student1 = new Student("Test", "question", Personality.EAGER);
        student2 = new Student("Test2", "question", Personality.EAGER);
        elevator = new ElevatorSystem(); // adjust constructor if needed
    }


    /**
     * This test will work to test that the first update method that is called
     * on the elevator will cause the state to change to doors opening, which is
     * the next expected state.
     */
    @Test
    void elevatorOpensDoorsFromFirstUpdateCall() {
        elevator.update();
        assertSame(ElevatorState.DOORS_OPENING, elevator.getCurrentElevatorState());
    }


    /**
     * This test will make sure that a student is not in the elevator after
     * the first update and then the student is properly added when it is allowed
     *
     */
    @Test
    void aStudentCanEnterTheElevator() {
        elevator.update();
        assertFalse(elevator.isStudentInElevator());
        assertTrue(elevator.canStudentEnter());
        elevator.addStudent(student1);
        assertTrue(elevator.isStudentInElevator());

    }

    /**
     * This test will work to test that once the doors have opened a student
     * can enter, then once the system is updated again the elevator state
     * will change to moving up
     */
    @Test
    void elevatorStartsMovingUpAfterStudentEnters() {
        elevator.update(); //updating to doors opening
        elevator.addStudent(student1); //adding student into the elevator
        elevator.update();
        assertSame(ElevatorState.MOVING_UP, elevator.getCurrentElevatorState());
    }

    /**
     * This test will make sure that the elevator does not move up when a student
     * has not entered the elevator.
     */
    @Test
    void elevatorDoesNotStartMovingWhenAStudentDoesNotEnter() {
        elevator.update();
        elevator.update();
        assertNotSame(ElevatorState.MOVING_UP, elevator.getCurrentElevatorState());
        assertSame(ElevatorState.DOORS_OPENING, elevator.getCurrentElevatorState());
    }


    /**
     * This test will test that if the elevator is moving up it will reach the
     * third floor on the next update method call
     */
    @Test
    void elevatorGoesToThirdFloorAfterMovingUp() {
        elevator.update();
        elevator.addStudent(student1);
        elevator.update();
        elevator.update();
        assertSame(ElevatorState.THIRD_FLOOR, elevator.getCurrentElevatorState());
    }

    /**
     * This test will check to see that once the elevator has reached
     * the third floor the doors will open
     */
    @Test
    void elevatorOpensTheDoorsAfterReachingThirdFloor() {
        elevator.update();
        elevator.addStudent(student1);
        elevator.update();
        elevator.update();
        elevator.update();
        assertSame(ElevatorState.DOORS_OPENING, elevator.getCurrentElevatorState());
    }

    /**
     * This test will test that a student can leave the elevator once it has
     * reached the third floor and has opened the doors
     */
    @Test
    void studentIsInElevatorAndCanLeaveOnceTheDoorsOpen() {
        elevator.update();
        elevator.addStudent(student1);
        elevator.update();
        elevator.update();
        elevator.update();
        assertTrue(elevator.isStudentInElevator());
        elevator.removeStudent();
        assertFalse(elevator.isStudentInElevator());
    }


    /**
     * This method will check to make sure that you can add another student
     * once the elevator has reached the third floor and has opened its doors.
     */
    @Test
    void studentCanReEnterTheElevator() {
        elevator.update();
        elevator.addStudent(student1);
        elevator.update();
        elevator.update();
        elevator.update();
        assertTrue(elevator.canStudentLeave());
        elevator.removeStudent();
        assertTrue(elevator.canStudentEnter());
        elevator.addStudent(student2);
        assertTrue(elevator.isStudentInElevator());
    }

    /**
     * This method will test to make sure that after the elevator
     * has reached the third floor, left the student, and taken a new student
     * that it will then being moving down
     */
    @Test
    void elevatorShouldBeingToMoveDownOnceStudentHasEntered() {
        elevator.update();
        elevator.addStudent(student1);
        elevator.update();
        elevator.update();
        elevator.update();
        elevator.removeStudent();
        elevator.addStudent(student2);
        elevator.update();
        assertSame(ElevatorState.MOVING_DOWN, elevator.getCurrentElevatorState());
    }

    /**
     * This test will test that once the elevator has started moving down it will
     * properly stop at the first floor
     */
    @Test
    void elevatorShouldStopAtTheFirstFloorAfterMovingDown() {
        elevator.update();
        elevator.addStudent(student1);
        elevator.update();
        elevator.update();
        elevator.update();
        elevator.removeStudent();
        elevator.addStudent(student2);
        elevator.update();
        elevator.update();
        assertSame(ElevatorState.FIRST_FLOOR, elevator.getCurrentElevatorState());
    }


    /**
     * This test will test that the elevator will open after it
     * reaches the first floor.
     */
    @Test
    void elevatorShouldOpenDoorsWhenTheElevatorReachesTheFirstFloor() {
        elevator.update();
        elevator.addStudent(student1);
        elevator.update();
        elevator.update();
        elevator.update();
        elevator.removeStudent();
        elevator.addStudent(student2);
        elevator.update();
        elevator.update();
        elevator.update();
        assertSame(ElevatorState.DOORS_OPENING, elevator.getCurrentElevatorState());
    }

    /**
     * This method will test that students can leave once the doors open after
     * reaching the first floor from the third
     */
    @Test
    void elevatorShouldBeAbleToHaveAStudentLeaveAndEnter() {
        elevator.update();
        elevator.addStudent(student1);
        elevator.update();
        elevator.update();
        elevator.update();
        elevator.removeStudent();
        elevator.addStudent(student2);
        elevator.update();
        elevator.update();
        elevator.update();
        assertTrue(elevator.canStudentLeave());
        elevator.removeStudent();
        assertFalse(elevator.isStudentInElevator());
    }


    /**
     * This method that the emergency stop is only able to be called
     * when the elevator is moving up or down and when it is called
     * the state gets set to STOPPPED_EMERGENCY
     */
    @Test
    void whenEmergencyStoppedIsCalledTheSystemResets() {
        elevator.update();
        elevator.addStudent(student1);
        assertFalse(elevator.activateEmergencyStop());
        elevator.update();
        //elevator is Moving Up
        assertTrue(elevator.activateEmergencyStop());
        assertSame(ElevatorState.STOPPED_EMERGENCY, elevator.getCurrentElevatorState());
    }

    /**
     * This will test that after emergency is called the elevator will be on the
     * first floor with the doors open
     */
    @Test
    void afterEmergencyStoppedIsCalledTheElevatorIsReset() {
        elevator.update();
        elevator.addStudent(student1);
        elevator.update();
        elevator.activateEmergencyStop();
        elevator.update();
        assertSame(ElevatorState.DOORS_OPENING, elevator.getCurrentElevatorState());
    }

    /**
     * This test will test to make sure that emergency stop can be called
     * when the elevator is on the third floor.
     */
    @Test
    void emergencyStopCanBeCalledWhenElevatorIsOnThirdFloor() {
        elevator.update();
        elevator.addStudent(student1);
        elevator.update();
        elevator.update();
        assertSame(ElevatorState.THIRD_FLOOR, elevator.getCurrentElevatorState());
        assertTrue(elevator.activateEmergencyStop());
    }


    /**
     * This test will make sure that when the emergency stop is called from the
     * third floor it will properly reset the elevator to doors opening on the first
     * floor.
     */
    @Test
    void emergencyStopWillResetElevatorFromThirdFloor() {
        elevator.update();
        elevator.addStudent(student1);
        elevator.update();
        elevator.update();
        elevator.activateEmergencyStop();
        elevator.update();
        assertSame(ElevatorState.DOORS_OPENING, elevator.getCurrentElevatorState());
        assertSame(ElevatorState.FIRST_FLOOR, elevator.getPreviousElevatorState());


    }



}
