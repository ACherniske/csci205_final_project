package org.five_nights_at_dana.Systems.Classroom;

import org.five_nights_at_dana.AI.Personality;
import org.five_nights_at_dana.AI.Student;
import org.five_nights_at_dana.Managers.Notification;
import org.five_nights_at_dana.Managers.NotificationManager;
import org.five_nights_at_dana.Systems.SensorHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClassroomMechanicTest {

    private ClassroomMechanic mechanic;
    private Student runner;

    @BeforeEach
    public void setUp() {
        mechanic = new ClassroomMechanic();
        runner = new Student("Runner", "Why did I fail?", Personality.RUNNER);
        mechanic.setRunner(runner);
        mechanic.setRunnerActive(true);
        mechanic.setRunnerDifficultyMultiplier(1.0);

        // Reset global systems
        NotificationManager.clear();
        SensorHelper.reset();
    }

    private void fastForward(Student s, int ticks) {
        for (int i = 0; i < ticks; i++) {
            s.update();
        }
    }

    @Test
    public void testInitialState() {
        assertEquals(0.0, mechanic.getActivityLevel());
        assertFalse(mechanic.isEventTriggered());
    }

    @Test
    public void testActivityDoesNotIncreaseBeforeCooldown() {
        for (int i = 0; i < 100; i++) {
            mechanic.update();
        }
        assertEquals(0.0, mechanic.getActivityLevel());
    }

    @Test
    public void testActivityIncreasesAfterCooldown() {
        for (int i = 0; i < 200; i++) {
            mechanic.update();
        }
        assertTrue(mechanic.getActivityLevel() > 0);
    }

    @Test
    public void testResetActivity() {
        for (int i = 0; i < 300; i++) {
            mechanic.update();
        }

        mechanic.resetActivity();
        assertEquals(0.0, mechanic.getActivityLevel());
    }

    @Test
    public void testTriggerChargeAndSprint() {
        for (int i = 0; i < 2300; i++) {
            mechanic.update();
        }

        assertTrue(mechanic.isEventTriggered());
        assertTrue(runner.isCharging());
        assertFalse(runner.isSprinting());

        fastForward(runner, 120);

        assertTrue(runner.isSprinting());
    }

    @Test
    public void testCannotResetWhileEventTriggered() {
        for (int i = 0; i < 2300; i++) {
            mechanic.update();
        }

        mechanic.resetActivity();

        assertTrue(mechanic.isEventTriggered());
    }

    @Test
    public void testActivityPercentage() {
        assertEquals(0.0, mechanic.getActivityPercentage());

        for (int i = 0; i < 1180; i++) mechanic.update();

        assertEquals(0.5, mechanic.getActivityPercentage(), 0.01);
    }

    @Test
    public void testMultipleResets() {
        for (int i = 0; i < 300; i++) mechanic.update();

        mechanic.resetActivity();
        mechanic.resetActivity();

        assertEquals(0.0, mechanic.getActivityLevel());
        assertEquals(0.0, mechanic.getActivityPercentage());
    }

    @Test
    public void testFullSystemReset() {
        for (int i = 0; i < 2300; i++) mechanic.update();

        mechanic.reset();

        assertFalse(mechanic.isEventTriggered());
        assertEquals(0.0, mechanic.getActivityLevel());
    }

    // =========================
    // NEW: NOTIFICATION TESTS
    // =========================

    @Test
    public void testWarningNotificationTriggered() {
        while (mechanic.getActivityLevel() < 75) {
            mechanic.update();
        }

        List<Notification> notifs = NotificationManager.getNotifications();

        assertTrue(
                notifs.stream().anyMatch(n ->
                        n.getMessage().contains("restless")
                ),
                "Expected runner warning notification"
        );
    }

    @Test
    public void testChargeNotificationTriggered() {
        for (int i = 0; i < 2300; i++) {
            mechanic.update();
        }

        List<Notification> notifs = NotificationManager.getNotifications();

        assertTrue(
                notifs.stream().anyMatch(n ->
                        n.getMessage().contains("charging")
                ),
                "Expected runner charging notification"
        );
    }

    @Test
    public void testNotificationCooldownPreventsSpam() {
        for (int i = 0; i < 2000; i++) {
            mechanic.update();
        }

        List<Notification> notifs = NotificationManager.getNotifications();

        long warningCount = notifs.stream()
                .filter(n -> n.getMessage().contains("restless"))
                .count();

        // Should NOT spam dozens of warnings
        assertTrue(warningCount <= 3, "Warning notifications should be rate-limited");
    }

    @Test
    public void testNoNotificationsBeforeCooldown() {
        for (int i = 0; i < 100; i++) {
            mechanic.update();
        }

        assertTrue(NotificationManager.getNotifications().isEmpty());
    }

    @Test
    public void testNotificationsResetBetweenTests() {
        NotificationManager.push("Test", Notification.Type.SYSTEM);

        assertFalse(NotificationManager.getNotifications().isEmpty());

        NotificationManager.clear();

        assertTrue(NotificationManager.getNotifications().isEmpty());
    }
}
