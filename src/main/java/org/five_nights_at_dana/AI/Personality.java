/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/25/2026
 * Time: 7:01 PM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.AI
 * Enum: Personality
 *
 * Description:
 * Defines the AI personality profiles for students.
 * Each personality dictates specific movement speeds, pathfinding
 * preferences (e.g., vents vs. stairs), and aggression levels.
 *
 * ****************************************
 */

package org.five_nights_at_dana.AI;

/**
 * Defines various student personality types that influence AI behavior,
 * movement speed, and pathfinding strategies within the Dana Engineering building.
 */
public enum Personality {

    /**
     * Eager students move quickly and favor direct routes like the elevator.
     * Speed: 150% of base movement speed.
     */
    EAGER,

    /**
     * Shy students are slower and prefer stealthier, darker paths such as vents.
     * Speed: 70% of base movement speed.
     */
    SHY,

    /**
     * Confused students exhibit unpredictable, erratic movement patterns
     * and change their pathing frequently.
     * Speed: Randomly varies between 50% and 200% per move.
     */
    CONFUSED,

    /**
     * Persistent students maintain a steady pace and linger longer at
     * critical locations like the office door.
     * Speed: 120% of base movement speed.
     */
    PERSISTENT,

    /**
     * Runner students remain in a specific room until a trigger event occurs,
     * at which point they perform a high-speed sprint.
     * Speed: 500% during sprint events.
     */
    RUNNER
}
