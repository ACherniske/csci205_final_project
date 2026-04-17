package org.five_nights_at_dana;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.five_nights_at_dana.Core.GamePane;
import org.five_nights_at_dana.Managers.AssetManager;

/**
 * Main entry point for Five Nights At Dana.
 */
public class DanaEngineering extends Application {

    private Stage primaryStage;
    private Scene scene;
    private GamePane gamePane;
    private AnimationTimer gameLoop;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        AssetManager.preloadAll();

        gamePane = new GamePane();
        scene = new Scene(gamePane, 1280, 720);

        stage.setScene(scene);
        stage.setTitle("Five Nights At Dana");
        stage.show();

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gamePane.update();
                gamePane.render();
            }
        };
        gameLoop.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
