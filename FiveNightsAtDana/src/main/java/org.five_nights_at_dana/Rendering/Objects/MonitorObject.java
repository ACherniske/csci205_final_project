/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 4:38 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Rendering.Objects
 * Class: MonitorObject
 *
 * Description:
 *
 * ****************************************
 */

package org.five_nights_at_dana.Rendering.Objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.five_nights_at_dana.Rendering.Office.InteractiveObject;

public class MonitorObject extends InteractiveObject {

    private boolean camerasOpen;
    private Image monitorOff;
    private Image monitorOn;

    public MonitorObject(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    @Override
    public void interact() {
        camerasOpen = !camerasOpen;
        // TODO trigger GameState change
    }

    @Override
    public void render(GraphicsContext gc, int offsetX, int offsetY) {
        // TODO draw monitor sprite
    }
}