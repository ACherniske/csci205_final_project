/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/27/2026
 * Time: 3:40 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Systems
 * Class: SensorHelper
 *
 * Description: Helps add sensors to other systems
 *
 * ****************************************
 */

package org.five_nights_at_dana.Systems;

import java.util.HashMap;
import java.util.Map;
import org.five_nights_at_dana.Managers.Notification;
import org.five_nights_at_dana.Managers.NotificationManager;

/**
 * Helper to allow for the use of sensors in systems. Helps construct notification toasts
 */
public class SensorHelper {

    private static final Map<String, Integer> LAST_TRIGGER_FRAME = new HashMap<>();
    private static final int COOLDOWN = 60;

    // ===== WITH COOLDOWN (for sensors, spammy events) =====
    /**
     * Emits a notification if the given key has not fired within the cooldown window.
     *
     * @param key          unique cooldown key
     * @param message      notification message
     * @param type         notification type
     * @param currentFrame current simulation frame
     */
    public static void trigger(String key, String message,
                               Notification.Type type, int currentFrame) {

        int last = LAST_TRIGGER_FRAME.getOrDefault(key, -COOLDOWN);

        if (currentFrame - last < COOLDOWN) {
            return;
        }

        LAST_TRIGGER_FRAME.put(key, currentFrame);

        NotificationManager.push(message, type);
    }

    // ===== NO COOLDOWN (for important events) =====
    /**
     * Emits a notification immediately with no cooldown.
     *
     * @param message notification message
     * @param type    notification type
     */
    public static void emit(String message, Notification.Type type) {
        NotificationManager.push(message, type);
    }

    /**
     * Clears all stored cooldown state.
     */
    public static void reset() {
        LAST_TRIGGER_FRAME.clear();
    }
}
