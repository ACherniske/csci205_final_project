package org.five_nights_at_dana.Managers;

import org.five_nights_at_dana.AI.Location;

/**
 * Global tracking for whether the player is currently viewing the camera tablet,
 * and which in-world {@link Location} is being watched.
 *
 * <p>Used to implement FNAF-style "stall while watched" AI behavior without
 * threading UI/controller dependencies through the AI update loop.
 */
public final class ObservationManager {

    private static volatile boolean camerasUp = false;
    private static volatile Location watchedLocation = null;

    private ObservationManager() {
    }

    /**
     * Check if the camera screen is up in order to determine watched location
     * @param up whether or not the cameraview is up
     */
    public static void setCamerasUp(boolean up) {
        camerasUp = up;
        if (!up) {
            watchedLocation = null;
        }
    }

    /**
     * Boolean to return if the cameras are up
     * @return true if cameras are up, false otherwise
     */
    public static boolean areCamerasUp() {
        return camerasUp;
    }

    public static void setWatchedLocation(Location location) {
        watchedLocation = location;
    }

    /**
     * Get the location being watched
     * @return Location player is watching
     */
    public static Location getWatchedLocation() {
        return watchedLocation;
    }

    /**
     * Boolean that returns if the player is watching a location
     * @return true iff cameras are up and the given location is the active camera location.
     */
    public static boolean isWatching(Location location) {
        if (!camerasUp) {
            return false;
        }
        return location != null && location == watchedLocation;
    }
}
