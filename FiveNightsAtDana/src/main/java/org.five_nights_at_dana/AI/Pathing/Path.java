package org.five_nights_at_dana.AI.Pathing;

import java.util.Arrays;
import java.util.List;

/**
 * Class for the pathing a student can take
 *
 * To read it, the first parameter and name of variable is the from,
 * and whatever comes after is the list of possible locations the could go from that location
 * For example RIGHT_STAIRS3 leads to HALLWAY_3B or the office
 */
public class Path {

    // Dead ends
    public static final PathPoint OFFICE = new PathPoint(Location.IN_OFFICE);
    public static final PathPoint CLASSROOM_3C = new PathPoint(Location.FLOOR3_CLASSROOM);

    // FLOOR 3
    public static final PathPoint HALLWAY_3B = new PathPoint(Location.FLOOR3_RIGHT_HALLWAY,
            OFFICE);

    public static final PathPoint RIGHT_STAIRS3 = new PathPoint(Location.FLOOR3_RIGHT_STAIRS,
            HALLWAY_3B,
            OFFICE);

    public static final PathPoint ELEVATOR_EXIT = new PathPoint(Location.FLOOR3_ELEVATOR_EXIT,
            HALLWAY_3B);

    public static final PathPoint HALLWAY_3C = new PathPoint(Location.FLOOR3_CENTER_HALLWAY,
            HALLWAY_3B,
            CLASSROOM_3C);

    public static final PathPoint HALLWAY_3A = new PathPoint(Location.FLOOR3_LEFT_HALLWAY,
            HALLWAY_3C);

    public static final PathPoint MIDDLE_STAIRS_3 = new PathPoint(Location.FLOOR3_MIDDLE_STAIRS,
            HALLWAY_3C);

    public static final PathPoint LEFT_STAIRS_3 = new PathPoint(Location.FLOOR3_LEFT_STAIRS,
            HALLWAY_3A);


    //Floor 2
    public static final PathPoint RIGHT_STAIRS_2 = new PathPoint(Location.FLOOR2_RIGHT_STAIRS,
            RIGHT_STAIRS3);

    public static final PathPoint LEFT_STAIRS_2 = new PathPoint(Location.FLOOR2_LEFT_STAIRS,
            LEFT_STAIRS_3);

    public static final PathPoint HALLWAY_2B = new PathPoint(Location.FLOOR2_RIGHT_HALLWAY,
            RIGHT_STAIRS_2);

    public static final PathPoint HALLWAY_2A = new PathPoint(Location.FLOOR2_LEFT_HALLWAY,
            LEFT_STAIRS_2);

    public static final PathPoint HALLWAY_2C = new PathPoint(Location.FLOOR2_MIDDLE_HALLWAY,
            HALLWAY_2A,
            HALLWAY_2B);

    public static final PathPoint MIDDLE_STAIRS_2 = new PathPoint(Location.FLOOR2_MIDDLE_STAIRS,
            MIDDLE_STAIRS_3,
            HALLWAY_2C);

    //Floor 1
    public static final PathPoint RIGHT_STAIRS_1 = new PathPoint(Location.FLOOR1_RIGHT_STAIRS,
            RIGHT_STAIRS_2);

    public static final PathPoint LEFT_STAIRS_1 = new PathPoint(Location.FLOOR1_LEFT_STAIRS,
            LEFT_STAIRS_2);

    public static final PathPoint HALLWAY_1B = new PathPoint(Location.FLOOR1_RIGHT_HALLWAY,
            RIGHT_STAIRS_1);

    public static final PathPoint HALLWAY_1A = new PathPoint(Location.FLOOR1_LEFT_HALLWAY,
            LEFT_STAIRS_1);

    public static final PathPoint MIDDLE_STAIRS_1 = new PathPoint(Location.FLOOR1_MIDDLE_STAIRS,
            MIDDLE_STAIRS_2);

    public static final PathPoint ELEVATOR_IN = new PathPoint(Location.ELEVATOR_ENTER,
            ELEVATOR_EXIT);

    public static final PathPoint ENTRANCE = new PathPoint(Location.FLOOR1_ENTRANCE,
            MIDDLE_STAIRS_1,
            ELEVATOR_IN,
            HALLWAY_1A,
            HALLWAY_1B);

    /**
     * Gets a random point in the first floor
     * @return a random point in the fitrst floor
     */
    public static PathPoint getRandomFirstFloor() {
        List<PathPoint> floor1Locations = Arrays.asList(
                ENTRANCE,
                HALLWAY_1A,
                HALLWAY_1B
        );

        int randomFloorIndex = (int) (Math.random() * floor1Locations.size());
        return floor1Locations.get(randomFloorIndex);
    }
}

