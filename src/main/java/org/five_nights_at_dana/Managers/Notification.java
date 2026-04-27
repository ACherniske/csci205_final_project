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
        RUNNER_CHARGING,
        RUNNER_SPRINTING,
        SYSTEM
    }

    private final String message;
    private final Type type;
    private final int timestamp;

    public Notification(String message, Type type, int timestamp) {
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getMessage() { return message; }
    public Type getType() { return type; }
    public int getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + type + ": " + message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification that)) return false;

        return timestamp == that.timestamp &&
                message.equals(that.message) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        int result = message.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + timestamp;
        return result;
    }
}
