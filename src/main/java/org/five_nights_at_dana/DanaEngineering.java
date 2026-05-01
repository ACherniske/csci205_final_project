package org.five_nights_at_dana;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.five_nights_at_dana.Managers.AssetManager;

/**
 * Main entry point for Five Nights At Dana.
 * Loads MainMenu.fxml as the initial scene.
 */
public class DanaEngineering extends Application {

    /**
     * JavaFX application entry point.
     * Preloads assets and loads the main menu scene.
     *
     * @param stage primary stage
     * @throws Exception if FXML loading fails
     */
    @Override
    public void start(Stage stage) throws Exception {
        AssetManager.preloadAll();

        Parent root = FXMLLoader.load(
                getClass().getResource("/org/five_nights_at_dana/MainMenu.fxml"));

        stage.setScene(new Scene(root, 1280, 720));
        stage.setTitle("Five Nights At Dana");
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command line args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
