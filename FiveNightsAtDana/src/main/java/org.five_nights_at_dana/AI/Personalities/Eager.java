package org.five_nights_at_dana.AI.Personalities;

import org.five_nights_at_dana.AI.Pathing.PathPoint;

import java.util.List;

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

