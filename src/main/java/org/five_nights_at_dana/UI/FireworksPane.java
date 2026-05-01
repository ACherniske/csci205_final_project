/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.UI
 * Class: FireworksPane
 *
 * Description:
 * Lightweight fireworks particle effect for the win celebration.
 * ****************************************
 */

package org.five_nights_at_dana.UI;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Random;

/**
 * Simple particle-based fireworks animation.
 */
public class FireworksPane extends Pane {

    private final Random random = new Random();
    private Timeline launcher;

    public FireworksPane() {
        setPickOnBounds(false);
        setMouseTransparent(true);
    }

    /**
     * Starts launching random bursts for the given duration.
     */
    public void start(Duration duration, Runnable onFinished) {
        stop();

        launcher = new Timeline(new KeyFrame(Duration.millis(220), e -> spawnBurst()));
        launcher.setCycleCount(Timeline.INDEFINITE);
        launcher.play();

        PauseTransition stopAfter = new PauseTransition(duration);
        stopAfter.setOnFinished(e -> {
            stop();
            if (onFinished != null) onFinished.run();
        });
        stopAfter.play();
    }

    public void stop() {
        if (launcher != null) {
            launcher.stop();
            launcher = null;
        }
    }

    private void spawnBurst() {
        double w = getWidth();
        double h = getHeight();
        if (w <= 10 || h <= 10) {
            // Layout not ready yet.
            return;
        }

        double x = lerp(w * 0.12, w * 0.88, random.nextDouble());
        double y = lerp(h * 0.20, h * 0.70, random.nextDouble());

        Color[] palette = new Color[] {
                Color.web("#ff3b30"),
                Color.web("#ffcc00"),
                Color.web("#34c759"),
                Color.web("#00c7ff"),
                Color.web("#af52de"),
                Color.web("#ffffff")
        };

        int particles = 20 + random.nextInt(18);
        for (int i = 0; i < particles; i++) {
            Circle p = new Circle(1.6 + random.nextDouble() * 2.6);
            p.setFill(palette[random.nextInt(palette.length)]);
            p.setLayoutX(x);
            p.setLayoutY(y);
            getChildren().add(p);

            double angle = random.nextDouble() * Math.PI * 2.0;
            double speed = 120 + random.nextDouble() * 260;
            double dx = Math.cos(angle) * speed;
            double dy = Math.sin(angle) * speed;

            Duration t = Duration.millis(700 + random.nextInt(650));

            TranslateTransition move = new TranslateTransition(t, p);
            move.setByX(dx);
            // Add slight downward bias so bursts arc.
            move.setByY(dy + 120);

            FadeTransition fade = new FadeTransition(t, p);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);

            ParallelTransition pt = new ParallelTransition(move, fade);
            pt.setOnFinished(e -> getChildren().remove(p));
            pt.play();
        }
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}
