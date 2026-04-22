/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 4:42 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Systems.Stairwells
 * Class: LeftStairwell
 *
 * Description:
 *      Left stairwell system.
 *      Plays music to affect student behavior.
 *
 * ****************************************
 */

package org.five_nights_at_dana.Systems.Stairwells;

import org.five_nights_at_dana.AI.Student;
import java.util.ArrayList;
import java.util.List;

public class LeftStairwell {

    private List<Student> studentsInStairwell;
    private boolean musicPlaying;

    public LeftStairwell() {
        studentsInStairwell = new ArrayList<>();
    }

    /**
     * Updates stairwell behavior.
     */
    public void update() {
        // TODO affect students if music is playing
    }

    /**
     * Toggles music state.
     */
    public void toggleMusic() {
        musicPlaying = !musicPlaying;
    }

    /**
     * @return power drain from music system
     */
    public double getPowerDrain() {
        // TODO return drain if music active
        return 0;
    }

    /**
     * Resets system.
     */
    public void reset() {
        studentsInStairwell.clear();
        musicPlaying = false;
    }
}