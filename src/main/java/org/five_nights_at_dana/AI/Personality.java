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
    RUNNER;

    /**
     * Returns the movement-opportunity interval (in seconds) for this personality.
     *
     * <p>This is inspired by FNAF-style AI checks: every N seconds, the AI gets a
     * chance to move (subject to AI level + stalling rules).
     */
    public double getMoveOpportunityIntervalSeconds() {
        return switch (this) {
            // Roughly modeled after classic FNAF 1 timings (character-specific)
            case SHY -> 3.02;        // Freddy-like: frequent checks, often stallable
            case EAGER -> 4.97;      // Bonnie-like
            case PERSISTENT -> 4.90; // Chica-like (close to Bonnie)
            case CONFUSED -> 5.00;   // Foxy-like
            case RUNNER -> 0.17;     // sprint step interval (handled specially by Student)
        };
    }

    /**
     * Whether this personality is prevented from moving while being watched on cameras.
     * This varies by character in the original games.
     */
    public boolean stallsWhenWatched() {
        return switch (this) {
            case SHY -> true;
            default -> false;
        };
    }

    /**
     * Gets the primary path type this personality will generally prefer.
     *
     * @return preferred path type
     */
    public PathType getPreferredPath() {
        return switch (this) {
            case EAGER -> PathType.ELEVATOR;
            case SHY -> PathType.VENT;
            case CONFUSED -> PathType.MIDDLE_STAIRS;
            case PERSISTENT -> PathType.LEFT_STAIRS;
            case RUNNER -> PathType.RIGHT_STAIRS;
            default -> PathType.RIGHT_STAIRS;
        };
    }
}
