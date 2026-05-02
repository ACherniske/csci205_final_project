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

public class Notification {

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

    private final String message;
    private final Type type;
    private final int timestamp;

    /**
     * Constructs a notification.
     *
     * @param message   user-facing message text
     * @param type      notification type
     * @param timestamp simulation frame timestamp
     */
    public Notification(String message, Type type, int timestamp) {
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
    }

    /**
     * Gets the message text.
     *
     * @return message
     */
    public String getMessage() { return message; }

    /**
     * Gets the notification type.
     *
     * @return type
     */
    public Type getType() { return type; }

    /**
     * Gets the timestamp (frame count) at which the notification was created.
     *
     * @return timestamp
     */
    public int getTimestamp() { return timestamp; }

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
        if (this == o) return true;
        if (!(o instanceof Notification that)) return false;

        return timestamp == that.timestamp &&
                message.equals(that.message) &&
                type == that.type;
    }

    /**
     * Computes a hash code consistent with {@link #equals(Object)}.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = message.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + timestamp;
        return result;
    }
}
