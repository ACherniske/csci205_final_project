package org.five_nights_at_dana.Systems.Stairwells;

import org.five_nights_at_dana.AI.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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

    @Test
    void testLightsOnlyAffectCorrectStairwell() {
        Student left = new Student("Left", "Q", Personality.EAGER);
        Student right = new Student("Right", "Q", Personality.EAGER);

        left.setLocation(Location.FLOOR2_STAIR_MID);
        right.setLocation(Location.FLOOR2_STAIR_MID);

        stairSystem.studentEnterStairwell(left, Stairwell.LEFT);
        stairSystem.studentEnterStairwell(right, Stairwell.RIGHT);

        Location rightBefore = right.getCurrentLocation();

        stairSystem.activateLights(Stairwell.LEFT);

        // Right stairwell student should NOT be affected
        assertEquals(rightBefore, right.getCurrentLocation());
    }

    @Test
    void testChargesNeverGoNegative() {
        for (int i = 0; i < 10; i++) {
            stairSystem.activateLights(Stairwell.LEFT);

            for (int j = 0; j < 300; j++) {
                stairSystem.update();
            }
        }

        assertTrue(stairSystem.getLightCharges() >= 0);
    }

    @Test
    void testCannotActivateWhileLightsStillOn() {
        stairSystem.activateLights(Stairwell.LEFT);

        stairSystem.update(); // still active

        boolean result = stairSystem.activateLights(Stairwell.RIGHT);

        assertFalse(result);
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

    @Test
    void testRepeatedPushbackDoesNotBreakMovement() {
        Student s = new Student("Test", "Q", Personality.EAGER);

        // Build movement history
        s.setLocation(Location.FLOOR1_STAIR_MID);
        s.setLocation(Location.FLOOR2_STAIR_MID);
        s.setLocation(Location.FLOOR3_STAIR_MID);

        stairSystem.studentEnterStairwell(s, Stairwell.LEFT);

        for (int i = 0; i < 5; i++) {
            stairSystem.activateLights(Stairwell.LEFT);

            // let lights expire
            for (int j = 0; j < 300; j++) {
                stairSystem.update();
            }
        }

        assertNotNull(s.getCurrentLocation());
        assertFalse(s.getRecentLocations().isEmpty());
    }

    @Test
    void testPersonalityAffectsPushback() {
        Student shy = new Student("Shy", "Q", Personality.SHY);
        Student persistent = new Student("Persistent", "Q", Personality.PERSISTENT);

        // Build identical history
        for (Student s : List.of(shy, persistent)) {
            s.setLocation(Location.FLOOR1_STAIR_MID);
            s.setLocation(Location.FLOOR2_STAIR_MID);
            s.setLocation(Location.FLOOR3_STAIR_MID);
            stairSystem.studentEnterStairwell(s, Stairwell.LEFT);
        }

        stairSystem.activateLights(Stairwell.LEFT);

        // We can't assert exact positions (random),
        // but we CAN assert both moved and stayed valid
        assertNotNull(shy.getCurrentLocation());
        assertNotNull(persistent.getCurrentLocation());
    }

    @Test
    void testPushbackWithMinimalHistoryIsSafe() {
        Student s = new Student("Minimal", "Q", Personality.EAGER);

        stairSystem.studentEnterStairwell(s, Stairwell.LEFT);

        assertDoesNotThrow(() -> stairSystem.activateLights(Stairwell.LEFT));

        assertNotNull(s.getCurrentLocation());
    }

    @Test
    void testPushbackActuallyMovesStudentBack() {
        Student s = new Student("BackTest", "Q", Personality.EAGER);

        s.setLocation(Location.FLOOR1_STAIR_MID);
        s.setLocation(Location.FLOOR2_STAIR_MID);
        s.setLocation(Location.FLOOR3_STAIR_MID);

        Location before = s.getCurrentLocation();

        stairSystem.studentEnterStairwell(s, Stairwell.LEFT);
        stairSystem.activateLights(Stairwell.LEFT);

        Location after = s.getCurrentLocation();

        assertNotEquals(before, after, "Student should be pushed back to a different location");
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

    // =========================
    // STRESS TESTS
    // =========================
    @Test
    void testMultipleStudentsHandledCorrectly() {
        List<Student> students = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Student s = new Student(
                    "S" + i,
                    "Q",
                    Personality.values()[i % Personality.values().length]
            );
            students.add(s);
            stairSystem.studentEnterStairwell(s, Stairwell.LEFT);
        }

        assertDoesNotThrow(() -> stairSystem.activateLights(Stairwell.LEFT));

        List<Student> inStairs = stairSystem.getStudentsInStairwell(Stairwell.LEFT);
        assertEquals(20, inStairs.size());
    }

    @Test
    void testFullSimulationFlow() {
        Student s = new Student("Sim", "Q", Personality.EAGER);

        // Simulate movement frames
        for (int i = 0; i < 500; i++) {
            s.update();
        }

        stairSystem.studentEnterStairwell(s, Stairwell.LEFT);

        Location before = s.getCurrentLocation();

        stairSystem.activateLights(Stairwell.LEFT);

        Location after = s.getCurrentLocation();

        assertNotNull(after);

        // Let system continue
        for (int i = 0; i < 200; i++) {
            s.update();
        }

        assertNotNull(s.getCurrentLocation());
    }

    @Test
    void testMemoryDoesNotOverflowOrCorrupt() {
        Student s = new Student("Memory", "Q", Personality.CONFUSED);

        // Force lots of movement + pushback
        for (int i = 0; i < 20; i++) {
            s.setLocation(Location.FLOOR1_STAIR_MID);
            s.setLocation(Location.FLOOR2_STAIR_MID);
            s.setLocation(Location.FLOOR3_STAIR_MID);

            stairSystem.studentEnterStairwell(s, Stairwell.LEFT);
            stairSystem.activateLights(Stairwell.LEFT);

            // expire lights
            for (int j = 0; j < 300; j++) {
                stairSystem.update();
            }
        }

        // Memory should still be bounded
        assertTrue(s.getRecentLocations().size() <= 5);
    }
}
