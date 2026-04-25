/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 3:45 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.AI
 * Enum: Location
 *
 * Description:
 *
 * ****************************************
 */

package org.five_nights_at_dana.AI.Pathing;

/**
 * Location enum
 */
public enum Location {
    // FLOOR 1
    FLOOR1_ENTRANCE,
    FLOOR1_LOUNGE,
    FLOOR1_HALLWAY_LEFT,
    FLOOR1_GARDNER,
    FLOOR1_MAKER_E,
    FLOOR1_HALLWAY_RIGHT,
    FLOOR1_STAIR_LEFT,
    FLOOR1_STAIR_MID,
    FLOOR1_STAIR_RIGHT,
    FLOOR1_ELEVATOR,

    // FLOOR 2
    FLOOR2_HALLWAY_LEFT,
    FLOOR2_CLASSROOM,
    FLOOR2_COMPUTER_LAB,
    FLOOR2_HALLWAY_CENTER,
    FLOOR2_HALLWAY_RIGHT,
    FLOOR2_STAIR_LEFT,
    FLOOR2_STAIR_MID,
    FLOOR2_STAIR_RIGHT,
    FLOOR2_ELEVATOR,

    // FLOOR 3
    FLOOR3_TESTING_LAB,
    FLOOR3_HALLWAY_LEFT,
    FLOOR3_HALLWAY_CENTER,
    FLOOR3_COMPUTER_LAB,  // RUNNER CLASSROOM
    FLOOR3_HALLWAY_RIGHT,
    FLOOR3_STAIR_LEFT,
    FLOOR3_STAIR_MID,
    FLOOR3_STAIR_RIGHT,
    FLOOR3_ELEVATOR_EXIT,
    FLOOR3_AT_DOOR,

    // SPECIAL
    IN_OFFICE,
    IN_VENT,
    IN_ELEVATOR
}

