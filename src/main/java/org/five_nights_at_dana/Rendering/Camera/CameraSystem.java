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

import java.util.List;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import org.five_nights_at_dana.AI.Student;
import org.five_nights_at_dana.Managers.AssetManager;
import org.five_nights_at_dana.Managers.StudentManager;

/**
 * Camera system using student manager to provide information about visibilty of students
 */
public class CameraSystem {

    private static final double CHARACTER_SPRITE_MAX_SIZE = 260.0;
    private static final String INSTANT_CAMERA_ID = "1D";

    private String currentCameraId = "1A";
    private final StudentManager studentManager;

    /**
     * Constructs a camera system that uses the given
     * student manager for visibility/occupancy queries.
     *
     * @param studentManager student manager to query for occupants
     */
    public CameraSystem(StudentManager studentManager) {
        this.studentManager = studentManager;
    }

    /**
     * Sets which camera is active on the tablet UI.
     * Invalid IDs are ignored.
     *
     * @param cameraId camera ID from {@link CameraConfig}
     */
    public void setActiveCamera(String cameraId) {
        if (CameraConfig.isValidCamera(cameraId)) {
            this.currentCameraId = cameraId;
            // You can now easily trigger the sound here using your AssetManager
            // AssetManager.getSound("camera_switch").play();
        }
    }

    /**
     * Updated: Dynamically determines if an "empty" or "student-present"
     * image should be returned based on StudentManager state.
     */
    public Image getFeedImage() {
        return getFeedImage(false);
    }

    /**
     * Renders the active camera feed, optionally overlaying the rare instant sprite.
     *
     * @param showInstantSprite true when the rare Maker-E event should be visible
     * @return rendered camera feed image
     */
    public Image getFeedImage(boolean showInstantSprite) {
        String roomId = currentCameraId;
        var location = CameraConfig.getLocation(roomId);
        List<Student> occupants = studentManager.getStudentsAt(location);
        boolean renderInstantSprite = showInstantSprite && INSTANT_CAMERA_ID.equals(roomId);
        boolean hasOccupants = occupants != null && !occupants.isEmpty();

        // Get the base room image
        Image roomImage = AssetManager.getImage(roomId);

        // If no students and no rare instant event, just return the room
        if (!renderInstantSprite && !hasOccupants) {
            return roomImage;
        }

        // If students are present, overlay them
        // We use a Canvas to draw the background, then the sprite
        Canvas canvas = new Canvas(roomImage.getWidth(), roomImage.getHeight());
        var gc = canvas.getGraphicsContext2D();

        // Draw the room
        gc.drawImage(roomImage, 0, 0);

        // Draw the student sprite (assuming first student for simplicity)
        if (hasOccupants) {
            String personality = occupants.get(0).getPersonality().name().toLowerCase();
            Image sprite = AssetManager.getImage("char_" + personality);

            if (sprite != null) {
                double spriteRatio = sprite.getWidth() / sprite.getHeight();
                double spriteWidth = CHARACTER_SPRITE_MAX_SIZE;
                double spriteHeight = CHARACTER_SPRITE_MAX_SIZE;

                if (spriteRatio >= 1.0) {
                    spriteHeight = spriteWidth / spriteRatio;
                } else {
                    spriteWidth = spriteHeight * spriteRatio;
                }

                double spriteX = (roomImage.getWidth() - spriteWidth) / 2.0;
                double spriteY = (roomImage.getHeight() - spriteHeight) / 2.0;

                if (spriteX < 0) {
                    spriteX = 0;
                }

                if (spriteY < 0) {
                    spriteY = 0;
                }

                gc.drawImage(sprite, spriteX, spriteY, spriteWidth, spriteHeight);
            }
        }

        if (renderInstantSprite) {
            Image instantSprite = AssetManager.getImage("char_instant");

            if (instantSprite != null) {
                double spriteRatio = instantSprite.getWidth() / instantSprite.getHeight();
                double spriteWidth = CHARACTER_SPRITE_MAX_SIZE;
                double spriteHeight = CHARACTER_SPRITE_MAX_SIZE;

                if (spriteRatio >= 1.0) {
                    spriteHeight = spriteWidth / spriteRatio;
                } else {
                    spriteWidth = spriteHeight * spriteRatio;
                }

                double spriteX = (roomImage.getWidth() - spriteWidth) / 2.0;
                double spriteY = (roomImage.getHeight() - spriteHeight) / 2.0;

                if (spriteX < 0) {
                    spriteX = 0;
                }

                if (spriteY < 0) {
                    spriteY = 0;
                }

                gc.drawImage(instantSprite, spriteX, spriteY, spriteWidth, spriteHeight);
            }
        }

        // Snapshot the canvas into an Image object
        return canvas.snapshot(new SnapshotParameters(), null);
    }

    /**
     * Gets the active camera entry metadata.
     *
     * @return active camera entry, or null if the current ID is unknown
     */
    public CameraConfig.CameraEntry getActiveCamera() {
        return CameraConfig.getCamera(currentCameraId);
    }

    /**
     * Checks whether any student is currently visible on the active camera.
     *
     * @return true if at least one student occupies the active camera's location
     */
    public boolean isStudentVisible() {
        return !studentManager.getStudentsAt(CameraConfig.getLocation(currentCameraId)).isEmpty();
    }
}
