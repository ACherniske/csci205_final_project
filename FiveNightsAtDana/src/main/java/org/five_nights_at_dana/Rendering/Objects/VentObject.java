/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 4:37 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Rendering.Objects
 * Class: VentObject
 *
 * Description:
 *
 * ****************************************
 */

package org.five_nights_at_dana.Rendering.Objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.five_nights_at_dana.Rendering.Office.InteractiveObject;

public class VentObject extends InteractiveObject {

    private boolean sealed;
    private Image ventOpen;
    private Image ventSealed;

    public VentObject(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    @Override
    public void interact() {
        sealed = !sealed;
        // TODO connect to VentSystem
    }

    @Override
    public void render(GraphicsContext gc, int offsetX, int offsetY) {
        // TODO draw correct vent state
    }
}