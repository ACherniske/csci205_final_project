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

    @Test
    public void testPreloadLoadsAllCameras() {
        // Verify every camera ID defined in Config was loaded into the AssetManager
        for (String id : CameraConfig.CAMERA_IDS) {
            assertNotNull(AssetManager.getImage(id),
                    "Camera ID " + id + " failed to load from AssetManager.");
        }
    }

    @Test
    public void testGetImage_FallbackBehavior() {
        // Test that requesting a non-existent key returns the error image
        // instead of null (ensuring game stability)
        assertNotNull(AssetManager.getImage("INVALID_KEY_999"),
                "AssetManager should return cam_error, not null, for missing keys.");
    }

    @Test
    public void testErrorImageExists() {
        // Ensure the fallback itself is actually loaded
        assertNotNull(AssetManager.getImage("cam_error"),
                "The error fallback image (cam_error) was not loaded.");
    }
}
