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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import org.five_nights_at_dana.Rendering.Camera.CameraConfig;

/**
 * Asset manager to provide all images and sounds for the game views
 */
public class AssetManager {

    private static final Map<String, Image> IMAGES = new HashMap<>();
    private static final Map<String, AudioClip> SOUNDS = new HashMap<>();

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
        loadImage("char_instant", "character_instant.png");
        loadImage("char_persistent", "character_persistent.png");
        loadImage("char_runner", "character_runner.png");
        loadImage("char_shy", "character_shy.png");

        // Load Sounds
        loadSound("door_bang", "door_bang.m4a");
        loadSound("door_handle", "door_handle.m4a");
        loadSound("elevator_alert", "elevator_alert.mp3");
        loadSound("elevator_ambient", "elevator_ambient.m4a");
        loadSound("elevator_call", "elevator_call.m4a");
        loadSound("elevator_close", "elevator_close.m4a");
        loadSound("elevator_door_open", "elevator_door_open.m4a");
        loadSound("elevator_open", "elevator_open.m4a");
        loadSound("elevator_ridedown", "elevator_ridedown.m4a");
        loadSound("elevator_rideup", "elevator_rideup.m4a");
        loadSound("hallway_ambient", "hallway_ambient.m4a");
        loadSound("hallway_fast", "hallway_fast.mp3");
        loadSound("hallway_slow", "hallway_slow.m4a");
        loadSound("heavy_stairs", "heavy_stairs.m4a");
        loadSound("keyboard_noise", "keyboard_noise.m4a");
        loadSound("light_switch", "light_switch.mp3");
        loadSound("main_entrance_enter", "main_entrance_enter.m4a");
        loadSound("metallic_stairs", "metallic_stairs.m4a");
        loadSound("phone_call", "PhoneCall.wav");
        loadSound("staircase_door", "staircase_door.m4a");
        loadSound("stairs_ambient", "stairs_ambient.m4a");
        loadSound("stairs_up_n_down", "stairs_up_n_down.m4a");
        loadSound("vent_enter", "vent_enter.mp3");
        loadSound("vent_exit", "vent_exit.mp3");
        loadSound("vent_seal", "vent_seal.mp3");
        loadSound("discord_notification", "discord_notification.mp3");
        loadSound("change_cams", "change_cams.mp3");
        loadSound("stairs_up", "stairs_up.m4a");
        loadSound("door_lock", "door_lock.mp3");
        loadSound("yippie", "yippie.mp3");
        loadSound("hooray", "hooray.mp3");
        loadSound("jumpscare", "jumpscare.mp3");
        loadSound("menu", "menu.mp3");
        loadSound("vent_unseal", "vent_unseal.mp3");




        System.out.println("AssetManager: Preload complete. Images: "
                + IMAGES.size()
                + " | Sounds: "
                + SOUNDS.size());
    }

    /**
     * Retrieves an image. Falls back to a "cam_error" image if key not found.
     */
    public static Image getImage(String key) {
        if (!IMAGES.containsKey(key)) {
            System.err.println("AssetManager: Warning - Image key not found: " + key);
            return IMAGES.getOrDefault("cam_error", null);
        }
        return IMAGES.get(key);
    }

    /**
     * Retrieves a preloaded sound effect.
     *
     * @param key sound key
     * @return audio clip, or null if not present
     */
    public static AudioClip getSound(String key) {
        return SOUNDS.get(key);
    }

    /**
     * Loads an image resource into the cache.
     *
     * @param key      lookup key
     * @param fileName filename relative to {@link #IMAGE_PATH}
     */
    private static void loadImage(String key, String fileName) {
        try {
            Image img = new Image(Objects.requireNonNull(AssetManager.class.getResourceAsStream(
                    IMAGE_PATH + fileName))
            );
            IMAGES.put(key, img);
        } catch (Exception e) {
            System.err.println("AssetManager: Failed to load image: " + fileName);
        }
    }

    /**
     * Loads a sound resource into the cache.
     *
     * @param key      lookup key
     * @param fileName filename relative to {@link #SOUND_PATH}
     */
    private static void loadSound(String key, String fileName) {
        try {
            AudioClip clip = new AudioClip(Objects.requireNonNull(AssetManager.class.getResource(
                    SOUND_PATH + fileName)).toString()
            );
            SOUNDS.put(key, clip);
        } catch (Exception e) {
            System.err.println("AssetManager: Failed to load sound: " + fileName);
        }
    }
}
