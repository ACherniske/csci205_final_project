/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 4:06 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Rendering.Office
 * Class: InteractiveObject
 *
 * Description:
 *      Base class for clickable objects.
 *
 * ****************************************
 */

package org.five_nights_at_dana.Rendering.Office;

import javafx.scene.canvas.GraphicsContext;

/**
 * Base class for clickable objects.
 */
public abstract class InteractiveObject {

    protected double worldX, y, width, height;
    protected boolean hovered;

    public InteractiveObject(double x, double y, double w, double h) {
        this.worldX = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    /**
     * Checks if world coordinate is inside object.
     */
    public boolean containsWorld(double mx, double my) {
        return mx >= worldX && mx <= worldX + width &&
                my >= y && my <= y + height;
    }

    public void setHovered(boolean hovered) { this.hovered = hovered; }
    public boolean isHovered() { return hovered; }
    public double getWorldX() { return worldX; }
    public double getY() { return y; }

    /**
     * Renders object.
     */
    public abstract void render(GraphicsContext gc, int offsetX, int offsetY);

    /**
     * Handles interaction.
     */
    public abstract void interact();

    /** Draws highlight. */
    protected void renderHighlight(GraphicsContext gc, int offsetX, int offsetY) {
        // TODO optional hover effect
    }
}