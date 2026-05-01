/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.UI
 * Class: FadeUtil
 *
 * Description:
 * Small helpers for fade-in/fade-out transitions when switching scenes.
 * ****************************************
 */

package org.five_nights_at_dana.UI;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Utility helpers for consistent cross-scene fades.
 */
public final class FadeUtil {

    private FadeUtil() {
    }

    /** Default fade duration used by convenience overloads. */
    public static final double DEFAULT_SECONDS = 0.35;

    /**
     * Fades in a node (typically the root) from 0 → 1.
     */
    public static void fadeIn(Node node, double seconds) {
        if (node == null) return;
        node.setOpacity(0.0);

        FadeTransition ft = new FadeTransition(Duration.seconds(Math.max(0.0, seconds)), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    /**
     * Fades out a node (typically the current scene root) from 1 → 0.
     */
    public static void fadeOut(Node node, double seconds, Runnable after) {
        if (node == null) {
            if (after != null) after.run();
            return;
        }

        FadeTransition ft = new FadeTransition(Duration.seconds(Math.max(0.0, seconds)), node);
        ft.setFromValue(node.getOpacity());
        ft.setToValue(0.0);
        ft.setOnFinished(e -> {
            if (after != null) after.run();
        });
        ft.play();
    }

    /**
     * Fade out the current scene root, swap to a new scene, and fade the new root in.
     */
    public static void fadeOutAndSwitch(Node anyNodeInCurrentScene, Parent nextRoot, double width, double height, double seconds) {
        if (anyNodeInCurrentScene == null || anyNodeInCurrentScene.getScene() == null) return;
        Scene currentScene = anyNodeInCurrentScene.getScene();
        Parent currentRoot = currentScene.getRoot();
        Stage stage = (Stage) currentScene.getWindow();

        fadeOut(currentRoot, seconds, () -> {
            if (stage == null) return;
            if (nextRoot == null) return;

            nextRoot.setOpacity(0.0);
            stage.setScene(new Scene(nextRoot, width, height));
            fadeIn(nextRoot, seconds);
        });
    }

    public static void fadeOutAndSwitch(Node anyNodeInCurrentScene, Parent nextRoot, double width, double height) {
        fadeOutAndSwitch(anyNodeInCurrentScene, nextRoot, width, height, DEFAULT_SECONDS);
    }
}
