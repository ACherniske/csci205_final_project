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
 * Defines all valid spatial nodes within the game world.
 * Used by AI controllers for pathfinding, state tracking,
 * and determining proximity to the player's office.
 *
 * ****************************************
 */

package org.five_nights_at_dana.AI;

/**
 * Defines all possible locations students can occupy within the Dana Engineering building.
 * This enum acts as the primary coordinate system for AI movement, pathfinding,
 * and trigger-based events (like vents, elevators, and office jumpscares).
 */
public enum Location {
// ==================== FLOOR 1 ====================

    /** Building entrance; the initial spawn point for all students. */
    FLOOR1_ENTRANCE,

    /** Common area located on the first floor. */
    FLOOR1_LOUNGE,

    /** Hallway segment on the left side of Floor 1. */
    FLOOR1_HALLWAY_LEFT,

    /** Gardner Lecture Hall; contains the V1 vent entrance. */
    FLOOR1_GARDNER,

    /** Maker-E engineering makerspace. */
    FLOOR1_MAKER_E,

    /** Hallway segment on the right side of Floor 1. */
    FLOOR1_HALLWAY_RIGHT,

    /** Left stairwell; associated with the music-based game mechanic. */
    FLOOR1_STAIR_LEFT,

    /** Middle stairwell; characterized by darkness and emergency lighting. */
    FLOOR1_STAIR_MID,

    /** Right stairwell; monitored by motion sensor mechanics. */
    FLOOR1_STAIR_RIGHT,

    /** Entrance to the elevator system on Floor 1. */
    FLOOR1_ELEVATOR,

    // ==================== FLOOR 2 ====================

    /** Hallway segment on the left side of Floor 2. */
    FLOOR2_HALLWAY_LEFT,

    /** Classroom setting; contains the V2 vent entrance. */
    FLOOR2_CLASSROOM,

    /** Computer Lab; visible to the player via CAM 2A. */
    FLOOR2_COMPUTER_LAB,

    /** Central hallway on Floor 2. */
    FLOOR2_HALLWAY_CENTER,

    /** Hallway segment on the right side of Floor 2. */
    FLOOR2_HALLWAY_RIGHT,

    /** Left stairwell; associated with the music-based game mechanic. */
    FLOOR2_STAIR_LEFT,

    /** Middle stairwell; characterized by darkness and emergency lighting. */
    FLOOR2_STAIR_MID,

    /** Right stairwell; monitored by motion sensor mechanics. */
    FLOOR2_STAIR_RIGHT,

    /** Elevator access on Floor 2; currently non-functional. */
    // FLOOR2_ELEVATOR,

    // ==================== FLOOR 3 ====================

    /** ECEG Testing Lab facility on Floor 3. */
    FLOOR3_TESTING_LAB,

    /** Hallway segment on the left side of Floor 3. */
    FLOOR3_HALLWAY_LEFT,

    /** Central hallway on Floor 3. */
    FLOOR3_HALLWAY_CENTER,

    /** Computer Lab on Floor 3 (CAM 3D). Primary location for the "Runner" AI student. */
    FLOOR3_COMPUTER_LAB,

    /** Right-side hallway; location of the V3 vent exit. */
    FLOOR3_HALLWAY_RIGHT,

    /** Left stairwell; associated with the music-based game mechanic. */
    FLOOR3_STAIR_LEFT,

    /** Middle stairwell; characterized by darkness and emergency lighting. */
    FLOOR3_STAIR_MID,

    /** Right stairwell; monitored by motion sensor mechanics. */
    FLOOR3_STAIR_RIGHT,

    /** Exit point for the elevator system on Floor 3. */
    FLOOR3_ELEVATOR_EXIT,

    /** Immediate area outside the office door; final staging point before a jumpscare. */
    FLOOR3_AT_DOOR,

    // ==================== SPECIAL ====================

    /** The player's office; triggers the game-over jumpscare event. */
    IN_OFFICE,

    /** Active state when a student is traversing the vent network. */
    IN_VENT,

    /** Active state when a student is transitioning between floors via elevator. */
    IN_ELEVATOR;

    /**
     * Determines the {@link PathType} associated with this location.
     * Used by AI to prioritize movement based on their personality preferences.
        *
        * @return the {@link PathType} assigned to this location, or null if it
        *       is a neutral zone (like a hallway or office)
     */
    public PathType getPathType() {
        String name = this.name();
        if (name.contains("STAIR_LEFT")) {
            return PathType.LEFT_STAIRS;
        }

        if (name.contains("STAIR_MID")) {
            return PathType.MIDDLE_STAIRS;
        }

        if (name.contains("STAIR_RIGHT")) {
            return PathType.RIGHT_STAIRS;
        }

        if (name.contains("ELEVATOR")) {
            return PathType.ELEVATOR;
        }

        if (name.contains("VENT")) {
            return PathType.VENT;
        }

        return null; // For general hallways or special locations
    }

    /**
     * Indicates whether this location is an entry point into the vent system.
     *
     * @return true if students can enter vents from this location
     */
    public boolean hasVentAccess() {
        return switch (this) {
            case FLOOR1_GARDNER,
                 FLOOR2_CLASSROOM -> true;
            default -> false;
        };
    }

    /**
     * Gets the floor number for this location.
     *
     * @return 1-3 for standard floor nodes, 4 for office (goal layer), -1 for transitional nodes,
     *      0 otherwise
     */
    public int getFloor() {
        String name = this.name();

        if (name.startsWith("FLOOR1")) {
            return 1;
        }

        if (name.startsWith("FLOOR2")) {
            return 2;
        }

        if (name.startsWith("FLOOR3")) {
            return 3;
        }

        // Special nodes
        if (this == IN_OFFICE) {
            return 4;       // treat as goal layer
        }

        if (this == IN_ELEVATOR) {
            return -1;    // transitional
        }

        if (this == IN_VENT) {
            return -1;        // transitional
        }

        return 0; // fallback / unknown
    }
}
