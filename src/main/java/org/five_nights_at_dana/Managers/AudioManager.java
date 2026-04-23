/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 4:26 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Managers
 * Class: AudioManager
 *
 * Description:
 *      Handles playback of sound effects and music.
 *
 * ****************************************
 */

package org.five_nights_at_dana.Managers;

import javafx.scene.media.AudioClip;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {

    private static Map<String, AudioClip> soundEffects = new HashMap<>();
    private static Map<String, AudioClip> loopingSounds = new HashMap<>();

    private static double masterVolume = 1.0;
    private static double sfxVolume = 1.0;
    private static double musicVolume = 1.0;

    /**
     * Plays a sound effect.
     * @param key sound key
     */
    public static void play(String key) {
        // TODO play sound with volume scaling
    }

    /**
     * Plays a sound at specified volume.
     * @param key sound key
     * @param volume volume multiplier
     */
    public static void play(String key, double volume) {
        // TODO implement custom volume playback
    }

    /**
     * Starts looping a sound.
     * @param key sound key
     */
    public static void playLoop(String key) {
        // TODO loop audio clip
    }

    /**
     * Stops looping a sound.
     * @param key sound key
     */
    public static void stopLoop(String key) {
        // TODO stop loop
    }

    /**
     * Stops all looping sounds.
     */
    public static void stopAllLoops() {
        // TODO iterate and stop all loops
    }

    /**
     * Sets master volume.
     */
    public static void setMasterVolume(double v) {
        masterVolume = v;
    }

    /**
     * Sets SFX volume.
     */
    public static void setSfxVolume(double v) {
        sfxVolume = v;
    }

    /**
     * Sets music volume.
     */
    public static void setMusicVolume(double v) {
        musicVolume = v;
    }
}
