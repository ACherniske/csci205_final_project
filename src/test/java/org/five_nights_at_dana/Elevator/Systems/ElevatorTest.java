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

package org.five_nights_at_dana.Elevator.Systems;

import static org.junit.jupiter.api.Assertions.*;

import org.five_nights_at_dana.AI.Personalities.Eager;
import org.five_nights_at_dana.AI.Student;
import org.five_nights_at_dana.Systems.Elevator.ElevatorState;
import org.five_nights_at_dana.Systems.Elevator.ElevatorSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ElevatorTest {

    ElevatorSystem elevator;
    Student student1;

    @BeforeEach
    void setUp() {
        student1 = new Student("Test", new Eager(), 2);
        elevator = new ElevatorSystem(); // adjust constructor if needed
    }


    @Test
    void elevatorMovesUpWhenStudentEnters() {
        elevator.addStudent(student1);
        elevator.update();
        assertSame(ElevatorState.MOVING_UP, elevator.getCurrentElevatorState());
        assertSame(ElevatorState.MOVING_DOWN, elevator.getCurrentElevatorState());

    }
}
