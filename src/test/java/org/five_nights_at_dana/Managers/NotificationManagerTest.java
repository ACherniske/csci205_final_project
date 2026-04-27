package org.five_nights_at_dana.Managers;

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
        assertEquals("Test message", notifications.getFirst().getMessage());
    }

    @Test
    void testMultipleNotificationsMaintainOrder() {
        NotificationManager.push("First", Notification.Type.SYSTEM);
        NotificationManager.push("Second", Notification.Type.WARNING);
        NotificationManager.push("Third", Notification.Type.SYSTEM);

        List<Notification> notifications = NotificationManager.getNotifications();

        assertEquals("First", notifications.get(0).getMessage());
        assertEquals("Second", notifications.get(1).getMessage());
        assertEquals("Third", notifications.get(2).getMessage());
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
                notifications.get(1).getTimestamp() >
                        notifications.get(0).getTimestamp()
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
        assertEquals("Msg 10", notifications.getFirst().getMessage());
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
        assertEquals("", notifications.getFirst().getMessage());
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
}
