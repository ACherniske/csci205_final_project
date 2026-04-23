/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 3:45 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.AI
 * Enum: Personality
 *
 * Description:
 *
 * ****************************************
 */

package org.five_nights_at_dana.AI.Personalities;

import org.five_nights_at_dana.AI.Pathing.Location;
import org.five_nights_at_dana.AI.Pathing.Path;
import org.five_nights_at_dana.AI.Pathing.PathPoint;
import org.five_nights_at_dana.AI.Student;

import java.util.List;

/**
 * Personality Interface
 */
public interface Personality {
    /**
     * Set the movement intreval of the student
     * @return a double of the intreval between movement attempts
     */
    double getMovementInterval();

    /**
     * Determines the movement behavior of a student
     * @param currentPoint the current location a student is at
     * @return the next location a student will go to
     */
    default PathPoint chooseNextPoint(PathPoint currentPoint) {
        List<PathPoint> path = currentPoint.getNextLocations();
        int rand = (int) (Math.random() * path.size());

        // Student in office or classroom
        if (path.isEmpty()) {
            return currentPoint;
        }

        return path.get(rand);
    }

    /**
     * Jumpscare boo
     */
    void jumpscare();

    /**
     * Behavior if a student is seen on the camera
     * @param student the student that is seen on camera
     */
    default void reactToCamera(Student student) {
        // Nothing happens unless personality is a camera reacting type
    }

    default void resetLocation(Student student) {
        student.setLocation(Path.getRandomFirstFloor());
    }
}