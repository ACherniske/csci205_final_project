/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 3:35 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Core
 * Class: GamePane
 *
 * Description:
 * Canvas-based rendering layer for dynamic game content.
 * Sits transparently over the FXML background/HUD and draws
 * per-frame visuals (door indicators, overlays, etc.).
 * All logic updates are driven by GameSession.
 * ****************************************
 */

package org.five_nights_at_dana.Core;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.five_nights_at_dana.AI.Student;

/**
 * Game pane for rendering the graphics
 */
public class GamePane extends Pane {

    private static final double WIDTH  = 1280;
    private static final double HEIGHT = 720;

    private final GraphicsContext gc;

    /**
     * Constructs the transparent rendering pane and its backing canvas.
     * The pane is mouse-transparent so underlying FXML controls remain clickable.
     */
    public GamePane() {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        gc     = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
        // Let mouse events fall through to the FXML buttons beneath
        setMouseTransparent(true);
    }

    /** Renders one frame based on the current GameSession state. */
    public void render() {
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        GameSession session = GameSession.getInstance();
        switch (session.getCurrentState()) {
            case PLAYING        -> renderOffice(session);
            case POWER_OUT      -> renderPowerOut();
            default             -> {}
        }
    }

    // ── Per-state renderers ───────────────────────────────────────────

    /**
     * Renders the office overlay for danger cues (e.g., student at door).
     *
     * @param session active game session
     */
    private void renderOffice(GameSession session) {
        Student atDoor = session.getStudentManager().getStudentAtDoor();
        if (atDoor != null) {
            // Subtle red tint signals imminent danger at the door
            gc.setFill(Color.color(1.0, 0.0, 0.0, 0.18));
            gc.fillRect(0, 0, WIDTH, HEIGHT);
        }
    }

    /**
     * Renders the power-out state as a full black screen.
     */
    private void renderPowerOut() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
    }
}
