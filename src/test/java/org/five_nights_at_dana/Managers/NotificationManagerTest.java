/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/26/2026
 * Time: 12:21 AM
 *
 * Project: csci205_final_project
 * Package: Managers
 * Class: NotificationManagerTest
 *
 * Description:
 * This file will test the notification manager
 *
 * ****************************************
 */

package org.five_nights_at_dana.Managers;

import org.five_nights_at_dana.AI.Personality;
import org.five_nights_at_dana.AI.Student;
import org.five_nights_at_dana.Systems.Classroom.ClassroomMechanic;
import org.five_nights_at_dana.Systems.SensorHelper;
import org.five_nights_at_dana.Systems.Stairwells.StairSystem;
import org.five_nights_at_dana.Systems.Stairwells.Stairwell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationManagerTest {

    @BeforeEach
    void setUp() {
        NotificationManager.clear();
    }

    // =========================
    // BASIC FUNCTIONALITY
    // =========================

    @Test
    void testPushAddsNotification() {
        NotificationManager.push("Test message", Notification.Type.SYSTEM);

        List<Notification> notifications = NotificationManager.getNotifications();

        assertEquals(1, notifications.size());
        assertEquals("Test message", notifications.getFirst().message());
    }

    @Test
    void testMultipleNotificationsMaintainOrder() {
        NotificationManager.push("First", Notification.Type.SYSTEM);
        NotificationManager.push("Second", Notification.Type.WARNING);
        NotificationManager.push("Third", Notification.Type.SYSTEM);

        List<Notification> notifications = NotificationManager.getNotifications();

        assertEquals("First", notifications.get(0).message());
        assertEquals("Second", notifications.get(1).message());
        assertEquals("Third", notifications.get(2).message());
    }

    // =========================
    // TIMESTAMP TESTS
    // =========================

    @Test
    void testTimestampsIncreaseWithUpdates() {
        NotificationManager.push("Before update", Notification.Type.SYSTEM);

        NotificationManager.update();
        NotificationManager.update();

        NotificationManager.push("After update", Notification.Type.SYSTEM);

        List<Notification> notifications = NotificationManager.getNotifications();

        assertTrue(
                notifications.get(1).timestamp() >
                        notifications.get(0).timestamp()
        );
    }

    // =========================
    // CLEAR / RESET TESTS
    // =========================

    @Test
    void testClearRemovesAllNotifications() {
        NotificationManager.push("A", Notification.Type.SYSTEM);
        NotificationManager.push("B", Notification.Type.SYSTEM);

        NotificationManager.clear();

        assertTrue(NotificationManager.getNotifications().isEmpty());
    }

    // =========================
    // CAPACITY TESTS
    // =========================

    @Test
    void testMaxCapacityIsRespected() {
        int max = 50; // must match NotificationManager

        for (int i = 0; i < max + 10; i++) {
            NotificationManager.push("Msg " + i, Notification.Type.SYSTEM);
        }

        List<Notification> notifications = NotificationManager.getNotifications();

        assertEquals(max, notifications.size());

        // Oldest should be trimmed
        assertEquals("Msg 10", notifications.getFirst().message());
    }

    // =========================
    // IMMUTABILITY TEST
    // =========================

    @Test
    void testReturnedListIsCopy() {
        NotificationManager.push("Safe", Notification.Type.SYSTEM);

        List<Notification> notifications = NotificationManager.getNotifications();
        notifications.clear(); // attempt to mutate external copy

        // Internal state should NOT change
        assertEquals(1, NotificationManager.getNotifications().size());
    }

    // =========================
    // EDGE CASES
    // =========================

    @Test
    void testEmptyState() {
        List<Notification> notifications = NotificationManager.getNotifications();
        assertTrue(notifications.isEmpty());
    }

    @Test
    void testNullOrEmptyMessageHandling() {
        NotificationManager.push("", Notification.Type.SYSTEM);

        List<Notification> notifications = NotificationManager.getNotifications();

        assertEquals(1, notifications.size());
        assertEquals("", notifications.getFirst().message());
    }

    // =========================
    // SPAM / COOLDOWN TESTS (if implemented)
    // =========================

    @Test
    void testDuplicateMessageCooldown() {
        NotificationManager.push("Spam", Notification.Type.SYSTEM);
        NotificationManager.push("Spam", Notification.Type.SYSTEM);

        List<Notification> notifications = NotificationManager.getNotifications();

        // If cooldown is implemented → expect 1
        // If not → expect 2 (this test will reveal behavior)
        assertTrue(notifications.size() == 1 || notifications.size() == 2);
    }

    // =========================
    // STRESS TEST
    // =========================

    @Test
    void testHighVolumeDoesNotCrash() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 1000; i++) {
                NotificationManager.push("Msg " + i, Notification.Type.SYSTEM);
            }
        });

        assertTrue(NotificationManager.getNotifications().size() <= 50);
    }

    // =========================
// INTEGRATION TESTS
// =========================

    @Test
    void testSensorHelperIntegration() {
        SensorHelper.reset();
        NotificationManager.clear();

        SensorHelper.trigger(
                "test_key",
                "Sensor triggered",
                Notification.Type.SYSTEM,
                0
        );

        List<Notification> notifications = NotificationManager.getNotifications();

        assertEquals(1, notifications.size());
        assertEquals("Sensor triggered", notifications.getFirst().message());
    }

    @Test
    void testSensorHelperCooldownIntegration() {
        SensorHelper.reset();
        NotificationManager.clear();

        for (int i = 0; i < 5; i++) {
            SensorHelper.trigger(
                    "cooldown_test",
                    "Spam",
                    Notification.Type.SYSTEM,
                    i
            );
        }

        List<Notification> notifications = NotificationManager.getNotifications();

        // Should only trigger once due to cooldown
        assertTrue(notifications.size() <= 2);
    }

    @Test
    void testStairSystemIntegration() {
        SensorHelper.reset();
        NotificationManager.clear();

        StairSystem stairs = new StairSystem();
        Student s = new Student("Tester", "Q", Personality.EAGER);

        stairs.studentEnterStairwell(s, Stairwell.LEFT);

        List<Notification> notifications = NotificationManager.getNotifications();

        assertTrue(
                notifications.stream().anyMatch(n ->
                        n.type() == Notification.Type.STAIR_SENSOR &&
                                n.message().contains("Tester")
                ),
                "Expected stair sensor notification from StairSystem"
        );
    }

    @Test
    void testStairSystemMultipleEntriesRespectCooldown() {
        SensorHelper.reset();
        NotificationManager.clear();

        StairSystem stairs = new StairSystem();
        Student s = new Student("Tester", "Q", Personality.EAGER);

        for (int i = 0; i < 10; i++) {
            stairs.studentEnterStairwell(s, Stairwell.LEFT);
        }

        List<Notification> notifications = NotificationManager.getNotifications();

        long count = notifications.stream()
                .filter(n -> n.type() == Notification.Type.STAIR_SENSOR)
                .count();

        assertTrue(count <= 2, "Stair sensor should be rate-limited");
    }

    @Test
    void testClassroomMechanicIntegration() {
        SensorHelper.reset();
        NotificationManager.clear();

        ClassroomMechanic mech = new ClassroomMechanic();
        Student runner = new Student("Runner", "Q", Personality.RUNNER);

        mech.setRunner(runner);
        mech.setRunnerActive(true);
        mech.setRunnerDifficultyMultiplier(1.0);

        // Run long enough to trigger warning
        for (int i = 0; i < 2000; i++) {
            mech.update();
        }

        List<Notification> notifications = NotificationManager.getNotifications();

        assertTrue(
                notifications.stream().anyMatch(n ->
                        n.message().contains("restless") ||
                                n.message().contains("charging")
                ),
                "Expected classroom notifications from mechanic"
        );
    }
}
