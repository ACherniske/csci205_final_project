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

import org.five_nights_at_dana.Managers.AssetManager; // ADDED
import org.five_nights_at_dana.Managers.StudentManager;
import javafx.scene.image.Image; // ADDED

public class CameraSystem {

    private String currentCameraId = "1A";
    private final StudentManager studentManager;

    public CameraSystem(StudentManager studentManager) {
        this.studentManager = studentManager;
    }

    public void setActiveCamera(String cameraId) {
        if (CameraConfig.isValidCamera(cameraId)) {
            this.currentCameraId = cameraId;
            // You can now easily trigger the sound here using your AssetManager
            // AssetManager.getSound("camera_switch").play();
        }
    }

    /**
     * New method: Returns the visual feed directly.
     * The UI Controller just calls system.getFeedImage() to update the screen.
     */
    public Image getFeedImage() {
        return AssetManager.getImage(currentCameraId);
    }

    public CameraConfig.CameraEntry getActiveCamera() {
        return CameraConfig.getCamera(currentCameraId);
    }

    public boolean isStudentVisible() {
        return !studentManager.getStudentsAt(CameraConfig.getLocation(currentCameraId)).isEmpty();
    }
}
