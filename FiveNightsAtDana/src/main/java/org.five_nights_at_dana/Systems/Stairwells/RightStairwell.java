/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 4:43 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Systems.Stairwells
 * Class: RightStairwell
 *
 * Description:
 *      Right stairwell system.
 *      Tracks student movement via sensors.
 * ****************************************
 */

package org.five_nights_at_dana.Systems.Stairwells;

import org.five_nights_at_dana.AI.Student;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RightStairwell {

    private List<Student> studentsInStairwell;
    private Map<Sensor, Integer> lastTriggerTime;

    public RightStairwell() {
        studentsInStairwell = new ArrayList<>();
        lastTriggerTime = new HashMap<>();
    }

    /**
     * Updates sensor logic.
     */
    public void update() {
        // TODO process sensor cooldowns
    }

    /**
     * Called when a student passes a sensor.
     *
     * @param student student triggering
     * @param sensor sensor used
     */
    public void studentPassed(Student student, Sensor sensor) {
        // TODO record trigger time
    }

    /**
     * Resets system.
     */
    public void reset() {
        studentsInStairwell.clear();
        lastTriggerTime.clear();
    }
}
