/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/26/2026
 * Time: 12:21 AM
 *
 * Project: csci205_final_project
 * Package: Rendering
 * Class: CameraSystemTest
 *
 * Description:
 * This file will test the notification manager
 *
 * ****************************************
 */

package org.five_nights_at_dana.Rendering;

import org.five_nights_at_dana.AI.Student;
import org.five_nights_at_dana.AI.Personality;
import org.five_nights_at_dana.AI.Location; // Ensure you import your Location enum
import org.five_nights_at_dana.Managers.AssetManager;
import javafx.embed.swing.JFXPanel;
import org.five_nights_at_dana.Rendering.Camera.CameraSystem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CameraSystemTest {

    private List<Student> students;
    private CameraSystem cameraSystem;

    @BeforeAll
    public static void initJFX() {
        new JFXPanel();
        AssetManager.preloadAll();
    }

    @BeforeEach
    public void setUp() {
        students = new ArrayList<>();
        // Mocking the behavior of StudentManager by passing our list
        // Note: You may need a small modification to CameraSystem to accept this list
        cameraSystem = new CameraSystem(new MockStudentManagerHelper(students));
    }

    @Test
    public void testIsStudentVisible_DirectStudentTest() {
        // Setup: Create a student at the entrance
        Student s1 = new Student("Dana", "Where is the syllabus?", Personality.EAGER);
        s1.setLocation(Location.FLOOR1_ENTRANCE);
        students.add(s1);

        // Act: Set camera to Entrance
        cameraSystem.setActiveCamera("1A"); // Assuming 1A maps to FLOOR1_ENTRANCE

        // Assert
        assertTrue(cameraSystem.isStudentVisible(), "Camera 1A should see Dana at the Entrance.");
    }

    @Test
    public void testIsStudentVisible_MultipleStudents() {
        // Setup: Two students at the same location
        Student s1 = new Student("Alice", "Q1", Personality.EAGER);
        Student s2 = new Student("Bob", "Q2", Personality.SHY);
        s1.setLocation(Location.FLOOR2_COMPUTER_LAB);
        s2.setLocation(Location.FLOOR2_COMPUTER_LAB);
        students.add(s1);
        students.add(s2);

        // Act: Set camera to computer lab
        cameraSystem.setActiveCamera("2C");

        // Assert
        assertTrue(cameraSystem.isStudentVisible(), "Camera 2C should see both students.");
    }

    @Test
    public void testIsStudentVisible_EmptyLocation() {
        // Setup: Student is somewhere else
        Student s1 = new Student("Dana", "Q1", Personality.EAGER);
        s1.setLocation(Location.FLOOR3_TESTING_LAB);
        students.add(s1);

        // Act: Camera is looking at entrance
        cameraSystem.setActiveCamera("1A");

        // Assert
        assertFalse(cameraSystem.isStudentVisible(), "Camera 1A should see nobody.");
    }

    @Test
    public void testStudentMovementUpdatesVisibility() {
        // Setup: Student at entrance
        Student s1 = new Student("Dana", "Q1", Personality.EAGER);
        s1.setLocation(Location.FLOOR1_ENTRANCE);
        students.add(s1);

        // Act 1: See student
        cameraSystem.setActiveCamera("1A");
        assertTrue(cameraSystem.isStudentVisible());

        // Act 2: Move student and check visibility again
        s1.setLocation(Location.FLOOR3_TESTING_LAB);
        assertFalse(cameraSystem.isStudentVisible(), "Visibility should update after student moves.");
    }

    @Test
    public void testInvalidCameraSafety() {
        // Act: Set an invalid camera
        cameraSystem.setActiveCamera("NON_EXISTENT");

        // Assert: Should not crash and should default to original camera (1A)
        // Ensure system remains operational
        assertNotNull(cameraSystem.getActiveCamera());
        // Depending on your implementation, it might still return the previous valid camera
    }

    /**
     * This test will test the default camera
     */
    @Test
    public void testDefaultCamera() {
        assertNotNull(cameraSystem.getActiveCamera());
        assertEquals("1A", cameraSystem.getActiveCamera().id(), "Default camera should be 1A.");
    }

    @Test
    public void testSwitchBetweenValidCameras() {
        cameraSystem.setActiveCamera("2C");
        assertEquals("2C", cameraSystem.getActiveCamera().id());
        cameraSystem.setActiveCamera("1A");
        assertEquals("1A", cameraSystem.getActiveCamera().id());
    }

    @Test
    public void testIsStudentVisible_NoStudentsAnywhere() {
        // students list is empty
        cameraSystem.setActiveCamera("1A");
        assertFalse(cameraSystem.isStudentVisible(), "No students exist, so nothing should be visible.");
    }

    @Test
    public void testIsStudentVisible_WrongCamera() {
        Student s1 = new Student("Test Student", "Q?", Personality.EAGER);
        s1.setLocation(Location.FLOOR2_COMPUTER_LAB);
        students.add(s1);

        cameraSystem.setActiveCamera("1A"); // not the computer lab
        assertFalse(cameraSystem.isStudentVisible());

        cameraSystem.setActiveCamera("2C"); // now pointing at the right place
        assertTrue(cameraSystem.isStudentVisible());
    }

}