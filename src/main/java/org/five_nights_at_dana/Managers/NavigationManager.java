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
 * Includes GraphViz export for debugging pathing.
 *
 * ****************************************
 */

package org.five_nights_at_dana.Managers;

import java.util.*;
import org.five_nights_at_dana.AI.*;

/**
 * Central manager for building navigation.
 * Stores the graph structure and handles AI movement selection logic.
 */
public class NavigationManager {

    private record Edge(Location to, PathType type) {
        /**
         * Constructs an outgoing edge in the navigation graph.
         *
         * @param to   destination node
         * @param type path type for the transition
         */
        private Edge {
        }
    }

    private static final Map<Location, List<Edge>> GRAPH = new HashMap<>();
    private static Random rand = new Random();

    /**
     * Sets a custom random seed, useful for deterministic testing.
     * @param r Random instance to use.
     */
    public static void setRandom(Random r) {
        rand = r;
    }

    static {
        for (Location loc : Location.values()) {
            GRAPH.put(loc, new ArrayList<>());
        }

        // ==================== INITIALIZE GRAPH EDGES ====================
        // Floors 1 Connectivity
        addBidirectionalEdge(Location.FLOOR1_ENTRANCE,
                Location.FLOOR1_HALLWAY_LEFT,
                PathType.NORMAL);

        addBidirectionalEdge(Location.FLOOR1_ENTRANCE,
                Location.FLOOR1_HALLWAY_RIGHT,
                PathType.NORMAL);

        addBidirectionalEdge(Location.FLOOR1_ENTRANCE,
                Location.FLOOR1_LOUNGE,
                PathType.NORMAL);

        addBidirectionalEdge(Location.FLOOR1_ENTRANCE,
                Location.FLOOR1_STAIR_MID,
                PathType.MIDDLE_STAIRS);

        addBidirectionalEdge(Location.FLOOR1_HALLWAY_LEFT,
                Location.FLOOR1_GARDNER,
                PathType.NORMAL);

        addBidirectionalEdge(Location.FLOOR1_HALLWAY_LEFT,
                Location.FLOOR1_MAKER_E,
                PathType.NORMAL);

        addEdge(Location.FLOOR1_HALLWAY_LEFT,
                Location.FLOOR1_STAIR_LEFT,
                PathType.LEFT_STAIRS);

        addEdge(Location.FLOOR1_HALLWAY_RIGHT,
                Location.FLOOR1_ELEVATOR,
                PathType.ELEVATOR);

        addEdge(Location.FLOOR1_HALLWAY_RIGHT,
                Location.FLOOR1_STAIR_RIGHT,
                PathType.RIGHT_STAIRS);

        addBidirectionalEdge(Location.FLOOR1_LOUNGE,
                Location.FLOOR1_STAIR_MID,
                PathType.MIDDLE_STAIRS);

        addBidirectionalEdge(Location.FLOOR1_LOUNGE,
                Location.FLOOR1_HALLWAY_RIGHT,
                PathType.NORMAL);


        addEdge(Location.FLOOR1_STAIR_LEFT,
                Location.FLOOR2_STAIR_LEFT,
                PathType.LEFT_STAIRS);

        addEdge(Location.FLOOR1_STAIR_MID,
                Location.FLOOR2_STAIR_MID,
                PathType.MIDDLE_STAIRS);

        addEdge(Location.FLOOR1_STAIR_RIGHT,
                Location.FLOOR2_STAIR_RIGHT,
                PathType.RIGHT_STAIRS);

        // Floor 2 Connectivity
        addEdge(Location.FLOOR2_STAIR_LEFT,
                Location.FLOOR2_HALLWAY_LEFT,
                PathType.NORMAL);

        addEdge(Location.FLOOR2_STAIR_MID,
                Location.FLOOR2_HALLWAY_CENTER,
                PathType.NORMAL);

        addEdge(Location.FLOOR2_STAIR_RIGHT,
                Location.FLOOR2_HALLWAY_RIGHT,
                PathType.NORMAL);

        addBidirectionalEdge(Location.FLOOR2_HALLWAY_LEFT,
                Location.FLOOR2_COMPUTER_LAB,
                PathType.NORMAL);

        addBidirectionalEdge(Location.FLOOR2_HALLWAY_LEFT,
                Location.FLOOR2_HALLWAY_CENTER,
                PathType.NORMAL);

        addBidirectionalEdge(Location.FLOOR2_HALLWAY_RIGHT,
                Location.FLOOR2_HALLWAY_CENTER,
                PathType.NORMAL);

        addBidirectionalEdge(Location.FLOOR2_HALLWAY_CENTER,
                Location.FLOOR2_STAIR_MID,
                PathType.MIDDLE_STAIRS);

        addBidirectionalEdge(Location.FLOOR2_HALLWAY_CENTER,
                Location.FLOOR2_CLASSROOM,
                PathType.NORMAL);

        addEdge(Location.FLOOR2_STAIR_LEFT,
                Location.FLOOR3_STAIR_LEFT,
                PathType.LEFT_STAIRS);

        addEdge(Location.FLOOR2_STAIR_MID,
                Location.FLOOR3_STAIR_MID,
                PathType.MIDDLE_STAIRS);

        addEdge(Location.FLOOR2_STAIR_RIGHT,
                Location.FLOOR3_STAIR_RIGHT,
                PathType.RIGHT_STAIRS);

        // Floor 3 Connectivity
        addEdge(Location.FLOOR3_STAIR_LEFT,
                Location.FLOOR3_HALLWAY_LEFT,
                PathType.NORMAL);

        addEdge(Location.FLOOR3_STAIR_MID,
                Location.FLOOR3_HALLWAY_CENTER,
                PathType.NORMAL);

        addEdge(Location.FLOOR3_STAIR_RIGHT,
                Location.FLOOR3_HALLWAY_RIGHT,
                PathType.NORMAL);

        addBidirectionalEdge(Location.FLOOR3_HALLWAY_LEFT,
                Location.FLOOR3_HALLWAY_CENTER,
                PathType.NORMAL);

        addBidirectionalEdge(Location.FLOOR3_HALLWAY_LEFT,
                Location.FLOOR3_TESTING_LAB,
                PathType.NORMAL);

        addBidirectionalEdge(Location.FLOOR3_HALLWAY_CENTER,
                Location.FLOOR3_HALLWAY_RIGHT,
                PathType.NORMAL);

        addEdge(Location.FLOOR3_COMPUTER_LAB,
                Location.FLOOR3_HALLWAY_RIGHT,
                PathType.NORMAL);

        addEdge(Location.FLOOR3_HALLWAY_RIGHT,
                Location.FLOOR3_AT_DOOR,
                PathType.NORMAL);


        // Vents, Elevator, Office
        addEdge(Location.FLOOR1_GARDNER,
                Location.IN_VENT,
                PathType.VENT);

        addEdge(Location.FLOOR2_CLASSROOM,
                Location.IN_VENT,
                PathType.VENT);

        addEdge(Location.IN_VENT,
                Location.FLOOR1_GARDNER,
                PathType.VENT);

        addEdge(Location.IN_VENT,
                Location.FLOOR2_CLASSROOM,
                PathType.VENT);

        addEdge(Location.IN_VENT,
                Location.FLOOR3_HALLWAY_RIGHT,
                PathType.VENT);

        addEdge(Location.FLOOR1_ELEVATOR,
                Location.IN_ELEVATOR,
                PathType.ELEVATOR);

        addEdge(Location.IN_ELEVATOR,
                Location.FLOOR3_ELEVATOR_EXIT,
                PathType.ELEVATOR);

        addEdge(Location.FLOOR3_ELEVATOR_EXIT,
                Location.FLOOR3_HALLWAY_RIGHT,
                PathType.NORMAL);

        addEdge(Location.FLOOR3_AT_DOOR,
                Location.IN_OFFICE,
                PathType.NORMAL);

        addEdge(Location.IN_OFFICE,
                Location.IN_OFFICE,
                PathType.NORMAL);
    }

    /**
     * Adds a directed edge to the navigation graph.
     * Duplicate edges (same destination + type) are ignored.
     *
     * @param from source node
     * @param to   destination node
     * @param type path type for the transition
     */
    private static void addEdge(Location from, Location to, PathType type) {
        List<Edge> edges = GRAPH.get(from);
        for (Edge e : edges) {
            if (e.to == to && e.type == type) {
                return;
            }
        }
        edges.add(new Edge(to, type));
    }

    /**
     * Adds edges in both directions between two nodes.
     *
     * @param a    first node
     * @param b    second node
     * @param type path type for both directions
     */
    private static void addBidirectionalEdge(Location a, Location b, PathType type) {
        addEdge(a, b, type);
        addEdge(b, a, type);
    }

    /**
     * Determines the next location for a student based on current graph and AI logic.
     */
    public static Location getNextLocation(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        Location current = student.getCurrentLocation();
        Location prev = student.getPreviousLocation();
        List<Edge> edges = GRAPH.get(current);
        if (edges == null || edges.isEmpty()) {
            throw new IllegalStateException("No transitions from " + current);
        }
        return selectEdge(student, current, prev, edges).to;
    }

    /**
     * Filters edges and determines path selection strategy.
     */
    private static Edge selectEdge(Student student,
                                   Location current,
                                   Location prev,
                                   List<Edge> edges) {
        List<Edge> candidates = new ArrayList<>();
        if (prev != null) {
            for (Edge e : edges) {
                if (e.to != prev) {
                    candidates.add(e);
                }
            }
        }

        if (candidates.isEmpty()) {
            candidates = edges;
        }

        Personality p = student.getPersonality();
        if (p == Personality.RUNNER && student.isSprinting()) {
            return getRunnerPath(candidates);
        }

        if (p == Personality.CONFUSED) {
            return candidates.get(rand.nextInt(candidates.size()));
        }

        return weightedChoice(student, candidates);
    }

    /**
     * Performs a weighted probabilistic choice based on edge weights.
     */
    private static Edge weightedChoice(Student student, List<Edge> edges) {
        double total = 0;
        double[] weights = new double[edges.size()];
        for (int i = 0; i < edges.size(); i++) {
            double w = Math.max(getWeight(student, edges.get(i)), 0.0001);
            weights[i] = w;
            total += w;
        }
        double roll = rand.nextDouble() * total;
        double cumulative = 0;
        for (int i = 0; i < edges.size(); i++) {
            cumulative += weights[i];
            if (roll <= cumulative) {
                return edges.get(i);
            }
        }
        return edges.getLast();
    }

    /**
     * Calculates the weight of an edge based on student personality and path type.
     */
    private static double getWeight(Student student, Edge e) {
        Personality p = student.getPersonality();
        PathType preferred = student.getPreferredPath();
        double weight = 1.0;

        if (e.type == preferred) {
            weight *= 3.0;
        }

        if (p == Personality.SHY && e.to.hasVentAccess()) {
            weight *= 6.0;
        }

        if (p == Personality.EAGER && e.type == PathType.ELEVATOR) {
            weight *= 8.0;
        }

        if (p == Personality.PERSISTENT && e.type == PathType.LEFT_STAIRS) {
            weight *= 6.0;
        }
        if (p == Personality.RUNNER && e.type == PathType.RIGHT_STAIRS) {
            weight *= 4.0;
        }

        if (e.type == PathType.NORMAL) {
            weight *= 0.8;
        }

        // ===== FLOOR PROGRESSION =====
        int targetFloor = e.to.getFloor();
        if (targetFloor == 2) {
            weight *= 1.2;
        }
        if (targetFloor == 3) {
            weight *= 1.3;
        }

        // ===== HALLWAY DAMPENING =====
        if (e.to.name().contains("HALLWAY")) {
            weight *= 0.65;
        }

        // ===== LEAF BOOST =====
        if (getNeighbors(e.to).size() <= 2) {
            weight *= 1.3;
        }

        // ===== SHORT-TERM MEMORY PENALTY =====
        Set<Location> recent = student.getRecentLocations();
        if (recent.contains(e.to)) {
            weight *= 0.4; // strong discouragement
        }

        // stronger penalty for immediate backtracking (extra safety)
        if (e.to == student.getPreviousLocation()) {
            weight *= 0.3;
        }

        // recent visit decay
        int visits = student.getRecentVisitCount(e.to); // last N steps
        weight *= (1.0 / (1 + 0.5 * visits));

        // ===== ENTROPY =====
        weight *= (0.9 + rand.nextDouble() * 0.2);

        // ===== DEGREE NORMALIZATION =====
        int degree = NavigationManager.getNeighbors(e.to).size();
        weight /= Math.max(degree, 1);

        return weight;
    }

    /**
     * Deterministic pathfinding for the RUNNER personality during sprint.
     */
    private static Edge getRunnerPath(List<Edge> edges) {
        for (Edge e : edges) {
            if (e.to == Location.FLOOR3_AT_DOOR) {
                return e;
            }
        }

        for (Edge e : edges) {
            if (e.to == Location.FLOOR3_HALLWAY_RIGHT) {
                return e;
            }
        }

        return edges.get(rand.nextInt(edges.size()));
    }

    // ===== TEST HELPERS =====
    /**
     * Returns the list of neighbors reachable from a given location.
     *
     * @param loc source location
     * @return list of neighbor locations (possibly empty)
     */
    public static List<Location> getNeighbors(Location loc) {
        List<Location> result = new ArrayList<>();
        List<Edge> edges = GRAPH.get(loc);
        if (edges == null) {
            return result;
        }

        for (Edge e : edges) {
            result.add(e.to);
        }

        return result;
    }

    /**
     * Checks whether a directed transition exists in the graph.
     *
     * @param from source location
     * @param to   destination location
     * @return true if a direct transition exists
     */
    public static boolean isValidTransition(Location from, Location to) {
        List<Edge> edges = GRAPH.get(from);
        if (edges == null) {
            return false;
        }

        for (Edge e : edges) {
            if (e.to == to) {
                return true;
            }
        }

        return false;
    }

    /**
     * Exports the navigation graph in GraphViz DOT format.
     *
     * @return DOT graph as a string
     */
    public static String toDot() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n").append("    rankdir=TB;\n")
                .append("    node [shape=box, style=filled, fillcolor=white];\n\n");
        addCluster(sb, "Floor 1", "FLOOR1");
        addCluster(sb, "Floor 2", "FLOOR2");
        addCluster(sb, "Floor 3", "FLOOR3");
        sb.append("    subgraph cluster_special {\n        label=\"Special Nodes\";\n        \"IN_VENT\"; \"IN_ELEVATOR\"; \"IN_OFFICE\";\n    }\n\n");
        for (Location from : GRAPH.keySet()) {
            for (Edge e : GRAPH.get(from)) {
                String color = switch (e.type) {
                    case ELEVATOR -> "blue"; case LEFT_STAIRS -> "green";
                    case RIGHT_STAIRS -> "red"; case MIDDLE_STAIRS -> "orange";
                    case VENT -> "purple"; default -> "black";
                };
                sb.append("    \"").append(from).append("\" -> \"").append(e.to)
                        .append("\" [label=\"").append(e.type).append("\", color=\"").append(color).append("\"];\n");
            }
        }
        sb.append("}\n");
        return sb.toString();
    }

    /**
     * Adds a GraphViz cluster for nodes that share a prefix (e.g., FLOOR1).
     *
     * @param sb     output builder
     * @param label  cluster label
     * @param prefix enum name prefix to include
     */
    private static void addCluster(StringBuilder sb, String label, String prefix) {
        sb.append("    subgraph cluster_").append(prefix).append(" {\n")
                .append("        label=\"").append(label).append("\";\n");
        for (Location l : Location.values()) {
            if (l.name().startsWith(prefix)) {
                sb.append("        \"").append(l).append("\";\n");
            }
        }
        sb.append("    }\n");
    }

    /**
     * Exports the navigation graph in DOT format with node colors determined by a heatmap.
     *
     * @param heatmap location -> visit count
     * @return DOT graph as a string
     */
    public static String toDotWithHeat(Map<Location, Integer> heatmap) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n");
        sb.append("    rankdir=TB;\n");
        sb.append("    node [shape=box, style=filled];\n\n");

        int max = heatmap.values().stream().max(Integer::compareTo).orElse(1);

        // Nodes with color
        for (Location loc : Location.values()) {
            int value = heatmap.getOrDefault(loc, 0);

            double intensity = (double) value / max;

            // red heat scale
            int red = 255;
            int green = (int) (255 * (1 - intensity));
            int blue = (int) (255 * (1 - intensity));

            String color = String.format("#%02x%02x%02x", red, green, blue);

            sb.append("    \"")
                    .append(loc)
                    .append("\" [fillcolor=\"")
                    .append(color)
                    .append("\"];\n");
        }

        sb.append("\n");

        // Edges (reuse your existing logic)
        for (Location from : GRAPH.keySet()) {
            for (Edge e : GRAPH.get(from)) {

                String color = switch (e.type) {
                    case ELEVATOR -> "blue";
                    case LEFT_STAIRS -> "green";
                    case RIGHT_STAIRS -> "red";
                    case MIDDLE_STAIRS -> "orange";
                    case VENT -> "purple";
                    default -> "black";
                };

                sb.append("    \"")
                        .append(from)
                        .append("\" -> \"")
                        .append(e.to)
                        .append("\" [color=\"")
                        .append(color)
                        .append("\"];\n");
            }
        }

        sb.append("}\n");
        return sb.toString();
    }
}
