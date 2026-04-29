/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 4:12 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Managers
 * Class: AssetManager
 *
 * Description:
 *
 * ****************************************
 */

package org.five_nights_at_dana.Managers;

import org.five_nights_at_dana.Rendering.Camera.CameraConfig;

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AssetManager {

    private static final Map<String, Image> images = new HashMap<>();
    private static final Map<String, AudioClip> sounds = new HashMap<>();

    // Base paths inside your resources directory
    private static final String IMAGE_PATH = "/assets/images/";
    private static final String SOUND_PATH = "/assets/sounds/";

    /**
     * Preloads all required assets.
     * Call this at application launch
     */
    public static void preloadAll() {
        System.out.println("AssetManager: Preloading assets...");

        // Load the Error Image first (so it's ready for fallbacks)
        loadImage("cam_error", "cam_error.png");

        // Automate loading of all 22 Camera feeds from CameraConfig
        for (String id : CameraConfig.CAMERA_IDS) {
            CameraConfig.CameraEntry entry = CameraConfig.getCamera(id);
            if (entry != null) {
                loadImage(id, entry.imageFilename());
            }
        }

        // Load other static assets
        //loadImage("static_overlay", "static_overlay.png");
        // NEW: Add character assets
        loadImage("char_confused", "character_confused.png");
        loadImage("char_eager", "character_eager.png");
        loadImage("char_persistent", "character_persistent.png");
        loadImage("char_runner", "character_runner.png");
        loadImage("char_shy", "character_shy.png");

        // Load Sounds
        //loadSound("camera_switch", "camera_switch.wav");
        // loadSound("jumpscare", "jumpscare.mp3");

        System.out.println("AssetManager: Preload complete. Images: " + images.size() + " | Sounds: " + sounds.size());
    }

    /**
     * Retrieves an image. Falls back to a "cam_error" image if key not found.
     */
    public static Image getImage(String key) {
        if (!images.containsKey(key)) {
            System.err.println("AssetManager: Warning - Image key not found: " + key);
            return images.getOrDefault("cam_error", null);
        }
        return images.get(key);
    }

    public static AudioClip getSound(String key) {
        return sounds.get(key);
    }

    private static void loadImage(String key, String fileName) {
        try {
            Image img = new Image(Objects.requireNonNull(AssetManager.class.getResourceAsStream(IMAGE_PATH + fileName)));
            images.put(key, img);
        } catch (Exception e) {
            System.err.println("AssetManager: Failed to load image: " + fileName);
        }
    }

    private static void loadSound(String key, String fileName) {
        try {
            AudioClip clip = new AudioClip(Objects.requireNonNull(AssetManager.class.getResource(SOUND_PATH + fileName)).toString());
            sounds.put(key, clip);
        } catch (Exception e) {
            System.err.println("AssetManager: Failed to load sound: " + fileName);
        }
    }
}
