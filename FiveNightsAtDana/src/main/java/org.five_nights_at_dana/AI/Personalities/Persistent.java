package org.five_nights_at_dana.AI.Personalities;

import org.five_nights_at_dana.AI.Pathing.Path;
import org.five_nights_at_dana.AI.Pathing.PathPoint;
import org.five_nights_at_dana.AI.Student;

import java.util.List;

public class Persistent implements Personality{
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
        // TODO: Persistent Jumpscare
        System.out.println("Persistent JUMPSCARE");
    }

    @Override
    public void resetLocation(Student student) {
        student.setLocation(Path.getRandomSecondFloor());
    }

}
