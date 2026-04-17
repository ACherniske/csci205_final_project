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
 *
 * ****************************************
 */

package org.five_nights_at_dana.AI;

public class Student {

    private String name;
    private String question;
    private Personality personality;
    private Location currentLocation;
    private PathType currentPath;

    private int movementTimer;
    private int difficulty;
    private double awarenessLevel;
    private boolean sprinting;

    public Student(String name, String question, Personality p) {
        this.name = name;
        this.question = question;
        this.personality = p;
    }

    public void update() {
        attemptMove();
    }

    public void increaseDifficulty() {}
    public void startSprint() { sprinting = true; }
    public boolean isSprinting() { return sprinting; }

    public String getName() { return name; }
    public String getQuestion() { return question; }
    public Personality getPersonality() { return personality; }
    public Location getCurrentLocation() { return currentLocation; }

    private void attemptMove() {}
    private void resetMovementTimer() {}
}