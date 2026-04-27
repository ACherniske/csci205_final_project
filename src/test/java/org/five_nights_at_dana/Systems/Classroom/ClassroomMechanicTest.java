package org.five_nights_at_dana.Systems.Classroom;

import org.five_nights_at_dana.AI.Personality;
import org.five_nights_at_dana.AI.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClassroomMechanicTest {

    private ClassroomMechanic mechanic;
    private Student runner;

    @BeforeEach
    public void setUp() {
        mechanic = new ClassroomMechanic();
        runner = new Student("Runner", "Why did I fail?", Personality.RUNNER);
        mechanic.setRunner(runner);
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
        assertEquals(0.0, mechanic.getActivityLevel(), "Activity should not increase before cooldown.");
    }

    @Test
    public void testActivityIncreasesAfterCooldown() {
        for (int i = 0; i < 200; i++) {
            mechanic.update();
        }
        assertTrue(mechanic.getActivityLevel() > 0, "Activity should have increased after cooldown.");
    }

    @Test
    public void testResetActivity() {
        for (int i = 0; i < 300; i++) {
            mechanic.update();
        }
        assertTrue(mechanic.getActivityLevel() > 0);

        mechanic.resetActivity();
        assertEquals(0.0, mechanic.getActivityLevel(), "Activity should reset to 0.");
    }

    @Test
    public void testTriggerChargeAndSprint() {
        // Increase loop count to 2300 to be safe
        for (int i = 0; i < 2300; i++) {
            mechanic.update();
            // Optional: Call student.update() here IF the student needs to be
            // "ready" to charge (e.g., if you have state dependencies)
        }

        // Use a more descriptive error message
        assertTrue(mechanic.isEventTriggered(),
                "ClassroomMechanic should show event triggered after " + 2300 + " frames.");

        assertTrue(runner.isCharging(), "Student should be in charging state.");
        assertFalse(runner.isSprinting(), "Student should not be sprinting yet.");

        // 3. Fast forward through the charge phase
        fastForward(runner, 120);

        // 4. Verify student is now sprinting
        assertTrue(runner.isSprinting(), "Student should be in sprinting state after charging.");
    }

    @Test
    public void testCannotResetWhileEventTriggered() {
        // Change to 2300 to ensure the trigger actually happens
        for (int i = 0; i < 2300; i++) {
            mechanic.update();
        }

        // NOW resetActivity() should hit the 'if (eventTriggered)' block
        mechanic.resetActivity();

        assertTrue(mechanic.isEventTriggered(), "Reset should have failed because event was already triggered.");
    }

    @Test
    public void testActivityPercentage() {
        assertEquals(0.0, mechanic.getActivityPercentage());

        // Manually set activity (or run enough frames to reach 50)
        // For 50 activity, we need (50 / 0.05) + 180 = 1180 frames
        for (int i = 0; i < 1180; i++) mechanic.update();

        assertEquals(0.5, mechanic.getActivityPercentage(), 0.001);
    }

    @Test
    public void testMultipleResets() {
        for (int i = 0; i < 300; i++) mechanic.update();
        mechanic.resetActivity();
        mechanic.resetActivity(); // Second reset

        assertEquals(0.0, mechanic.getActivityLevel());
        assertEquals(0, mechanic.getActivityPercentage());
    }

    @Test
    public void testFullSystemReset() {
        // Run into a triggered state
        for (int i = 0; i < 2300; i++) mechanic.update();

        mechanic.reset();

        assertFalse(mechanic.isEventTriggered());
        assertEquals(0.0, mechanic.getActivityLevel());
        // Since runner is private, you could check if resetActivity works
        // to infer that the runner is null (if it throws or fails)
    }
}
