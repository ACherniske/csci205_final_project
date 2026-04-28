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

import org.five_nights_at_dana.Managers.Notification;
import org.five_nights_at_dana.Managers.NotificationManager;

import java.util.HashMap;
import java.util.Map;

public class SensorHelper {

    private static final Map<String, Integer> lastTriggerFrame = new HashMap<>();
    private static final int COOLDOWN = 60;

    // ===== WITH COOLDOWN (for sensors, spammy events) =====
    public static void trigger(String key, String message,
                               Notification.Type type, int currentFrame) {

        int last = lastTriggerFrame.getOrDefault(key, -COOLDOWN);

        if (currentFrame - last < COOLDOWN) return;

        lastTriggerFrame.put(key, currentFrame);

        NotificationManager.push(message, type);
    }

    // ===== NO COOLDOWN (for important events) =====
    public static void emit(String message, Notification.Type type) {
        NotificationManager.push(message, type);
    }

    public static void reset() {
        lastTriggerFrame.clear();
    }
}
