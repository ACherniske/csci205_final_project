/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Managers
 * Class: NavigationManager
 *
 * Description:
 * Graph-based navigation system for Student AI.
 * Uses hybrid (bidirectional + directed) edges to model movement.
 *
 * ****************************************
 */

package org.five_nights_at_dana.Managers;

import org.five_nights_at_dana.AI.*;

import java.util.*;

public class NavigationManager {

    private static class Edge {
        final Location to;
        final PathType type;

        Edge(Location to, PathType type) {
            this.to = to;
            this.type = type;
        }
    }

    private static final Map<Location, List<Edge>> graph = new HashMap<>();

    private static Random rand = new Random();

    public static void setRandom(Random r) {
        rand = r;
    }

    static {
        for (Location loc : Location.values()) {
            graph.put(loc, new ArrayList<>());
        }

        // ===== FLOOR 1 =====

        // Entrance (free roaming)
        addBidirectionalEdge(Location.FLOOR1_ENTRANCE, Location.FLOOR1_HALLWAY_LEFT, PathType.NORMAL);
        addBidirectionalEdge(Location.FLOOR1_ENTRANCE, Location.FLOOR1_HALLWAY_RIGHT, PathType.NORMAL);
        addBidirectionalEdge(Location.FLOOR1_ENTRANCE, Location.FLOOR1_LOUNGE, PathType.NORMAL);
        addBidirectionalEdge(Location.FLOOR1_ENTRANCE, Location.FLOOR1_STAIR_MID, PathType.MIDDLE_STAIRS);

        // Hall Left
        addBidirectionalEdge(Location.FLOOR1_HALLWAY_LEFT, Location.FLOOR1_GARDNER, PathType.NORMAL);
        addBidirectionalEdge(Location.FLOOR1_HALLWAY_LEFT, Location.FLOOR1_MAKER_E, PathType.NORMAL);

        // Progression (one-way)
        addEdge(Location.FLOOR1_HALLWAY_LEFT, Location.FLOOR1_STAIR_LEFT, PathType.LEFT_STAIRS);

        // Progression (one-way)
        addEdge(Location.FLOOR1_HALLWAY_RIGHT, Location.FLOOR1_ELEVATOR, PathType.ELEVATOR);
        addEdge(Location.FLOOR1_HALLWAY_RIGHT, Location.FLOOR1_STAIR_RIGHT, PathType.RIGHT_STAIRS);

        // Lounge (random loop)
        addBidirectionalEdge(Location.FLOOR1_LOUNGE, Location.FLOOR1_ENTRANCE, PathType.NORMAL);
        addBidirectionalEdge(Location.FLOOR1_LOUNGE, Location.FLOOR1_STAIR_MID, PathType.MIDDLE_STAIRS);
        addBidirectionalEdge(Location.FLOOR1_LOUNGE, Location.FLOOR1_HALLWAY_RIGHT, PathType.NORMAL);
    }

    private static void addEdge(Location from, Location to, PathType type) {
        List<Edge> edges = graph.get(from);

        for (Edge e : edges) {
            if (e.to == to && e.type == type) {
                return; // prevent duplicate
            }
        }

        edges.add(new Edge(to, type));
    }

    private static void addBidirectionalEdge(Location a, Location b, PathType type) {
        addEdge(a, b, type);
        addEdge(b, a, type);
    }

    public static Location getNextLocation(Student student) {
        Location current = student.getCurrentLocation();
        List<Edge> edges = graph.get(current);

        if (edges == null || edges.isEmpty()) {
            throw new IllegalStateException("No valid transitions from " + current);
        }

        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }

        Edge chosen = selectEdge(student, edges);

        if (chosen == null || chosen.to == null) {
            throw new IllegalStateException("Navigation failed from " + current);
        }

        return chosen.to;
    }

    private static Edge selectEdge(Student student, List<Edge> edges) {
        Personality p = student.getPersonality();
        PathType preferred = student.getPreferredPath();

        // CONFUSED = random
        if (p == Personality.CONFUSED) {
            return edges.get(rand.nextInt(edges.size()));
        }

        // SHY = prioritize vent-access rooms (Gardner)
        if (p == Personality.SHY) {
            List<Edge> ventEdges = new ArrayList<>();

            for (Edge e : edges) {
                if (e.to.hasVentAccess()) {
                    ventEdges.add(e);
                }
            }

            if (!ventEdges.isEmpty()) {
                return ventEdges.get(rand.nextInt(ventEdges.size()));
            }
        }

        // Preferred path bias
        List<Edge> preferredEdges = new ArrayList<>();
        for (Edge e : edges) {
            if (e.type == preferred) {
                preferredEdges.add(e);
            }
        }

        if (!preferredEdges.isEmpty()) {
            return preferredEdges.get(rand.nextInt(preferredEdges.size()));
        }

        List<Edge> normalEdges = new ArrayList<>();

        for (Edge e : edges) {
            if (e.type == PathType.NORMAL) {
                normalEdges.add(e);
            }
        }

        if (!normalEdges.isEmpty()) {
            return normalEdges.get(rand.nextInt(normalEdges.size()));
        }

        // final fallback
        return edges.get(rand.nextInt(edges.size()));
    }

    // ===== TEST HELPERS =====

    public static List<Location> getNeighbors(Location loc) {
        List<Location> result = new ArrayList<>();
        List<Edge> edges = graph.get(loc);

        if (edges == null) return result;

        for (Edge e : edges) {
            result.add(e.to);
        }
        return result;
    }

    public static boolean isValidTransition(Location from, Location to) {
        List<Edge> edges = graph.get(from);
        if (edges == null) return false;

        for (Edge e : edges) {
            if (e.to == to) return true;
        }
        return false;
    }

    public static void validateGraph() {
        for (Location loc : Location.values()) {
            List<Edge> edges = graph.get(loc);

            if (edges == null || edges.isEmpty()) {
                System.err.println("WARNING: Dead end at " + loc);
            }
        }
    }

    public static void printGraph() {
        for (Location loc : graph.keySet()) {
            System.out.print(loc + " -> ");
            List<String> out = new ArrayList<>();
            for (Edge e : graph.get(loc)) {
                out.add(e.to + "(" + e.type + ")");
            }
            System.out.println(out);
        }
    }
}
