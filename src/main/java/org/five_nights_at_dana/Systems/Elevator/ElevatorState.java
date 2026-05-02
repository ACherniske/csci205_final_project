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
    FIRST_FLOOR,
    MOVING_UP,
    MOVING_DOWN,
    STOPPED_EMERGENCY,
    THIRD_FLOOR,
    DOORS_OPENING;

    /**
     * Determines whether the elevator state machine is
     * allowed to transition to the given next state.
     *
     * @param next proposed next state
     * @return true if the transition is valid
     */
    public boolean canTransitionTo(ElevatorState next) {
        return switch (this) {
            case FIRST_FLOOR -> next == MOVING_UP
                    || next == DOORS_OPENING
                    || next == STOPPED_EMERGENCY;

            case MOVING_UP -> next == STOPPED_EMERGENCY || next == THIRD_FLOOR;

            case MOVING_DOWN -> next == STOPPED_EMERGENCY || next == FIRST_FLOOR;

            case STOPPED_EMERGENCY -> next == MOVING_UP
                    || next == DOORS_OPENING
                    || next == FIRST_FLOOR;

            case THIRD_FLOOR -> next == DOORS_OPENING
                    || next == MOVING_DOWN
                    || next == STOPPED_EMERGENCY;
            
            case DOORS_OPENING -> next == FIRST_FLOOR || next == THIRD_FLOOR;
        };
    }
}
