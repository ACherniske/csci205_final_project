package org.five_nights_at_dana.AI.Personalities;

import java.util.List;
import org.five_nights_at_dana.AI.Pathing.PathPoint;

/**
 * Eager Personality
 * 3 second movement interval
 * Takes the fastest path to the office
 */
public class Eager implements Personality {
    @Override
    public double getMovementInterval() {
        return 3;
    }

    @Override
    public PathPoint chooseNextPoint(PathPoint currentPoint) {
        List<PathPoint> path = currentPoint.getNextLocations();
        return path.getFirst();
    }

    @Override
    public void jumpscare() {
        // TODO: Eager Jumpscare
        System.out.println("EAGER JUMPSCARE");
    }
}

