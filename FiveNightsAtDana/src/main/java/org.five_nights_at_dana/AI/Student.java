/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 3:44 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.AI
 * Class: Student
 *
 * Description:
 *      Represents an AI-controlled student.
 *
 * ****************************************
 */

package org.five_nights_at_dana.AI;

import org.five_nights_at_dana.AI.Pathing.Location;
import org.five_nights_at_dana.AI.Pathing.Path;
import org.five_nights_at_dana.AI.Pathing.PathPoint;
import org.five_nights_at_dana.AI.Personalities.Personality;

public class Student {
    private final int difficulty;
    private final Personality personality;
    private PathPoint currentLocation;
    private double movementTimer;
    private boolean jumpScared;

    public Student(Personality personality, int difficulty) {
        this.personality = personality;
        this.difficulty = difficulty;
        setLocation(Path.getRandomFirstFloor());
        movementTimer = 0.0;
        this.jumpScared = false;
    }

    public void update(double deltaTime, boolean isDoorClosed, boolean isSeenOnCam) {
        movementTimer += deltaTime;

        if (isSeenOnCam)
        {
            personality.reactToCamera(this);
        }

        if (personality.getMovementInterval() <= movementTimer) {
            if (currentLocation.getLocation() == Location.IN_OFFICE) {
                personality.jumpscare();
                jumpScared = true;
                return;
            }

            attemptMove(isDoorClosed);
            resetMovementTimer();
        }

    }

    public void attemptMove(boolean isDoorClosed) {
        if (Math.random() * 20 <= difficulty) {
            PathPoint nextMove = personality.chooseNextPoint(currentLocation);

            if (nextMove.getLocation() == Location.IN_OFFICE && isDoorClosed) {
                System.out.println("BANG");
                setLocation(Path.getRandomFirstFloor());
            }
            else {
                setLocation(nextMove);
            }
        }
    }

    public boolean isJumpScared() {
        return jumpScared;
    }

    public PathPoint getCurrentLocation() {
        return currentLocation;
    }

    public void setLocation(PathPoint location) {
        this.currentLocation = location;
    }

    public void resetMovementTimer() {
        movementTimer = 0;
    }

//    public Image getSilhouetteSprite() {
//        // TODO: Return the silhouette sprite image
//        return null;
//    }
}