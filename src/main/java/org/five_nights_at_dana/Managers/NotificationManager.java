/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/27/2026
 * Time: 3:31 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Managers
 * Class: NotificationManager
 *
 * Description:
 * Manages the global notification system. Tracks game events
 * via a queue and ensures memory is managed by capping the
 * total number of stored notifications.
 *
 * ****************************************
 */

package org.five_nights_at_dana.Managers;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A static manager class responsible for handling in-game notifications.
 * It maintains a fixed-size queue of {@link Notification} objects to
 * ensure the system doesn't consume excessive memory during long sessions.
 */
public class NotificationManager {

    /** Maximum number of notifications to keep in the history queue. */
    private static final int MAX_NOTIFICATIONS = 50;

    /** The internal queue used to store notifications chronologically. */
    private static final Deque<Notification> notifications = new ArrayDeque<>();

    /** Keeps track of the current frame count for timestamping notifications. */
    private static int currentFrame = 0;

    /** Optional listeners for real-time UI/audio notification reactions. */
    private static final List<NotificationListener> listeners = new CopyOnWriteArrayList<>();

    @FunctionalInterface
    public interface NotificationListener {
        /**
         * Called when a new {@link Notification} is pushed.
         *
         * @param notification the notification that was added
         */
        void onNotification(Notification notification);
    }

    /**
     * Updates the manager's internal frame clock. Should be called
     * once per game tick.
     */
    public static void update() {
        currentFrame++;
    }

    /**
     * Adds a new notification to the system. If the capacity is reached,
     * the oldest notification is removed before adding the new one.
     *
     * @param message The text content of the notification.
     * @param type    The severity/category of the notification.
     */
    public static void push(String message, Notification.Type type) {
        if (notifications.size() >= MAX_NOTIFICATIONS) {
            notifications.removeFirst();
        }

        Notification n = new Notification(message, type, currentFrame);
        notifications.addLast(n);

        // Notify listeners (UI toasts, audio, etc.). Keep it resilient in tests/headless runs.
        for (NotificationListener l : listeners) {
            try {
                l.onNotification(n);
            } catch (RuntimeException ignored) {
                // Listener failures should not break gameplay/tests.
            }
        }
    }

    /**
     * Retrieves a copy of all current notifications.
     *
     * @return A list containing all active notifications in order of arrival.
     */
    public static List<Notification> getNotifications() {
        return new ArrayList<>(notifications);
    }

    /**
     * Registers a listener to receive real-time notifications.
     *
     * @param listener callback invoked whenever a notification is pushed
     */
    public static void addListener(NotificationListener listener) {
        if (listener != null) listeners.add(listener);
    }

    /**
     * Removes a previously registered listener.
     *
     * @param listener the listener instance to remove
     */
    public static void removeListener(NotificationListener listener) {
        listeners.remove(listener);
    }

    /**
     * Convenience helper for stair sensor notifications.
     *
     * @param message the notification message
     */
    public static void stair(String message) {
        push(message, Notification.Type.STAIR_SENSOR);
    }

    /**
     * Convenience helper for the runner charging notification.
     *
     * @param message the notification message
     */
    public static void runnerCharging(String message) {
        push(message, Notification.Type.RUNNER_CHARGING);
    }

    /**
     * Convenience helper for the runner sprinting notification.
     *
     * @param message the notification message
     */
    public static void runnerSprinting(String message) {
        push(message, Notification.Type.RUNNER_SPRINTING);
    }

    /**
     * Convenience helper for warning notifications.
     *
     * @param message the notification message
     */
    public static void warning(String message) {
        push(message, Notification.Type.WARNING);
    }

    /**
     * Convenience helper for system/status notifications.
     *
     * @param message the notification message
     */
    public static void system(String message) {
        push(message, Notification.Type.SYSTEM);
    }

    /**
     * Clears all existing notifications from the queue.
     */
    public static void clear() {
        notifications.clear();
    }
}
