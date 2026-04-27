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
        STAIR_SENSOR,
        RUNNER_CHARGING,
        RUNNER_SPRINTING,
        SYSTEM,
        WARNING
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
}
