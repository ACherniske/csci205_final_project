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

        notifications.addLast(new Notification(message, type, currentFrame));
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
     * Clears all existing notifications from the queue.
     */
    public static void clear() {
        notifications.clear();
    }
}
