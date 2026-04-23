package org.five_nights_at_dana.AI.Personalities;

import java.util.List;
import org.five_nights_at_dana.AI.Pathing.PathPoint;
import org.five_nights_at_dana.AI.Personalities.Personality;

/**
 * Lazy Personality
 * 6 second movement interval
 * Takes longest path to the office
 */
public class Lazy implements Personality {
    @Override
    public double getMovementInterval() {
        return 6;
    }

    @Override
    public PathPoint chooseNextPoint(PathPoint currentPoint) {
        List<PathPoint> path = currentPoint.getNextLocations();
        return path.getLast();
    }

    @Override
    public void jumpscare() {
    }
}

