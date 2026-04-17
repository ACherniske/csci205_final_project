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

package org.five_nights_at_dana.AI;

import org.five_nights_at_dana.AI.enums.Location;
import org.five_nights_at_dana.AI.enums.Personality;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StudentManager {

    private List<Student> students;

    /**
     * Constructs the StudentManager and initializes students.
     */
    public StudentManager() {
        students = new ArrayList<>();
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
            student.update();
        }
    }

    /**
     * Gets a student currently at the office door.
     *
     * @return Student at door, or null if none present
     *
     * @implNote Assumes only one student can occupy the door at a time
     */
    public Student getStudentAtDoor() {
        for (Student student : students) {
            if (student.getCurrentLocation() == Location.FLOOR3_AT_DOOR) {
                return student;
            }
        }
        return null;
    }

    /**
     * Checks if any student is currently at the office door.
     *
     * @return true if at least one student is at the door
     */
    public boolean isStudentAtDoor() {
        return getStudentAtDoor() != null;
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
    public List<Student> getStudentsAt(String locationName) {
        // TODO consider changing to Location parameter instead of String

        return students.stream()
                .filter(s -> s.getCurrentLocation() != null &&
                        s.getCurrentLocation().name().equals(locationName))
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

        students.add(new Student("Alex", "Can you help me?", Personality.EAGER));
        students.add(new Student("Jamie", "I'm confused...", Personality.CONFUSED));
        students.add(new Student("Taylor", "I have a question.", Personality.PERSISTENT));
        students.add(new Student("Morgan", "Uh... hello?", Personality.SHY));
        students.add(new Student("Riley", "I need help NOW!", Personality.RUNNER));
        students.add(new Student("Jordan", "Excuse me?", Personality.EAGER));
    }
}