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

import org.five_nights_at_dana.Managers.StudentManager;
import org.five_nights_at_dana.Rendering.Camera.CameraConfig;
import org.five_nights_at_dana.Rendering.Camera.CameraConfig.CameraEntry;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages camera states, student visibility lookups, and feed switching.
 */
public class CameraSystem {

    private String currentCameraId = "1A";
    private final StudentManager studentManager;

    public CameraSystem(StudentManager studentManager) {
        this.studentManager = studentManager;
    }

    public void setActiveCamera(String cameraId) {
        if (CameraConfig.isValidCamera(cameraId)) {
            this.currentCameraId = cameraId;
            // TODO: Add audio trigger here: AudioManager.play("cam_switch");
        }
    }

    public CameraEntry getActiveCamera() {
        return CameraConfig.getCamera(currentCameraId);
    }

    /**
     * Checks if any students are currently in the location
     * monitored by the active camera.
     */
    public boolean isStudentVisible() {
        return !studentManager.getStudentsAt(CameraConfig.getLocation(currentCameraId)).isEmpty();
    }
}
