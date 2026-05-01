package org.five_nights_at_dana.AI;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies the FNAF-style "hang at the door" timing.
 */
public class StudentDoorLingerTest {

    @Test
    void testStudentLingersAtDoorForOneMoveOpportunity() {
        Student s = new Student("TestStudent", "Q", Personality.EAGER);

        // Force the student to arrive at the door; this should arm the 1-opportunity linger.
        s.setLocation(Location.FLOOR3_AT_DOOR);
        s.setAiLevel(20); // always moves on opportunities

        // First opportunity at the door should be consumed by the linger (no movement).
        s.attemptMove();
        assertEquals(Location.FLOOR3_AT_DOOR, s.getCurrentLocation(),
                "Student should remain at the door for one movement opportunity");

        // Next opportunity: student can attempt entry.
        s.attemptMove();
        assertEquals(Location.IN_OFFICE, s.getCurrentLocation(),
                "After lingering, student should be able to enter the office");
    }
}
