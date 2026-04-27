package org.five_nights_at_dana.Systems.Stairwells;

import org.five_nights_at_dana.AI.Personality;
import org.five_nights_at_dana.AI.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StairSystemTest {

    private StairSystem stairs;
    private Student testStudent;

    @BeforeEach
    public void setUp() {
        stairs = new StairSystem();
        testStudent = new Student("Test Student", "Why?", Personality.SHY);
    }

    @Test
    public void testInitialState() {
        assertEquals(5, stairs.getLightCharges(), "System should start with 5 charges.");
        assertTrue(stairs.canActivateLights(), "Should be able to activate lights initially.");
        assertFalse(stairs.areLightsActive(Stairwell.LEFT));
    }

    @Test
    public void testLightChargeDepletion() {
        for (int i = 0; i < 5; i++) {
            assertTrue(stairs.activateLights(Stairwell.LEFT));
            // Simulate light duration to reset state
            for (int j = 0; j < 301; j++) stairs.update();
        }

        assertFalse(stairs.canActivateLights(), "Should have 0 charges remaining.");
        assertFalse(stairs.activateLights(Stairwell.LEFT), "Should not activate with 0 charges.");
    }

    @Test
    public void testCannotActivateSimultaneously() {
        stairs.activateLights(Stairwell.LEFT);
        assertFalse(stairs.activateLights(Stairwell.MIDDLE),
                "Should not be able to activate lights in a second stairwell while one is active.");
    }

    @Test
    public void testLightsDeactivateAfterDuration() {
        stairs.activateLights(Stairwell.LEFT);

        // Advance time by 300 frames (exactly the duration)
        for (int i = 0; i < 300; i++) {
            stairs.update();
        }

        assertFalse(stairs.areLightsActive(Stairwell.LEFT), "Lights should have timed out.");
        assertTrue(stairs.canActivateLights(), "Lights should have deactivated, allowing a new activation.");
    }

    @Test
    public void testStudentTracking() {
        stairs.studentEnterStairwell(testStudent, Stairwell.LEFT);
        assertTrue(stairs.getStudentsInStairwell(Stairwell.LEFT).contains(testStudent),
                "Student should be registered in the stairwell.");

        stairs.studentExitStairwell(testStudent, Stairwell.LEFT);
        assertFalse(stairs.getStudentsInStairwell(Stairwell.LEFT).contains(testStudent),
                "Student should be removed after exiting.");
    }

    @Test
    public void testResetSystem() {
        stairs.studentEnterStairwell(testStudent, Stairwell.LEFT);
        stairs.activateLights(Stairwell.LEFT);

        stairs.reset();

        assertEquals(5, stairs.getLightCharges(), "Reset should restore charges.");
        assertTrue(stairs.canActivateLights(), "Reset should allow light activation.");
        assertTrue(stairs.getStudentsInStairwell(Stairwell.LEFT).isEmpty(),
                "Reset should clear student occupancy.");
    }
}
