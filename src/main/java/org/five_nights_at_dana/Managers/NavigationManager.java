/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/25/2026
 * Time: 8:22 PM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Managers
 * Class: NavigationManager
 *
 * Description:
 * Manages the graph connectivity of the building layout.
 * Provides neighbors for AI pathfinding and manages specific path transitions.
 *
 * ****************************************
 */

package org.five_nights_at_dana.Managers;

import org.five_nights_at_dana.AI.Location;
import org.five_nights_at_dana.AI.PathType;
import org.five_nights_at_dana.AI.Personality;
import org.five_nights_at_dana.AI.Student;

import java.util.*;

/**
 * Acts as the central hub for AI movement. Maps the building layout
 * as a graph where {@link Location} nodes are connected via valid paths.
 */
public class NavigationManager {

    private static final Random rand = new Random();

    public static Location getNextLocation(Student student) {
        Location current = student.getCurrentLocation();
        Personality personality = student.getPersonality();
        PathType preferred = student.getPreferredPath();

        switch (current) {

            // ===== FLOOR 1 =====
            case FLOOR1_ENTRANCE:
                return chooseFloor1Path(preferred);

            case FLOOR1_HALLWAY_LEFT:
                return (preferred == PathType.LEFT_STAIRS)
                        ? Location.FLOOR1_STAIR_LEFT
                        : Location.FLOOR1_STAIR_MID;

            case FLOOR1_HALLWAY_RIGHT:
                return (preferred == PathType.ELEVATOR)
                        ? Location.FLOOR1_ELEVATOR
                        : Location.FLOOR1_STAIR_RIGHT;

            case FLOOR1_GARDNER:
                if (personality == Personality.SHY && rand.nextDouble() < 0.3) {
                    return Location.IN_VENT;
                }
                return Location.FLOOR1_HALLWAY_LEFT;

            // ===== ELEVATOR =====
            case FLOOR1_ELEVATOR:
                return Location.IN_ELEVATOR;

            case IN_ELEVATOR:
                return Location.FLOOR3_ELEVATOR_EXIT;

            case FLOOR3_ELEVATOR_EXIT:
                return Location.FLOOR3_HALLWAY_RIGHT;

            // ===== STAIRS =====
            case FLOOR1_STAIR_LEFT:
                return Location.FLOOR2_STAIR_LEFT;
            case FLOOR1_STAIR_MID:
                return Location.FLOOR2_STAIR_MID;
            case FLOOR1_STAIR_RIGHT:
                return Location.FLOOR2_STAIR_RIGHT;

            case FLOOR2_STAIR_LEFT:
                return Location.FLOOR3_STAIR_LEFT;
            case FLOOR2_STAIR_MID:
                return Location.FLOOR3_STAIR_MID;
            case FLOOR2_STAIR_RIGHT:
                return Location.FLOOR3_STAIR_RIGHT;

            case FLOOR3_STAIR_LEFT:
            case FLOOR3_STAIR_MID:
            case FLOOR3_STAIR_RIGHT:
                return Location.FLOOR3_HALLWAY_RIGHT;

            // ===== FLOOR 2 =====
            case FLOOR2_CLASSROOM:
                if (personality == Personality.SHY && rand.nextDouble() < 0.4) {
                    return Location.IN_VENT;
                }
                return Location.FLOOR2_HALLWAY_CENTER;

            // ===== VENTS =====
            case IN_VENT:
                return Location.FLOOR3_HALLWAY_RIGHT;

            // ===== FLOOR 3 =====
            case FLOOR3_HALLWAY_LEFT:
            case FLOOR3_HALLWAY_CENTER:
            case FLOOR3_HALLWAY_RIGHT:
                return Location.FLOOR3_AT_DOOR;

            case FLOOR3_AT_DOOR:
                return Location.IN_OFFICE;

            default:
                return null;
        }
    }

    private static Location chooseFloor1Path(PathType preferred) {
        switch (preferred) {
            case ELEVATOR:
                return Location.FLOOR1_HALLWAY_RIGHT;
            case VENT:
                return Location.FLOOR1_GARDNER;
            case LEFT_STAIRS:
            case MIDDLE_STAIRS:
                return Location.FLOOR1_HALLWAY_LEFT;
            case RIGHT_STAIRS:
                return Location.FLOOR1_HALLWAY_RIGHT;
            default:
                return Location.FLOOR1_HALLWAY_LEFT;
        }
    }
}