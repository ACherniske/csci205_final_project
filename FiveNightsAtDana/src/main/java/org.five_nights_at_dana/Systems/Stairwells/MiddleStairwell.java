/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 4:46 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Systems.Stairwells
 * Class: MiddleStairwell
 *
 * Description:
 *      Middle stairwell system.
 *      Uses limited emergency lighting charges.
 *
 * ****************************************
 */

package org.five_nights_at_dana.Systems.Stairwells;

import org.five_nights_at_dana.AI.Student;
import java.util.ArrayList;
import java.util.List;

public class MiddleStairwell {

    private List<Student> studentsInStairwell;
    private boolean emergencyLightsActive;
    private int emergencyLightCharges;
    private int emergencyLightTimer;

    public MiddleStairwell() {
        studentsInStairwell = new ArrayList<>();
        emergencyLightCharges = 3;
    }

    /**
     * Updates lighting system.
     */
    public void update() {
        // TODO handle timer and light duration
    }

    /**
     * Activates emergency lights.
     *
     * @return true if activation successful
     */
    public boolean activateEmergencyLights() {
        // TODO decrement charges and activate
        return false;
    }

    /**
     * @return remaining charges
     */
    public int getChargesRemaining() {
        return emergencyLightCharges;
    }

    /**
     * Resets system.
     */
    public void reset() {
        studentsInStairwell.clear();
        emergencyLightsActive = false;
        emergencyLightCharges = 3;
        emergencyLightTimer = 0;
    }
}
