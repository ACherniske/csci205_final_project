/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.UI
 * Class: NotificationToastOverlay
 *
 * Description:
 * Lightweight Discord-style toast popups for the in-game NotificationManager.
 * ****************************************
 */

package org.five_nights_at_dana.UI;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.five_nights_at_dana.Managers.Notification;
import org.five_nights_at_dana.Managers.NotificationManager;

/**
 * Installs a toast overlay into any AnchorPane root.
 * Listens to {@link NotificationManager} and renders transient popups.
 */
public final class NotificationToastOverlay {

    private static final int MAX_VISIBLE_TOASTS = 4;
    private static final Duration IN_DURATION = Duration.millis(160);
    private static final Duration HOLD_DURATION = Duration.millis(2400);
    private static final Duration OUT_DURATION = Duration.millis(220);

    private NotificationToastOverlay() {
    }

    /**
     * Adds the overlay nodes to {@code root} and registers a NotificationManager listener.
     * Safe to call multiple times; each call installs a separate overlay.
     */
    public static void install(AnchorPane root) {
        if (root == null) return;

        VBox stack = new VBox(10);
        stack.setMouseTransparent(true);
        stack.setPickOnBounds(false);
        stack.setAlignment(Pos.BOTTOM_RIGHT);

        AnchorPane.setRightAnchor(stack, 18.0);
        AnchorPane.setBottomAnchor(stack, 18.0);

        root.getChildren().add(stack);

        // Small trick so the lambda can remove itself.
        final NotificationManager.NotificationListener[] handle = new NotificationManager.NotificationListener[1];
        handle[0] = n -> {
            // Auto-unregister if this overlay is no longer attached.
            if (stack.getScene() == null) {
                NotificationManager.removeListener(handle[0]);
                return;
            }

            showToast(stack, n);
        };

        stack.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                NotificationManager.removeListener(handle[0]);
            }
        });

        NotificationManager.addListener(handle[0]);
    }

    private static void showToast(VBox stack, Notification n) {
        Node toast = buildToast(n);

        // Keep stack small
        while (stack.getChildren().size() >= MAX_VISIBLE_TOASTS) {
            stack.getChildren().remove(0);
        }

        stack.getChildren().add(toast);

        toast.setOpacity(0.0);
        toast.setTranslateX(18.0);

        if (shouldPlayDiscordSound(n)) {
            // TODO AudioManager.play("discord_notification");
            // (Audio manager is being implemented separately.)
        }

        FadeTransition fadeIn = new FadeTransition(IN_DURATION, toast);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        TranslateTransition slideIn = new TranslateTransition(IN_DURATION, toast);
        slideIn.setFromX(18.0);
        slideIn.setToX(0.0);

        PauseTransition hold = new PauseTransition(HOLD_DURATION);

        FadeTransition fadeOut = new FadeTransition(OUT_DURATION, toast);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        TranslateTransition slideOut = new TranslateTransition(OUT_DURATION, toast);
        slideOut.setFromX(0.0);
        slideOut.setToX(18.0);

        fadeIn.play();
        slideIn.play();

        hold.setOnFinished(e -> {
            fadeOut.play();
            slideOut.play();
            fadeOut.setOnFinished(done -> stack.getChildren().remove(toast));
        });
        hold.play();
    }

    private static boolean shouldPlayDiscordSound(Notification n) {
        if (n == null) return false;
        if (n.getType() == Notification.Type.STAIR_SENSOR) return true;

        String msg = n.getMessage();
        if (msg == null) return false;

        // Vent-related messages (enter/exit/seal) should pop like a Discord notification.
        return msg.contains("Vent") || msg.contains("vents");
    }

    private static Node buildToast(Notification n) {
        HBox root = new HBox(10);
        root.setAlignment(Pos.TOP_LEFT);
        root.setMaxWidth(320);
        root.setStyle(
                "-fx-background-color: rgba(43,45,49,0.94);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #1e1f22;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 10 12 10 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 18, 0.25, 0, 6);"
        );

        Circle dot = new Circle(4);
        dot.setFill(colorForType(n.getType()));

        VBox text = new VBox(2);
        Label title = new Label(titleForType(n.getType()));
        title.setStyle("-fx-text-fill: #e6e6e6; -fx-font-weight: bold; -fx-font-size: 12px;");

        Label message = new Label(n.getMessage());
        message.setWrapText(true);
        message.setMaxWidth(270);
        message.setStyle("-fx-text-fill: #c9cdd4; -fx-font-size: 12px;");

        text.getChildren().addAll(title, message);
        root.getChildren().addAll(dot, text);

        return root;
    }

    private static Color colorForType(Notification.Type t) {
        return switch (t) {
            case DANGER -> Color.web("#ED4245");
            case WARNING -> Color.web("#FEE75C");
            case STAIR_SENSOR -> Color.web("#57F287");
            case RUNNER_CHARGING, RUNNER_SPRINTING -> Color.web("#5865F2");
            case SYSTEM -> Color.web("#A78BFA");
            case INFO -> Color.web("#99AAB5");
        };
    }

    private static String titleForType(Notification.Type t) {
        return switch (t) {
            case STAIR_SENSOR -> "STAIR SENSOR";
            case RUNNER_CHARGING -> "RUNNER";
            case RUNNER_SPRINTING -> "RUNNER";
            case WARNING -> "WARNING";
            case DANGER -> "DANGER";
            case SYSTEM -> "SYSTEM";
            case INFO -> "INFO";
        };
    }
}
