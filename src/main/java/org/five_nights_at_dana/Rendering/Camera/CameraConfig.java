/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/28/2026
 * Time: 4:21 PM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Rendering.Camera
 * Class: CameraConfig
 *
 * Description:
 *      Configuration registry for all camera metadata.
 *      Maps camera IDs to their display names, image files, and locations.
 *
 * ****************************************
 */

package org.five_nights_at_dana.Rendering.Camera;

import org.five_nights_at_dana.AI.Location;
import java.util.Map;

/**
 * Configuration registry for all camera metadata.
 *
 * <p> Total cameras: 22
 * - Floor 1: 9 cameras (6 rooms + 3 stairs)
 * - Floor 2: 7 cameras (4 rooms + 3 stairs)
 * - Floor 3: 6 cameras (4 rooms + 3 stairs - no hallway right cam)
 */
public class CameraConfig {

    /**
     * Camera metadata record.
     *
     * @param id Camera ID (e.g., "1A", "SL1")
     * @param label Display name shown in UI
     * @param imageFilename Image file for camera feed background
     * @param location Location this camera monitors
     * @param floor Floor number (1, 2, or 3)
     */
    public record CameraEntry(String id, String label, String imageFilename, Location location, int floor) {}

    /**
     * Complete camera registry.
     * All 22 cameras with metadata.
     */
    public static final Map<String, CameraEntry> REGISTRY = Map.ofEntries(
            // ==================== FLOOR 1 (9 cameras) ====================

            // Room cameras
            Map.entry("1A", new CameraEntry(
                    "1A",
                    "Entrance Hall",
                    "cam_1a_entrance.png",
                    Location.FLOOR1_ENTRANCE,
                    1
            )),

            Map.entry("1B", new CameraEntry(
                    "1B",
                    "Hallway Left",
                    "cam_1b_hallway_left.png",
                    Location.FLOOR1_HALLWAY_LEFT,
                    1
            )),

            Map.entry("1C", new CameraEntry(
                    "1C",
                    "Gardner Lecture Hall",
                    "cam_1c_gardner.png",
                    Location.FLOOR1_GARDNER,
                    1
            )),

            Map.entry("1D", new CameraEntry(
                    "1D",
                    "Maker-E",
                    "cam_1d_maker_e.png",
                    Location.FLOOR1_MAKER_E,
                    1
            )),

            Map.entry("1E", new CameraEntry(
                    "1E",
                    "Lounge",
                    "cam_1e_lounge.png",
                    Location.FLOOR1_LOUNGE,
                    1
            )),

            Map.entry("1F", new CameraEntry(
                    "1F",
                    "Hallway Right",
                    "cam_1f_hallway_right.png",
                    Location.FLOOR1_HALLWAY_RIGHT,
                    1
            )),

            // Stairwell cameras
            Map.entry("SL1", new CameraEntry(
                    "SL1",
                    "Left Stairs F1",
                    "cam_s_l1_stairs.png",
                    Location.FLOOR1_STAIR_LEFT,
                    1
            )),

            Map.entry("SM1", new CameraEntry(
                    "SM1",
                    "Middle Stairs F1",
                    "cam_s_m1_stairs.png",
                    Location.FLOOR1_STAIR_MID,
                    1
            )),

            Map.entry("SR1", new CameraEntry(
                    "SR1",
                    "Right Stairs F1",
                    "cam_s_r1_stairs.png",
                    Location.FLOOR1_STAIR_RIGHT,
                    1
            )),

            // ==================== FLOOR 2 (7 cameras) ====================

            // Room cameras
            Map.entry("2A", new CameraEntry(
                    "2A",
                    "Hallway Left",
                    "cam_2a_hallway_left.png",
                    Location.FLOOR2_HALLWAY_LEFT,
                    2
            )),

            Map.entry("2B", new CameraEntry(
                    "2B",
                    "Hallway Center",
                    "cam_2b_hallway_center.png",
                    Location.FLOOR2_HALLWAY_CENTER,
                    2
            )),

            Map.entry("BUCKY", new CameraEntry(
                    "BUCKY",
                    "Hallway Right (Hidden)",
                    "cam_bucky_hallway_right.png",
                    Location.FLOOR2_HALLWAY_RIGHT,
                    2
            )),

            Map.entry("2C", new CameraEntry(
                    "2C",
                    "Computer Lab",
                    "cam_2c_computer_lab.png",
                    Location.FLOOR2_COMPUTER_LAB,
                    2
            )),

            // Stairwell cameras
            Map.entry("SL2", new CameraEntry(
                    "SL2",
                    "Left Stairs F2",
                    "cam_s_l2_stairs.png",
                    Location.FLOOR2_STAIR_LEFT,
                    2
            )),

            Map.entry("SM2", new CameraEntry(
                    "SM2",
                    "Middle Stairs F2",
                    "cam_s_m2_stairs.png",
                    Location.FLOOR2_STAIR_MID,
                    2
            )),

            Map.entry("SR2", new CameraEntry(
                    "SR2",
                    "Right Stairs F2",
                    "cam_s_r2_stairs.png",
                    Location.FLOOR2_STAIR_RIGHT,
                    2
            )),

            // ==================== FLOOR 3 (6 cameras) ====================

            // Room cameras
            Map.entry("3A", new CameraEntry(
                    "3A",
                    "Testing Lab",
                    "cam_3a_testing_lab.png",
                    Location.FLOOR3_TESTING_LAB,
                    3
            )),

            Map.entry("3B", new CameraEntry(
                    "3B",
                    "Hallway Left",
                    "cam_3b_hallway_left.png",
                    Location.FLOOR3_HALLWAY_LEFT,
                    3
            )),

            Map.entry("3C", new CameraEntry(
                    "3C",
                    "Hallway Center",
                    "cam_3c_hallway_center.png",
                    Location.FLOOR3_HALLWAY_CENTER,
                    3
            )),

            Map.entry("3D", new CameraEntry(
                    "3D",
                    "Computer Lab (RUNNER)",
                    "cam_3d_computer_lab.png",
                    Location.FLOOR3_COMPUTER_LAB,
                    3
            )),

            // Stairwell cameras
            Map.entry("SL3", new CameraEntry(
                    "SL3",
                    "Left Stairs F3",
                    "cam_s_l3_stairs.png",
                    Location.FLOOR3_STAIR_LEFT,
                    3
            )),

            Map.entry("SM3", new CameraEntry(
                    "SM3",
                    "Middle Stairs F3",
                    "cam_s_m3_stairs.png",
                    Location.FLOOR3_STAIR_MID,
                    3
            )),

            Map.entry("SR3", new CameraEntry(
                    "SR3",
                    "Right Stairs F3",
                    "cam_s_r3_stairs.png",
                    Location.FLOOR3_STAIR_RIGHT,
                    3
            ))
    );

    /**
     * Array of all camera IDs in logical order.
     * Useful for camera grid layout and iteration.
     */
    public static final String[] CAMERA_IDS = {
            // Floor 1
            "1A", "1B", "1C", "1D", "1E", "1F",
            "SL1", "SM1", "SR1",
            // Floor 2
            "2A", "2B", "2C", "BUCKY",
            "SL2", "SM2", "SR2",
            // Floor 3
            "3A", "3B", "3C", "3D",
            "SL3", "SM3", "SR3"
    };

    /**
     * Camera IDs organized by floor for floor-based filtering.
     */
    public static final Map<Integer, String[]> CAMERAS_BY_FLOOR = Map.of(
            1, new String[]{"1A", "1B", "1C", "1D", "1E", "1F", "SL1", "SM1", "SR1"},
            2, new String[]{"2A", "2B", "2C", "BUCKY", "SL2", "SM2", "SR2"},
            3, new String[]{"3A", "3B", "3C", "3D", "SL3", "SM3", "SR3"}
    );

    /**
     * Gets camera entry by ID.
     *
     * @param cameraId Camera ID
     * @return CameraEntry or null if not found
     */
    public static CameraEntry getCamera(String cameraId) {
        return REGISTRY.get(cameraId);
    }

    /**
     * Gets display label for a camera.
     *
     * @param cameraId Camera ID
     * @return Display label or "Unknown Camera"
     */
    public static String getLabel(String cameraId) {
        CameraEntry entry = REGISTRY.get(cameraId);
        return entry != null ? entry.label() : "Unknown Camera";
    }

    /**
     * Gets location monitored by a camera.
     *
     * @param cameraId Camera ID
     * @return Location or null if not found
     */
    public static Location getLocation(String cameraId) {
        CameraEntry entry = REGISTRY.get(cameraId);
        return entry != null ? entry.location() : null;
    }

    /**
     * Gets floor number for a camera.
     *
     * @param cameraId Camera ID
     * @return Floor number (1, 2, or 3) or 0 if not found
     */
    public static int getFloor(String cameraId) {
        CameraEntry entry = REGISTRY.get(cameraId);
        return entry != null ? entry.floor() : 0;
    }

    /**
     * Checks if camera ID is valid.
     *
     * @param cameraId Camera ID to check
     * @return true if valid camera
     */
    public static boolean isValidCamera(String cameraId) {
        return REGISTRY.containsKey(cameraId);
    }

    /**
     * Gets total number of cameras.
     *
     * @return 22
     */
    public static int getTotalCameras() {
        return REGISTRY.size();
    }
}
