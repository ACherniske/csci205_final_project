package org.five_nights_at_dana.AI;

import org.five_nights_at_dana.AI.Pathing.PathPoint;

public class Player {
    private boolean isDoorClosed;
    private boolean isLookingAtCam;
    private PathPoint currentCamRoom;

    public Player() {
        this.isDoorClosed = false;
        this.isLookingAtCam = false;
        this.currentCamRoom = null;
    }

    public boolean isLookingAtCam() {
        return this.isLookingAtCam;
    }

    public void setLookingAtCam(boolean lookingAtCam) {
        this.isLookingAtCam = lookingAtCam;
        if (!lookingAtCam) {
            this.currentCamRoom = null;
        }
    }

    public boolean isDoorClosed() {
        return this.isDoorClosed;
    }

    public void setDoorClosed(boolean doorClosed) {
        this.isDoorClosed = doorClosed;
    }

    public PathPoint getCurrentCameraRoom() {
        return this.currentCamRoom;
    }

    public void setCurrentCamRoom(PathPoint room) {
        this.currentCamRoom = room;
    }
}
