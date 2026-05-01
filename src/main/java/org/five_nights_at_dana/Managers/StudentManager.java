/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 4:02 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Managers
 * Class: StudentManager
 *
 * Description:
 * Central controller for all student AI entities.
 * Responsible for creating, updating, and querying students
 *
 * ****************************************
 */

package org.five_nights_at_dana.Managers;

import org.five_nights_at_dana.AI.Location;
import org.five_nights_at_dana.AI.Personality;
import org.five_nights_at_dana.AI.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentManager {

    /** List containing all active student AI entities. */
    private List<Student> students;

    /**
     * Constructs a new {@code StudentManager} and initializes all student AI.
     */
    public StudentManager() {
        students = new ArrayList<>();
        createStudents();
        System.out.println("StudentManager: Created " + students.size() + " students");
    }

    /**
     * Instantiates all student AI with predefined personalities and dialogue.
     */
    private void createStudents() {
        // TODO RENAME STUDENTS
        students.add(new Student("Student1", "JumpscareEager", Personality.EAGER));
        students.add(new Student("Student2", "JumpscarePersistent", Personality.PERSISTENT));
        students.add(new Student("Student3", "JumpscareConfused", Personality.CONFUSED));
        students.add(new Student("Student4", "JumpscareShy", Personality.SHY));
        students.add(new Student("Student5", "JumpscareEager?", Personality.EAGER));
        students.add(new Student("Student6", "JumpscareRunner", Personality.RUNNER));
    }

    /**
     * Updates all student AI each frame.
     */
    public void update() {
        for (Student student : students) {
            student.update();
        }
    }

    /**
     * Retrieves the RUNNER personality student.
     *
     * @return the runner student, or null if not found
     */
    public Student getRunnerStudent() {
        for (Student student : students) {
            if (student.getPersonality() == Personality.RUNNER) {
                return student;
            }
        }
        return null;
    }

    /**
     * Returns a student currently at the office door.
     *
     * @return student at door, or null if none
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
     * Returns a student currently inside the office.
     *
     * @return student in office, or null if none
     */
    public Student getStudentInOffice() {
        for (Student student : students) {
            if (student.getCurrentLocation() == Location.IN_OFFICE) {
                return student;
            }
        }
        return null;
    }

    /**
     * Checks if any student is at the office door.
     *
     * @return true if a student is at the door
     */
    public boolean isStudentAtDoor() {
        return getStudentAtDoor() != null;
    }

    /**
     * Retrieves all students currently at a given location.
     *
     * <p>This method is used by external systems (e.g., CameraSystem)
     * to determine visibility without exposing internal data structures.
     *
     * @param location the location to query
     * @return list of students at that location (empty if none)
     */
    public List<Student> getStudentsAt(Location location) {
        List<Student> result = new ArrayList<>();

        for (Student student : students) {
            if (student.getCurrentLocation() == location) {
                result.add(student);
            }
        }

        return result;
    }

    /**
     * Returns a copy of all students.
     */
    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    /**
     * Increases difficulty for all students.
     */
    public void increaseDifficulty() {
        for (Student student : students) {
            student.increaseDifficulty();
        }
        System.out.println("StudentManager: Difficulty increased!");
    }

    /**
     * Resets all students to their initial state.
     */
    public void reset() {
        students.clear();
        createStudents();
    }
}
