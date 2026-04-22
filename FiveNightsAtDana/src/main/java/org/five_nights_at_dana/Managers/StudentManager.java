/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 4:02 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.AI
 * Class: StudentManager
 *
 * Description:
 *      Manages all student AI entities in the game.
 *
 * ****************************************
 */

package org.five_nights_at_dana.Managers;

import org.five_nights_at_dana.AI.Pathing.PathPoint;
import org.five_nights_at_dana.AI.Personalities.*;
import org.five_nights_at_dana.AI.Player;
import org.five_nights_at_dana.AI.Student;
import org.five_nights_at_dana.AI.Pathing.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StudentManager {

    private List<Student> students;
    private Player player;
    /**
     * Constructs the StudentManager and initializes students.
     */
    public StudentManager(Player player) {
        students = new ArrayList<>();
        this.player = player;
        createStudents();
    }

    /**
     * Updates all students for the current frame.
     *
     * <p>Responsibilities:
     * - Calls update() on each student
     * - Ensures AI behavior progresses each frame
     */
    public void update() {
        for (Student student : students) {
            boolean isLookingAtStudent = player.getCurrentCameraRoom() == student.getCurrentLocation()
                    && player.isLookingAtCam();

            student.update(1, player.isDoorClosed(), isLookingAtStudent);

            Student jumping = jumpscaringStudent();
            if (jumping != null) {
                player.isKilled(true);
                System.out.println("Jumpscared by: " + jumping.getName());
                break;
            }
        }
    }

    public Student jumpscaringStudent() {
        for (Student student : students) {
            if (student.isJumpScared()) {
                return student;
            }
        }
        return null;
    }

    /**
     * Gets a student currently at the office door.
     *
     * @return Student at door, or null if none present
     *
     * @implNote Assumes only one student can occupy the door at a time
     */
    public List<Student> getStudentsAtDoor() {
        List<Student> studentAtDoor = new ArrayList<>();
        for (Student student : students) {
            PathPoint nextMove = student.getPersonality().chooseNextPoint(student.getCurrentLocation());
            if (nextMove.getLocation() == Location.IN_OFFICE) {
                studentAtDoor.add(student);
            }
        }
        return studentAtDoor;
    }

    /**
     * Checks if any student is currently at the office door.
     *
     * @return true if at least one student is at the door
     */
    public boolean isStudentAtDoor() {
        return getStudentsAtDoor() != null;
    }

    /**
     * Retrieves all students at a given location.
     *
     * @param locationName String representation of Location enum
     * @return List of students at the specified location
     *
     * @implNote Uses String comparison for flexibility, but could be optimized
     * to use Location enum directly.
     */
    public List<Student> getStudentsAt(PathPoint locationName) {
        return students.stream()
                .filter(s -> s.getCurrentLocation() != null &&
                        s.getCurrentLocation().equals(locationName))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all students managed by the system.
     *
     * @return list of all students
     */
    public List<Student> getAllStudents() {
        return students;
    }

    /**
     * Increases difficulty for all students.
     *
     * <p>Effects:
     * - Faster movement
     * - More aggressive behavior
     * - Higher chance of sprinting (for RUNNER)
     */
    public void increaseDifficulty() {
        for (Student student : students) {
            student.increaseDifficulty();
        }
    }

    /**
     * Resets all students to initial state.
     *
     * <p>Used when:
     * - Restarting the game
     * - Resetting difficulty
     */
    public void reset() {
        students.clear();
        createStudents();
    }

    /**
     * Initializes all student instances.
     *
     * @implNote Currently hardcoded.
     * Future improvement:
     * - Load from config
     * - Randomized personalities
     */
    private void createStudents() {
        // TODO expand with full roster and variety
        // Guy is gender neurtal im not just making them all males btw if that was a think u were thinking
        Student shyGuy = new Student("Bob" ,new Shy(), 15);
        students.add(shyGuy);

        Student eagerGuy = new Student("Jeremy" ,new Eager(), 15);
        students.add(eagerGuy);

        Student lazyGuy = new Student("Chloe" ,new Lazy(), 15);
        students.add(lazyGuy);

        Student persistentGuy = new Student("Charlie" ,new Persistent(), 15);
        students.add(persistentGuy);
    }
}