package org.five_nights_at_dana.AI.Pathing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathPoint {
    private final Location location;
    private final List<PathPoint> nextLocations;

    public PathPoint(Location location) {
        this.location = location;
        this.nextLocations = new ArrayList<>();
    }

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
