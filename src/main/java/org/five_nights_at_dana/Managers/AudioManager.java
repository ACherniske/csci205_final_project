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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.HashMap;
import java.util.Map;

public class AudioManager {

    private static Map<String, MediaPlayer> loopingSounds = new HashMap<>();

    private static double masterVolume = 1.0;
    private static double sfxVolume = 1.0;
    private static double musicVolume = 1.0;

    /**
     * Plays a sound effect.
     * @param key sound key
     * @param isSFX true if audio clip is a sfx, false if it is a music clip
     */
    public static void play(String key, boolean isSFX) {
        play(key, 1, isSFX);

    }

    /**
     * Plays a sound at specified volume.
     * @param key sound key
     * @param volume volume multiplier
     * @param isSFX true if audio clip is a sfx, false if it is a music clip
     */
    public static void play(String key, double volume, boolean isSFX) {
        try {
            AudioClip soundClip = AssetManager.getSound(key);

            if (soundClip == null) {
                System.err.println("AudioManager: Failed to load sound: " + key);
                return;
            }

            double clipVolume = 0;
            if (isSFX) {
                clipVolume = calculateVolume(volume, sfxVolume);
            } else {
                clipVolume = calculateVolume(volume, musicVolume);
            }
            soundClip.play(clipVolume);

        } catch (Exception e) {
            System.err.println("AudioManger: Failed to load sound: " + key);
        }
    }

    /**
     * Stops an audio clip from playing
     * @param key of the audio clip to be stopped
     */
    public static void stop(String key) {
        try {
            AudioClip soundClip = AssetManager.getSound(key);
            if (soundClip != null) {
                soundClip.stop();
            }
        } catch (Exception e) {
            System.err.println("AudioManger: Failed to stop sound: " + key);
        }
    }

    /**
     * Starts looping a sound.
     * @param key sound key
     * @param volume of the sound
     * @param isSFX true if audio clip is a sfx, false if it is a music clip
     */
    public static void playLoop(String key, double volume, boolean isSFX) {
        try {
            AudioClip soundClip = AssetManager.getSound(key);

            if (soundClip == null) {
                System.err.println("AudioManager: Failed to load sound: " + key);
                return;
            }

            if (loopingSounds.containsKey(key)) {
                System.err.println("AudioManager: Sound is already looping. " + key);
                return;
            }

            Media media = new Media(soundClip.getSource());
            MediaPlayer player = new MediaPlayer(media);

            double clipVolume = 0;
            if (isSFX) {
                clipVolume = calculateVolume(volume, sfxVolume);
            } else {
                clipVolume = calculateVolume(volume, musicVolume);
            }

            player.setVolume(clipVolume);
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.play();
            loopingSounds.put(key, player);
        } catch (Exception e) {
            System.err.println("AudioManger: Failed to loop sound: " + key);
        }
    }

    /**
     * Gets the volume to be played
     * @param volume local to the call
     * @param categoryVolume whether its sfx sound or a music sound
     * @return the calculated volume limited between [0, 1]
     */
    private static double calculateVolume(double volume, double categoryVolume) {
        return Math.max(0.0, Math.min(1, volume * categoryVolume * masterVolume));
    }

    /**
     * Stops looping a sound.
     * @param key sound key
     */
    public static void stopLoop(String key) {
        try {
            if (!loopingSounds.containsKey(key)) {
                System.err.println("AudioManager: Cant stop looping a sound that isn't being looped: " + key);
                return;
            }
            MediaPlayer player = loopingSounds.remove(key);
            player.stop();
        }  catch (Exception e) {
            System.err.println("AudioManager: Failed to stop looping sound: " + key);
        }
    }

    /**
     * Stops all looping sounds.
     */
    public static void stopAllLoops() {
        try {
            for (MediaPlayer player : loopingSounds.values()) {
                if (player != null) {
                    player.stop();
                }
            }
            loopingSounds.clear();
        }  catch (Exception e) {
            System.err.println("AudioManager: Failed to stop all looping sounds: " + e.getMessage());
        }
    }

    /**
     * Updates the volume of the loops for when they are changed
     */
    private static void updateLoopVolume() {
        for (Map.Entry<String, MediaPlayer> entry : loopingSounds.entrySet()) {
            MediaPlayer player = entry.getValue();
            if (player != null) {
                double volume = masterVolume * musicVolume;
                player.setVolume(volume);
            }
        }
    }

    /**
     * Sets master volume.
     */
    public static void setMasterVolume(double v) {
        masterVolume = Math.max(0.0, Math.min(1.0, v));
        updateLoopVolume();
    }

    /**
     * Sets SFX volume.
     */
    public static void setSfxVolume(double v) {
        sfxVolume = Math.max(0.0, Math.min(1.0, v));
        updateLoopVolume();
    }

    /**
     * Sets music volume.
     */
    public static void setMusicVolume(double v) {
        musicVolume = Math.max(0.0, Math.min(1.0, v));
        updateLoopVolume();
    }
}
