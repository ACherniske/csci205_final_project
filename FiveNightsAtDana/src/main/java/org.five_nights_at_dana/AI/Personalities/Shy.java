package org.five_nights_at_dana.AI.Personalities;

import java.util.List;
import org.five_nights_at_dana.AI.Pathing.Location;
import org.five_nights_at_dana.AI.Pathing.PathPoint;
import org.five_nights_at_dana.AI.Student;

/**
 * Shy personality
 * 4 second movement interval
 * Moves randomly till they reach the classroom
 * Being seen on camera resets movement interval
 */
public class Shy implements Personality {
    @Override
    public double getMovementInterval() {
        return 4;
    }

    @Override
    public PathPoint chooseNextPoint(PathPoint currentPoint) {
        List<PathPoint> path = currentPoint.getNextLocations();
        int rand = (int) (Math.random() * path.size());

        // Student in office or classroom
        if (path.isEmpty()) {
            return currentPoint;
        }

        for (PathPoint point : path) {
            if (point.getLocation() == Location.FLOOR3_CLASSROOM) {
                return point;
            }
        }

        return path.get(rand);
    }

    @Override
    public void jumpscare() {
        // TODO: Shy Jumpscare
        System.out.println("SHY JUMPSCARE");
    }

    @Override
    public void reactToCamera(Student student) {
        student.resetMovementTimer();
        System.out.println("Froze shy guy");
    }
}

