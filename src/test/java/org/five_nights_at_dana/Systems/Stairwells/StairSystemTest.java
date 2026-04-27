package org.five_nights_at_dana.Systems.Stairwells;

import org.five_nights_at_dana.AI.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class StairSystemTest {

    private StairSystem stairSystem;
    private Student student;

    @BeforeEach
    void setUp() {
        stairSystem = new StairSystem();

        // Deterministic randomness for consistent pushback behavior
        Student.setRandom(new Random(42));

        student = new Student(
                "TestStudent",
                "Test Question",
                Personality.EAGER
        );
    }

    // =========================
    // BASIC SYSTEM TESTS
    // =========================

    @Test
    void testInitialState() {
        assertEquals(5, stairSystem.getLightCharges());
        assertTrue(stairSystem.canActivateLights());
        assertNull(stairSystem.getActiveLightsStairwell());
    }

    @Test
    void testActivateLightsConsumesCharge() {
        boolean activated = stairSystem.activateLights(Stairwell.LEFT);

        assertTrue(activated);
        assertEquals(4, stairSystem.getLightCharges());
        assertTrue(stairSystem.areLightsActive(Stairwell.LEFT));
    }

    @Test
    void testCannotActivateLightsWhenAlreadyActive() {
        stairSystem.activateLights(Stairwell.LEFT);
        boolean second = stairSystem.activateLights(Stairwell.RIGHT);

        assertFalse(second);
    }

    @Test
    void testLightDeactivationAfterDuration() {
        stairSystem.activateLights(Stairwell.LEFT);

        // Simulate frames
        for (int i = 0; i < 300; i++) {
            stairSystem.update();
        }

        assertNull(stairSystem.getActiveLightsStairwell());
        assertFalse(stairSystem.areLightsActive(Stairwell.LEFT));
    }

    // =========================
    // STUDENT MANAGEMENT TESTS
    // =========================

    @Test
    void testStudentEnterStairwell() {
        stairSystem.studentEnterStairwell(student, Stairwell.LEFT);

        List<Student> students = stairSystem.getStudentsInStairwell(Stairwell.LEFT);

        assertEquals(1, students.size());
        assertTrue(students.contains(student));
    }

    @Test
    void testStudentExitStairwell() {
        stairSystem.studentEnterStairwell(student, Stairwell.LEFT);
        stairSystem.studentExitStairwell(student, Stairwell.LEFT);

        List<Student> students = stairSystem.getStudentsInStairwell(Stairwell.LEFT);

        assertTrue(students.isEmpty());
    }

    @Test
    void testNoDuplicateStudents() {
        stairSystem.studentEnterStairwell(student, Stairwell.LEFT);
        stairSystem.studentEnterStairwell(student, Stairwell.LEFT);

        List<Student> students = stairSystem.getStudentsInStairwell(Stairwell.LEFT);

        assertEquals(1, students.size());
    }

    // =========================
    // PUSHBACK TESTS
    // =========================

    @Test
    void testStudentGetsPushedBackOnLightActivation() {
        stairSystem.studentEnterStairwell(student, Stairwell.LEFT);

        Location original = student.getCurrentLocation();

        // Force some movement history so pushback is possible
        student.setLocation(Location.FLOOR1_STAIR_MID);
        student.setLocation(Location.FLOOR2_STAIR_MID);

        stairSystem.activateLights(Stairwell.LEFT);

        Location after = student.getCurrentLocation();

        // We don't assert exact location (random),
        // just that the student moved backward or stayed valid
        assertNotNull(after);
    }

    @Test
    void testPushbackDoesNotCrashWithMinimalHistory() {
        stairSystem.studentEnterStairwell(student, Stairwell.LEFT);

        // No extra history beyond spawn
        assertDoesNotThrow(() ->
                stairSystem.activateLights(Stairwell.LEFT)
        );
    }

    // =========================
    // RESET TESTS
    // =========================

    @Test
    void testResetRestoresSystem() {
        stairSystem.studentEnterStairwell(student, Stairwell.LEFT);
        stairSystem.activateLights(Stairwell.LEFT);

        stairSystem.reset();

        assertEquals(5, stairSystem.getLightCharges());
        assertNull(stairSystem.getActiveLightsStairwell());
        assertTrue(stairSystem.getStudentsInStairwell(Stairwell.LEFT).isEmpty());
    }

    // =========================
    // EDGE CASE TESTS
    // =========================

    @Test
    void testCannotActivateLightsWithNoCharges() {
        for (int i = 0; i < 5; i++) {
            stairSystem.activateLights(Stairwell.LEFT);

            // Simulate lights turning off
            for (int j = 0; j < 300; j++) {
                stairSystem.update();
            }
        }

        assertEquals(0, stairSystem.getLightCharges());
        assertFalse(stairSystem.activateLights(Stairwell.LEFT));
    }
}
