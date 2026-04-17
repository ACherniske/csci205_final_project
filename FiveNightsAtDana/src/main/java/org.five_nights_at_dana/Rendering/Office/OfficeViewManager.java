/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 3:38 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Managers
 * Class: OfficeViewManager
 *
 * Description:
 *
 * ****************************************
 */

package org.five_nights_at_dana.Rendering.Office;

import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles office rotation and rendering.
 */
public class OfficeViewManager {

    private double viewportX;
    private double targetX;
    private AnimationState state;

    private Image backgroundStrip;
    private Image foregroundStrip;
    private List<InteractiveObject> allObjects;

    private Timeline rotationTimeline;

    private static final double BACKGROUND_PARALLAX = 0.5;
    private static final double FOREGROUND_PARALLAX = 1.0;

    public OfficeViewManager() {
        allObjects = new ArrayList<>();
        createInteractiveObjects();
    }

    public void rotateLeft() {}
    public void rotateRight() {}

    public void render(GraphicsContext gc) {
        renderParallaxView(gc);
        for (InteractiveObject obj : allObjects) {
            obj.render(gc, (int) viewportX, 0);
        }
    }

    public void update() {}

    public void handleClick(double x, double y) {}
    public void handleInput(KeyCode key) {}
    public void handleMouseMove(double x, double y) {}

    public double getViewportX() { return viewportX; }
    public AnimationState getState() { return state; }
    public boolean isRotating() { return state != AnimationState.IDLE; }

    private void startRotation() {}
    private void renderParallaxView(GraphicsContext gc) {}
    private void createInteractiveObjects() {
        allObjects.add(new DoorWithBlinds(100, 300, 100, 200));
    }
}
