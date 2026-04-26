/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/26/2026
 * Time: 12:21 AM
 *
 * Project: csci205_final_project
 * Package: Managers
 * Class: NavigationManagerTest
 *
 * Description:
 *
 * ****************************************
 */


package org.five_nights_at_dana.Managers;

import org.five_nights_at_dana.AI.*;
import org.five_nights_at_dana.Managers.NavigationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for NavigationManager.
 */
public class NavigationManagerTest {

    /**
     * Lightweight test student that uses real Student logic
     * but allows controlled starting position.
     */
    private static class TestStudent extends Student {

        public TestStudent(Location start, Personality personality) {
            super("Test", "Test Question", personality);
            setLocation(start); // override default spawn
        }
    }

    @BeforeEach
    void setup() {
        NavigationManager.setRandom(new Random(42));
        Student.setRandom(new Random(42));
    }

    // ==================== GRAPH TESTS ====================

    @Test
    void testNoDeadEndsExceptOffice() {
        for (Location loc : Location.values()) {
            List<Location> neighbors = NavigationManager.getNeighbors(loc);

            if (loc == Location.IN_OFFICE) continue;

            assertFalse(neighbors.isEmpty(),
                    "Dead end found at: " + loc);
        }
    }

    @Test
    void testAllTransitionsValid() {
        for (Location from : Location.values()) {
            for (Location to : NavigationManager.getNeighbors(from)) {
                assertTrue(
                        NavigationManager.isValidTransition(from, to),
                        "Invalid transition: " + from + " -> " + to
                );
            }
        }
    }

    @Test
    void testNoUnexpectedSelfLoops() {
        for (Location loc : Location.values()) {
            for (Location neighbor : NavigationManager.getNeighbors(loc)) {
                if (loc == Location.IN_OFFICE) continue;

                assertNotEquals(loc, neighbor,
                        "Unexpected self-loop at: " + loc);
            }
        }
    }

    // ==================== MOVEMENT TESTS ====================

    @Test
    void testMovementAlwaysValid() {
        for (Personality p : Personality.values()) {
            TestStudent s = new TestStudent(Location.FLOOR1_ENTRANCE, p);

            for (int i = 0; i < 100; i++) {
                Location current = s.getCurrentLocation();
                Location next = NavigationManager.getNextLocation(s);

                assertTrue(
                        NavigationManager.isValidTransition(current, next),
                        "Invalid move: " + current + " -> " + next
                );

                s.setLocation(next);
            }
        }
    }

    @Test
    void testMultiFloorTraversal() {
        TestStudent s = new TestStudent(Location.FLOOR1_ENTRANCE, Personality.EAGER);

        boolean reachedFloor3 = false;

        for (int i = 0; i < 200; i++) {
            Location next = NavigationManager.getNextLocation(s);
            s.setLocation(next);

            if (next.name().startsWith("FLOOR3")) {
                reachedFloor3 = true;
                break;
            }
        }

        assertTrue(reachedFloor3, "Student never reached Floor 3");
    }

    // ==================== PERSONALITY TESTS ====================

    @Test
    void testConfusedIsRandom() {
        TestStudent s = new TestStudent(Location.FLOOR1_ENTRANCE, Personality.CONFUSED);

        Set<Location> visited = new HashSet<>();

        for (int i = 0; i < 50; i++) {
            Location next = NavigationManager.getNextLocation(s);
            visited.add(next);
        }

        assertTrue(visited.size() > 1, "Confused is not random enough");
    }

    @Test
    void testShyPrefersVentRooms() {
        TestStudent s = new TestStudent(Location.FLOOR1_HALLWAY_LEFT, Personality.SHY);

        boolean reachedGardner = false;

        for (int i = 0; i < 20; i++) {
            Location next = NavigationManager.getNextLocation(s);

            if (next == Location.FLOOR1_GARDNER) {
                reachedGardner = true;
                break;
            }
        }

        assertTrue(reachedGardner, "SHY did not prioritize vent room");
    }

    @Test
    void testEagerUsesElevatorEventually() {
        TestStudent s = new TestStudent(Location.FLOOR1_HALLWAY_RIGHT, Personality.EAGER);

        boolean usedElevator = false;

        for (int i = 0; i < 50; i++) {
            Location next = NavigationManager.getNextLocation(s);

            if (next == Location.IN_ELEVATOR || next == Location.FLOOR3_ELEVATOR_EXIT) {
                usedElevator = true;
                break;
            }

            s.setLocation(next);
        }

        assertTrue(usedElevator, "EAGER did not use elevator");
    }

    // ==================== SYSTEM TESTS ====================

    @Test
    void testVentSystemReachable() {
        TestStudent s = new TestStudent(Location.FLOOR1_GARDNER, Personality.SHY);

        boolean enteredVent = false;

        for (int i = 0; i < 20; i++) {
            Location next = NavigationManager.getNextLocation(s);

            if (next == Location.IN_VENT) {
                enteredVent = true;
                break;
            }
        }

        assertTrue(enteredVent, "Vent system not reachable");
    }

    @Test
    void testEventuallyReachesOffice() {
        TestStudent s = new TestStudent(Location.FLOOR1_ENTRANCE, Personality.EAGER);

        boolean reachedOffice = false;

        for (int i = 0; i < 500; i++) {
            Location next = NavigationManager.getNextLocation(s);
            s.setLocation(next);

            if (next == Location.IN_OFFICE) {
                reachedOffice = true;
                break;
            }
        }

        assertTrue(reachedOffice, "Student never reached office");
    }

    // ==================== STRESS TEST ====================

    @Test
    void testRandomWalkStability() {
        TestStudent s = new TestStudent(Location.FLOOR1_ENTRANCE, Personality.CONFUSED);

        for (int i = 0; i < 1000; i++) {
            Location next = NavigationManager.getNextLocation(s);

            assertNotNull(next, "Null location encountered");

            s.setLocation(next);
        }
    }
}
