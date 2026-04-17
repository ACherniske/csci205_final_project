/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 4:07 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Rendering.Camera
 * Class: CameraSystem
 *
 * Description:
 *      Handles camera feeds and rendering.
 *
 * ****************************************
 */

package org.five_nights_at_dana.Rendering.Camera;

import javafx.scene.canvas.GraphicsContext;
import org.five_nights_at_dana.Core.GamePane;

/**
 * Handles camera feeds and rendering.
 */
public class CameraSystem {

    private GamePane gamePaneReference;

    public CameraSystem(GamePane pane) {
        this.gamePaneReference = pane;
    }

    /** Updates camera logic. */
    public void update() {
        // TODO handle switching/static
    }

    /**
     * Renders camera feed.
     */
    public void render(GraphicsContext gc) {
        // TODO draw feed + overlay
    }

    /**
     * Sets active camera.
     */
    public void setCamera(String cam) {
        // TODO validate and switch
    }

    /**
     * @return current camera
     */
    public String getCurrentCamera() {
        // TODO return camera id
        return null;
    }

    /** Resets system. */
    public void reset() {
        // TODO reset state
    }
}