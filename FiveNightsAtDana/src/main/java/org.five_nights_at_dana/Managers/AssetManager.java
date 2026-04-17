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

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {

    private static Map<String, Image> images = new HashMap<>();
    private static Map<String, AudioClip> sounds = new HashMap<>();

    /**
     * Preloads all assets.
     */
    public static void preloadAll() {
        // TODO load all assets into maps
    }

    /**
     * Retrieves an image.
     * @param key asset key
     * @return Image or null
     */
    public static Image getImage(String key) {
        return images.get(key);
    }

    /**
     * Retrieves a sound.
     * @param key asset key
     * @return AudioClip or null
     */
    public static AudioClip getSound(String key) {
        return sounds.get(key);
    }

    /**
     * Loads an image from disk.
     * @param path file path
     * @return loaded Image
     */
    private static Image loadImage(String path) {
        // TODO implement file loading
        return null;
    }

    /**
     * Loads a sound from disk.
     * @param path file path
     * @return loaded AudioClip
     */
    private static AudioClip loadSound(String path) {
        // TODO implement file loading
        return null;
    }
}