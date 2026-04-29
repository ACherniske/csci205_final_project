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

import org.five_nights_at_dana.AI.Location;import org.five_nights_at_dana.AI.Personality;
import org.five_nights_at_dana.AI.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * This will test the functionality of the elevator system
 */
public class ElevatorTest {

    private ElevatorSystem elevator;
    private Student student1;

    /**
     * This will set up two students and the elevator for testing
     */
    @BeforeEach
    void setUp() {
        student1 = new Student("Test Student", "Test", Personality.EAGER);
        elevator = new ElevatorSystem(); // adjust constructor if needed
    }

    /**
     * This will test a student enters the elevator
     */
    @Test
    public void testStudentEntersElevator() {
        boolean success = elevator.studentEnterElevator(student1, Location.FLOOR1_ELEVATOR);
        assertTrue(success, "Student should enter Elevator");
        assertTrue(elevator.hasStudent());
        assertEquals(student1, elevator.getStudentInElevator());
    }

    /**
     * This will test that the cooldown works properly and cannot be immediately
     * restopped
     */
    @Test
    public void testEmergencyCooldownPreventsImmediateRestop() {
        elevator.emergencyStop();
        elevator.reset();
        elevator.emergencyStop();
        assertTrue(elevator.isEmergencyStopped());
        elevator.unEmergencyStop();

        assertFalse(elevator.canStop(), "Should not be able to seal during cooldown");

    }

    /**
     * This will test the transit time for the student in the elevator
     */
    @Test
    public void testStudentTransitTime() {
        elevator.studentEnterElevator(student1, Location.FLOOR1_ELEVATOR);

        // Fast-forward 5 seconds
        for (int i = 0; i < 300; i++) elevator.update();

        assertEquals(5, elevator.getTravelTimeRemainingSeconds(), "Travel time should decrease.");
    }

    /**
     * This will test the elevator has the student exit
     */
    @Test
    public void testElevatorTransitExits() {
        elevator.studentEnterElevator(student1, Location.FLOOR2_CLASSROOM);

        // Fast-forward full transit
        for (int i = 0; i < 600; i++) elevator.update();

        assertFalse(elevator.hasStudent(), "Student should have exited.");
    }

    /**
     * This will test that two students cannot enter the elevator
     */
    @Test
    public void testCannotDoubleEnterElevator() {
        Student student2 = new Student("Second Student", "Test", Personality.RUNNER);
        elevator.studentEnterElevator(student1, Location.FLOOR1_ELEVATOR);

        boolean success = elevator.studentEnterElevator(student2, Location.FLOOR1_ELEVATOR);
        assertFalse(success, "Second student should not be able to enter an occupied Elevator.");
    }

    /**
     * This will test on invalid entry points
     */
    @Test
    public void testInvalidEntryPoint() {
        boolean success = elevator.studentEnterElevator(student1, Location.FLOOR3_COMPUTER_LAB);
        assertFalse(success, "Entering from an invalid location should return false.");
        assertFalse(elevator.hasStudent());
    }

    /**
     * This will test that the emergency stop functions correctly
     */
    @Test
    public void testEmergencyStopWhenEmpty() {
        boolean success = elevator.emergencyStop();
        assertTrue(success, "Should be able to stop an empty elevator.");
        assertTrue(elevator.isEmergencyStopped());
        assertFalse(elevator.hasStudent());
    }

    /**
     * This will test that once a student enters the first floor they are
     * taken to the third floor and exits.
     */
    @Test
    public void testStudentArrivesAtThirdFloorExit() {

        // Student enters elevator from first floor
        boolean success = elevator.studentEnterElevator(
                student1,
                Location.FLOOR1_ELEVATOR
        );

        assertTrue(success, "Student should successfully enter elevator.");

        // Simulate full elevator travel time
        for (int i = 0; i < 600; i++) {
            elevator.update();
        }

        // Student should no longer be in elevator
        assertFalse(elevator.hasStudent(),
                "Student should have exited the elevator.");

        // Verify student arrived at third floor exit
        assertEquals(
                Location.FLOOR3_ELEVATOR_EXIT,
                student1.getCurrentLocation(),
                "Student should arrive at the third floor elevator exit."
        );
    }





}
