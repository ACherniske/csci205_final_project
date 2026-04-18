package org.five_nights_at_dana.AI.Personalities;

import org.five_nights_at_dana.AI.Pathing.PathPoint;
import org.five_nights_at_dana.AI.Personalities.Personality;

import java.util.List;

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
        // TODO: Lazy Jumpscare
        System.out.println("Lazy JUMPSCARE");
    }
}

