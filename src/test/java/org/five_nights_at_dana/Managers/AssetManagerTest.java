/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/26/2026
 * Time: 12:21 AM
 *
 * Project: csci205_final_project
 * Package: Managers
 * Class: AssetManagerTest
 *
 * Description:
 * This file will test the asset manager class
 *
 * ****************************************
 */
package org.five_nights_at_dana.Managers;

import javafx.embed.swing.JFXPanel;
import org.five_nights_at_dana.Rendering.Camera.CameraConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AssetManager.
 * Note: Requires javafx-swing dependency in build.gradle.
 */
public class AssetManagerTest {

    @BeforeAll
    public static void initJFX() {
        // Initialize JavaFX environment to prevent "Toolkit not initialized" errors
        new JFXPanel();
        AssetManager.preloadAll();
    }

    /**
     * This test will test that all the cameras are loaded
     */
    @Test
    public void testPreloadLoadsAllCameras() {
        // Verify every camera ID defined in Config was loaded into the AssetManager
        for (String id : CameraConfig.CAMERA_IDS) {
            assertNotNull(AssetManager.getImage(id),
                    "Camera ID " + id + " failed to load from AssetManager.");
        }
    }

    /**
     * This will test that the fall back of the images operates
     * correctly
     */
    @Test
    public void testGetImage_FallbackBehavior() {
        // Test that requesting a non-existent key returns the error image
        // instead of null (ensuring game stability)
        assertNotNull(AssetManager.getImage("INVALID_KEY_999"),
                "AssetManager should return cam_error, not null, for missing keys.");
    }

    /**
     * This will test the error image
     */
    @Test
    public void testErrorImageExists() {
        // Ensure the fallback itself is actually loaded
        assertNotNull(AssetManager.getImage("cam_error"),
                "The error fallback image (cam_error) was not loaded.");
    }

    /**
     * This will test the confused image character
     */
    @Test
    public void testCharacterConfusedImage() {
        //Testing that the confused image was loaded
        String characters = "char_confused";
            assertNotNull(AssetManager.getImage(characters));
    }

    /**
     * This will test the eager image character
     */
    @Test
    public void testCharacterEagerImage() {
        //Testing that the eager image was loaded
        String characters = "char_eager";
        assertNotNull(AssetManager.getImage(characters));
    }

    /**
     * This will test the persistent image character
     */
    @Test
    public void testCharacterPersistentImage() {
        //Testing that the persistent image was loaded
        String characters = "char_persistent";
        assertNotNull(AssetManager.getImage(characters));
    }

    /**
     * This will test the runner image character
     */
    @Test
    public void testCharacterRunnerImage() {
        //Testing that the runner image was loaded
        String characters = "char_runner";
        assertNotNull(AssetManager.getImage(characters));
    }

    /**
     * This will test the shy image character
     */
    @Test
    public void testCharacterShyImage() {
        //Testing that the shy image was loaded
        String characters = "char_shy";
        assertNotNull(AssetManager.getImage(characters));
    }

    /**
     * This test will check to see that a missing sound returns nothing
     */
    @Test
    public void testingMissingSound() {
        assertNull(AssetManager.getSound("INVALID_SOUND_999"),
                "getSound() should return null for unknown keys, not throw.");
    }



}
