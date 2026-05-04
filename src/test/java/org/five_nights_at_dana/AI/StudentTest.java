/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/26/2026
 * Time: 12:21 AM
 *
 * Project: csci205_final_project
 * Package: AI
 * Class: StudentTest
 *
 * Description: This file will test some of the
 * attributes of the student class
 *
 * ****************************************
 */
package org.five_nights_at_dana.AI;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies the FNAF-style "hang at the door" timing.
 */
public class StudentTest {

    /**
     * This test will test to make sure that the student will linger at the door
     * for one move opportunity
     */
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

    /**
     * This test will test that the AI doesn't move when the level is set to 0
     */
    @Test
    void testAiLevelZeroNeverMoves() {
        Student s = new Student("TestStudent", "Q", Personality.EAGER);

        //set AI to be zero to test that it doesn't move
        s.setAiLevel(0);

        Location before = s.getCurrentLocation();

        //Attempt to move for 100 turns
        for (int i = 0; i < 100; i++) s.attemptMove();
        assertEquals(before, s.getCurrentLocation(),
                "AI level 0 should never produce movement");
    }

    /**
     * This test will test if the AI is capped at 20 and can't go higher
     */
    @Test
    void AiIsCappedAt20() {
        Student s = new Student("TestStudent", "Q", Personality.EAGER);

        //set AI level to 90
        s.setAiLevel(90);
        assertEquals(20, s.getAiLevel(), "AI level must not exceed 20");
    }


    /**
     * This test will test that the AI cannot go below zero
     */
    @Test
    void AiCanNotGoBelowZero() {
        Student s = new Student("TestStudent", "Q", Personality.EAGER);

        //set AI level to 90
        s.setAiLevel(-10);
        assertEquals(0, s.getAiLevel(), "AI level must not go below 0");
    }

    /**
     * This test will make sure that the AI level can be increased as expected
     */
    @Test
    void testThatTheLevelCanBeIncreased() {
        Student s = new Student("TestStudent", "Q", Personality.EAGER);

        //Set level to 5 and then increase it by 3
        s.setAiLevel(5);
        s.increaseAiLevel(3);

        //Test that the level is 5 + 3 = 8
        assertEquals(8, s.getAiLevel());
    }

    /**
     * This test will make sure that the increasing with a negative number will
     * have no effect on the overall AI level
     */
    @Test
    void aNegativeIncreaseHasNoEffect() {
        Student s = new Student("TestStudent", "Q", Personality.EAGER);
        //set to 5
        s.setAiLevel(5);
        //increase by zero
        s.increaseAiLevel(0);
        //will not increase
        s.increaseAiLevel(-2);
        assertEquals(5, s.getAiLevel(), "Non-positive delta should have no effect");
    }
}
