package org.five_nights_at_dana.AI.Pathing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for the individual nodes on the made
 */
public class PathPoint {
    private final Location location;
    private final List<PathPoint> nextLocations;

    /**
     * Constructor for a dead end
     * @param location of the point
     */
    public PathPoint(Location location) {
        this.location = location;
        this.nextLocations = new ArrayList<>();
    }

    /**
     * Constructs a point on the map that leads to other points
     * @param location is current location you are at
     * @param nextLocations the possible locations that can be travelled too
     */
    public PathPoint(Location location, PathPoint... nextLocations) {
        this.location = location;
        this.nextLocations = Arrays.asList(nextLocations);
    }

    public Location getLocation() {
        return location;
    }

    public List<PathPoint> getNextLocations() {
        return nextLocations;
    }
}
