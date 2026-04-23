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

/**
 * Student class
 */
public class Student {
    private int difficulty;
    private String name;
    private final Personality personality;
    private PathPoint currentLocation;
    private double movementTimer;
    private boolean jumpScared;

    /**
     * Constructs a student object with a set personality and difficulity
     * @param name is name of student
     * @param personality dictates the behavior of the student
     * @param difficulty an int from 1-20 that sets the odds of a movement opportunity passing
     */
    public Student(String name, Personality personality, int difficulty) {
        this.name = name;
        this.personality = personality;
        this.difficulty = difficulty;
        setLocation(Path.getRandomFirstFloor());
        movementTimer = 0.0;
        this.jumpScared = false;
    }

    /**
     * Updates the student object to react to the changing state of the game
     * @param deltaTime is to keep track of time, and lining up movement intervals
     * @param isDoorClosed the state of the door, whether closed or open
     * @param isSeenOnCam is the player on the cam where the student is
     */
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

    /**
     * Makes the student attempt of moving with the odds being if the random is less than the student's difficulty
     * @param isDoorClosed if a movement opportunity succeceds and the next move is into the office but the door is closed
     *                     the student is sent to a random place in the first floor.
     */
    public void attemptMove(boolean isDoorClosed) {
        if (Math.random() * 20 <= difficulty) {
            PathPoint nextMove = personality.chooseNextPoint(currentLocation);

            if (nextMove.getLocation() == Location.IN_OFFICE && isDoorClosed) {
                System.out.println("BANG");
                personality.resetLocation(this);
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

    public void increaseDifficulty() {
        this.difficulty++;
    }

    public Personality getPersonality() {
        return this.personality;
    }

    public String getName() {
        return name;
    }

    //    public Image getSilhouetteSprite() {
//        // TODO: Return the silhouette sprite image
//        return null;
//    }


}