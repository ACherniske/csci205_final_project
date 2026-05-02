/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/27/2026
 * Time: 3:33 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Managers
 * Class: Notification
 *
 * Description: Notification
 *
 * ****************************************
 */

package org.five_nights_at_dana.Managers;

/**
 * Defines notifications provided by the game for debug and player viewing
 */
public record Notification(String message, Type type, int timestamp) {

    /**
     * Notification Types
     */
    public enum Type {
        INFO,
        WARNING,
        DANGER,
        STAIR_SENSOR,
        ELEVATOR,
        RUNNER_CHARGING,
        RUNNER_SPRINTING,
        SYSTEM
    }

    /**
     * Constructs a notification.
     *
     * @param message   user-facing message text
     * @param type      notification type
     * @param timestamp simulation frame timestamp
     */
    public Notification {
    }

    /**
     * Gets the message text.
     *
     * @return message
     */
    @Override
    public String message() {
        return message;
    }

    /**
     * Gets the notification type.
     *
     * @return type
     */
    @Override
    public Type type() {
        return type;
    }

    /**
     * Gets the timestamp (frame count) at which the notification was created.
     *
     * @return timestamp
     */
    @Override
    public int timestamp() {
        return timestamp;
    }

    /**
     * Returns a readable string representation.
     *
     * @return formatted notification
     */
    @Override
    public String toString() {
        return "[" + timestamp + "] " + type + ": " + message;
    }

    /**
     * Compares notifications by message, type, and timestamp.
     *
     * @param o other object
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Notification(String message1, Type type1, int timestamp1))) {
            return false;
        }

        return timestamp == timestamp1 &&
                message.equals(message1) &&
                type == type1;
    }

}
