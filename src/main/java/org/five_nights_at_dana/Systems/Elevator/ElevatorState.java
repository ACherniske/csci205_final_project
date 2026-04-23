/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/17/2026
 * Time: 4:42 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Systems.Elevator
 * Enum: ElevatorState
 *
 * Description:
 *      Represents elevator states.
 *
 * ****************************************
 */

package org.five_nights_at_dana.Systems.Elevator;

/**
 * Represents elevator states.
 */
public enum ElevatorState {
    IDLE,
    MOVING_UP,
    MOVING_DOWN,
    STOPPED_EMERGENCY,
    DOORS_OPENING,
}
