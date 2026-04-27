package org.five_nights_at_dana.Systems.Vent;

import org.five_nights_at_dana.AI.Location;
import org.five_nights_at_dana.AI.Personality;
import org.five_nights_at_dana.AI.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VentSystemTest {

    private VentSystem vent;
    private Student testStudent;

    @BeforeEach
    public void setUp() {
        vent = new VentSystem();
        testStudent = new Student("Test Student", "Test", Personality.SHY);
    }

    @Test
    public void testStudentEntersVent() {
        boolean success = vent.studentEnterVent(testStudent, Location.FLOOR1_GARDNER);
        assertTrue(success, "Student should enter V1.");
        assertTrue(vent.hasStudent());
        assertEquals(testStudent, vent.getStudentInVent());
    }

    @Test
    public void testVentSealingEjectsStudent() {
        vent.studentEnterVent(testStudent, Location.FLOOR1_GARDNER);
        assertTrue(vent.hasStudent());

        vent.sealVent();
        assertFalse(vent.hasStudent(), "Student should be ejected when vent is sealed.");
    }

    @Test
    public void testSealCooldownPreventsImmediateReseal() {
        vent.sealVent();
        vent.reset(); // Need to simulate time passing or manual unseal
        // Actually, just verify it can't seal if cooldown > 0
        vent.sealVent();
        assertTrue(vent.isSealed());
        vent.unsealVent();

        assertFalse(vent.canSeal(), "Should not be able to seal during cooldown.");
    }

    @Test
    public void testStudentTransitTime() {
        vent.studentEnterVent(testStudent, Location.FLOOR2_CLASSROOM); // V2 = 10s (600 frames)

        // Fast-forward 5 seconds
        for(int i = 0; i < 300; i++) vent.update();

        assertEquals(5, vent.getTravelTimeRemainingSeconds(), "Travel time should decrease.");
    }

    @Test
    public void testVentTransitExits() {
        vent.studentEnterVent(testStudent, Location.FLOOR2_CLASSROOM);

        // Fast-forward full transit
        for(int i = 0; i < 600; i++) vent.update();

        assertFalse(vent.hasStudent(), "Student should have exited.");
    }

    @Test
    public void testCannotDoubleEnterVent() {
        Student student2 = new Student("Second Student", "Test", Personality.RUNNER);
        vent.studentEnterVent(testStudent, Location.FLOOR1_GARDNER);

        boolean success = vent.studentEnterVent(student2, Location.FLOOR2_CLASSROOM);
        assertFalse(success, "Second student should not be able to enter an occupied vent.");
    }

    @Test
    public void testInvalidEntryPoint() {
        boolean success = vent.studentEnterVent(testStudent, Location.FLOOR3_COMPUTER_LAB);
        assertFalse(success, "Entering from an invalid location should return false.");
        assertFalse(vent.hasStudent());
    }

    @Test
    public void testSealVentWhenEmpty() {
        boolean success = vent.sealVent();
        assertTrue(success, "Should be able to seal an empty vent.");
        assertTrue(vent.isSealed());
        assertFalse(vent.hasStudent());
    }

    @Test
    public void testPowerDrainCalculations() {
        assertEquals(0.0, vent.getPowerDrain(), "Should drain no power when unsealed.");

        vent.sealVent();
        assertEquals(0.08, vent.getPowerDrain(), 0.001, "Should drain power when sealed.");
    }
}
