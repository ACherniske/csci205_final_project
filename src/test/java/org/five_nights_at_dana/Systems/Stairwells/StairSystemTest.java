package org.five_nights_at_dana.Systems.Stairwells;

import org.five_nights_at_dana.AI.*;
import org.five_nights_at_dana.Managers.Notification;
import org.five_nights_at_dana.Managers.NotificationManager;
import org.five_nights_at_dana.Systems.SensorHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StairSystemTest {

    private StairSystem stairSystem;
    private Student student;

    @BeforeEach
    void setUp() {
        stairSystem = new StairSystem();

        // Reset global systems (CRITICAL)
        NotificationManager.clear();
        SensorHelper.reset();

        // Deterministic randomness
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
        assertTrue(stairSystem.canActivateLights());
        assertNull(stairSystem.getActiveLightsStairwell());
        assertEquals(0.0, stairSystem.getPowerDrain(), 0.0001);
    }

    @Test
    void testActivateLightsActivatesAndDrainsPower() {
        boolean activated = stairSystem.activateLights(Stairwell.LEFT);

        assertTrue(activated);
        assertTrue(stairSystem.areLightsActive(Stairwell.LEFT));
        assertTrue(stairSystem.getPowerDrain() > 0.0);
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
    void testNoDuplicateStudents() {
        stairSystem.studentEnterStairwell(student, Stairwell.LEFT);
        stairSystem.studentEnterStairwell(student, Stairwell.LEFT);

        List<Student> students = stairSystem.getStudentsInStairwell(Stairwell.LEFT);

        assertEquals(1, students.size());
    }

    // =========================
    // 🔔 NOTIFICATION TESTS
    // =========================

    @Test
    void testStairSensorNotificationTriggered() {
        stairSystem.studentEnterStairwell(student, Stairwell.LEFT);

        List<Notification> notifs = NotificationManager.getNotifications();

        assertTrue(
                notifs.stream().anyMatch(n ->
                        n.type() == Notification.Type.STAIR_SENSOR &&
                                n.message().contains("TestStudent")
                ),
                "Expected stair sensor notification when student enters"
        );
    }

    @Test
    void testNotificationCooldownPreventsSpam() {
        // Spam enter same stairwell repeatedly
        for (int i = 0; i < 10; i++) {
            stairSystem.studentEnterStairwell(student, Stairwell.LEFT);
        }

        List<Notification> notifs = NotificationManager.getNotifications();

        long count = notifs.stream()
                .filter(n -> n.type() == Notification.Type.STAIR_SENSOR)
                .count();

        // Should be LOW due to cooldown
        assertTrue(count <= 2, "Sensor should be rate-limited");
    }

    @Test
    void testDifferentStairwellsProduceSeparateNotifications() {
        Student s1 = new Student("LeftGuy", "Q", Personality.EAGER);
        Student s2 = new Student("RightGuy", "Q", Personality.EAGER);

        stairSystem.studentEnterStairwell(s1, Stairwell.LEFT);

        // Advance frames to bypass cooldown
        for (int i = 0; i < 100; i++) stairSystem.update();

        stairSystem.studentEnterStairwell(s2, Stairwell.RIGHT);

        List<Notification> notifs = NotificationManager.getNotifications();

        assertTrue(
                notifs.stream().anyMatch(n -> n.message().contains("LeftGuy")),
                "Expected LEFT stair notification"
        );

        assertTrue(
                notifs.stream().anyMatch(n -> n.message().contains("RightGuy")),
                "Expected RIGHT stair notification"
        );
    }

    @Test
    void testNoNotificationWhenStudentAlreadyPresent() {
        stairSystem.studentEnterStairwell(student, Stairwell.LEFT);

        NotificationManager.clear();

        // Enter again (duplicate)
        stairSystem.studentEnterStairwell(student, Stairwell.LEFT);

        assertTrue(
                NotificationManager.getNotifications().isEmpty(),
                "Duplicate entry should not trigger notification"
        );
    }

    // =========================
    // PUSHBACK TESTS
    // =========================

    @Test
    void testStudentGetsPushedBackOnLightActivation() {
        stairSystem.studentEnterStairwell(student, Stairwell.LEFT);

        student.setLocation(Location.FLOOR1_STAIR_MID);
        student.setLocation(Location.FLOOR2_STAIR_MID);

        stairSystem.activateLights(Stairwell.LEFT);

        assertNotNull(student.getCurrentLocation());
    }

    @Test
    void testPushbackDoesNotCrashWithMinimalHistory() {
        stairSystem.studentEnterStairwell(student, Stairwell.LEFT);

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

        assertNull(stairSystem.getActiveLightsStairwell());
        assertTrue(stairSystem.getStudentsInStairwell(Stairwell.LEFT).isEmpty());
        assertEquals(0.0, stairSystem.getPowerDrain(), 0.0001);
    }

    @Test
    void testCanReactivateLightsAfterDuration() {
        assertTrue(stairSystem.activateLights(Stairwell.LEFT));
        assertFalse(stairSystem.activateLights(Stairwell.RIGHT), "Cannot activate while lights already active");

        for (int i = 0; i < 300; i++) {
            stairSystem.update();
        }

        assertTrue(stairSystem.activateLights(Stairwell.RIGHT), "Can activate again once lights turn off");
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

        assertEquals(20, stairSystem.getStudentsInStairwell(Stairwell.LEFT).size());
    }
}
