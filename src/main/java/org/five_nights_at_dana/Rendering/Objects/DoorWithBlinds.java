/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 4:39 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Rendering.Objects
 * Class: DoorWithBlinds
 *
 * Description:
 *
 * ****************************************
 */

package org.five_nights_at_dana.Rendering.Objects;

import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.five_nights_at_dana.Rendering.Office.InteractiveObject;

public class DoorWithBlinds extends InteractiveObject {

    private boolean peeking;
    private Image doorClosed;
    private Image doorPeek;
    private Timeline peekTimer;

    public DoorWithBlinds(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    @Override
    public void interact() {
        peeking = !peeking;
        // TODO trigger peek animation
    }

    @Override
    public void render(GraphicsContext gc, int offsetX, int offsetY) {
        // TODO draw correct sprite
    }

    private void showStudentGlimpse() {
        // TODO brief student reveal
    }
}