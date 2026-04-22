/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 4:47 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Systems.Vent
 * Class: VentSystem
 *
 * Description:
 *      Vent system.
 *      Controls vent sealing and hidden student movement.
 *
 * ****************************************
 */

package org.five_nights_at_dana.Systems.Vent;

import org.five_nights_at_dana.AI.Student;

public class VentSystem {

    private boolean ventSealed;
    private Student studentInVent;

    private int sealTimer;
    private int sealCooldown;

    public VentSystem() {
        reset();
    }

    /**
     * Updates vent state.
     */
    public void update() {
        // TODO handle seal duration and cooldown
    }

    /**
     * Seals the vent.
     *
     * @return true if successful
     */
    public boolean sealVent() {
        // TODO check cooldown and activate
        return false;
    }

    /**
     * @return whether vent is sealed
     */
    public boolean isSealed() {
        return ventSealed;
    }

    /**
     * @return power drain
     */
    public double getPowerDrain() {
        // TODO return drain if sealed
        return 0;
    }

    /**
     * Resets system.
     */
    public void reset() {
        ventSealed = false;
        studentInVent = null;
        sealTimer = 0;
        sealCooldown = 0;
    }
}
