/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/25/2026
 * Time: 7:10 PM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.AI
 * Enum: PathType
 *
 * Description:
 * Defines the various transit methods available to student AI.
 * Each path type interacts with unique game mechanics (sensors,
 * vents, lights) that the player must manage to prevent incursions.
 *
 * ****************************************
 */

package org.five_nights_at_dana.AI;

/**
 * Defines the different path types students can utilize to navigate the
 * building and reach the office. Each path introduces distinct gameplay
 * risks and tactical considerations for the player.
 */
public enum PathType {

    /**
     * Normal path types that describe hallways and rooms seperate
     * from special transit methods.
     */
    NORMAL,

    /**
     * Utilizes the elevator system.
     * Characteristics: Fast transit but can be stopped by the player.
     * Preference: Optimal for EAGER students.
     */
    ELEVATOR,

    /**
     * Utilizes the left stairwell.
     * Characteristics: Movement speed is influenced by the active music mechanic.
     * Preference: Optimal for PERSISTENT students.
     */
    LEFT_STAIRS,

    /**
     * Utilizes the middle stairwell.
     * Characteristics: Dark area; students require emergency lighting to navigate.
     * Preference: Optimal for CONFUSED students.
     */
    MIDDLE_STAIRS,

    /**
     * Utilizes the right stairwell.
     * Characteristics: Monitored by motion sensors that alert the player to movement.
     * Preference: Used by various personalities; provides strategic info to the player.
     */
    RIGHT_STAIRS,

    /**
     * Utilizes the building's vent network.
     * Characteristics: Hidden movement path that the player can seal off.
     * Preference: Optimal for SHY students.
     */
    VENT
}
