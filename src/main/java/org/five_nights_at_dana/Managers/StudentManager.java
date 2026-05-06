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

import java.util.ArrayList;
import java.util.List;
import org.five_nights_at_dana.AI.Location;
import org.five_nights_at_dana.AI.Personality;
import org.five_nights_at_dana.AI.Student;

/**
 * Manages all student objects, locations and movement intervals
 */
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
        students.add(new Student("Kaidou", "JumpscareEager", Personality.EAGER));
        students.add(new Student("Sean", "JumpscarePersistent", Personality.PERSISTENT));
        students.add(new Student("Cyrus", "JumpscareConfused", Personality.CONFUSED));
        students.add(new Student("Tyler", "JumpscareShy", Personality.SHY));
        students.add(new Student("Mason", "JumpscareRunner", Personality.RUNNER));
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
     * Applies a FNAF-1 Night-1 inspired aggression schedule at specific hours.
     *
     * <p>Reference schedule (AI 0..20):
     * - 2AM: Bonnie +1
     * - 3AM: Bonnie +1, Chica +1, Foxy +1
     * - 4AM: Bonnie +1, Chica +1, Foxy +1
     *
     * <p>Mapping in this project (by personality):
     * - EAGER behaves like Bonnie (increments at 2/3/4)
     * - PERSISTENT behaves like Chica (increments at 3/4)
     * - CONFUSED behaves like Foxy (increments at 3/4)
     * - SHY stays at 0 on Night 1 (Freddy-like)
     * - RUNNER is handled by the Classroom mechanic
     */
    public void applyNight1AggressionGrowth(int hour) {
        // Scale factor for this project’s larger 3-floor map.
        // FNAF 1 Night 1 increases are very small because the map is short;
        // here we need larger steps so AI meaningfully ramps within one night.
        final int SCALE = 2;

        for (Student s : students) {
            int delta = switch (s.getPersonality()) {
                case EAGER -> (hour == 2 || hour == 3 || hour == 4) ? (1 * SCALE) : 0;
                case PERSISTENT, CONFUSED, SHY -> (hour == 3 || hour == 4) ? (1 * SCALE) : 0;
                default -> 0;
            };

            if (delta > 0) {
                s.increaseAiLevel(delta);
            }
        }
    }

    /**
     * Applies a small Night-1 baseline so students can move before 2 AM.
     * FNAF uses per-character starting AI; with a 1..20 roll system, AI=0 means "never moves".
     */
    public void applyNight1StartingAiLevels() {
        for (Student s : students) {
            int start = switch (s.getPersonality()) {
                case EAGER -> 2;
                case PERSISTENT, CONFUSED, SHY -> 1;
                default -> 0;
            };

            if (start > 0) {
                s.setAiLevel(start);
            }
        }
    }

    /**
     * Resets all students to their initial state.
     */
    public void reset() {
        students.clear();
        createStudents();
    }
}
