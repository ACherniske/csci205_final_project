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

        // ==================== FLOOR 1 ====================

        addBidirectionalEdge(Location.FLOOR1_ENTRANCE, Location.FLOOR1_HALLWAY_LEFT, PathType.NORMAL);
        addBidirectionalEdge(Location.FLOOR1_ENTRANCE, Location.FLOOR1_HALLWAY_RIGHT, PathType.NORMAL);
        addBidirectionalEdge(Location.FLOOR1_ENTRANCE, Location.FLOOR1_LOUNGE, PathType.NORMAL);
        addBidirectionalEdge(Location.FLOOR1_ENTRANCE, Location.FLOOR1_STAIR_MID, PathType.MIDDLE_STAIRS);

        addBidirectionalEdge(Location.FLOOR1_HALLWAY_LEFT, Location.FLOOR1_GARDNER, PathType.NORMAL);
        addBidirectionalEdge(Location.FLOOR1_HALLWAY_LEFT, Location.FLOOR1_MAKER_E, PathType.NORMAL);

        addEdge(Location.FLOOR1_HALLWAY_LEFT, Location.FLOOR1_STAIR_LEFT, PathType.LEFT_STAIRS);

        addEdge(Location.FLOOR1_HALLWAY_RIGHT, Location.FLOOR1_ELEVATOR, PathType.ELEVATOR);
        addEdge(Location.FLOOR1_HALLWAY_RIGHT, Location.FLOOR1_STAIR_RIGHT, PathType.RIGHT_STAIRS);

        addBidirectionalEdge(Location.FLOOR1_LOUNGE, Location.FLOOR1_STAIR_MID, PathType.MIDDLE_STAIRS);
        addBidirectionalEdge(Location.FLOOR1_LOUNGE, Location.FLOOR1_HALLWAY_RIGHT, PathType.NORMAL);

        // ==================== FLOOR 2 ====================

        addEdge(Location.FLOOR1_STAIR_LEFT, Location.FLOOR2_STAIR_LEFT, PathType.LEFT_STAIRS);
        addEdge(Location.FLOOR1_STAIR_MID, Location.FLOOR2_STAIR_MID, PathType.MIDDLE_STAIRS);
        addEdge(Location.FLOOR1_STAIR_RIGHT, Location.FLOOR2_STAIR_RIGHT, PathType.RIGHT_STAIRS);

        addEdge(Location.FLOOR2_STAIR_LEFT, Location.FLOOR2_HALLWAY_LEFT, PathType.NORMAL);
        addEdge(Location.FLOOR2_STAIR_MID, Location.FLOOR2_HALLWAY_CENTER, PathType.NORMAL);
        addEdge(Location.FLOOR2_STAIR_RIGHT, Location.FLOOR2_HALLWAY_RIGHT, PathType.NORMAL);

        addBidirectionalEdge(Location.FLOOR2_HALLWAY_LEFT, Location.FLOOR2_COMPUTER_LAB, PathType.NORMAL);
        addBidirectionalEdge(Location.FLOOR2_HALLWAY_LEFT, Location.FLOOR2_HALLWAY_CENTER, PathType.NORMAL);

        addBidirectionalEdge(Location.FLOOR2_HALLWAY_RIGHT, Location.FLOOR2_HALLWAY_CENTER, PathType.NORMAL);

        addBidirectionalEdge(Location.FLOOR2_HALLWAY_CENTER, Location.FLOOR2_STAIR_MID, PathType.MIDDLE_STAIRS);

        addBidirectionalEdge(Location.FLOOR2_HALLWAY_CENTER, Location.FLOOR2_CLASSROOM, PathType.NORMAL);

        // ==================== FLOOR 3 ====================

        addEdge(Location.FLOOR2_STAIR_LEFT, Location.FLOOR3_STAIR_LEFT, PathType.LEFT_STAIRS);
        addEdge(Location.FLOOR2_STAIR_MID, Location.FLOOR3_STAIR_MID, PathType.MIDDLE_STAIRS);
        addEdge(Location.FLOOR2_STAIR_RIGHT, Location.FLOOR3_STAIR_RIGHT, PathType.RIGHT_STAIRS);

        addEdge(Location.FLOOR3_STAIR_LEFT, Location.FLOOR3_HALLWAY_LEFT, PathType.NORMAL);
        addEdge(Location.FLOOR3_STAIR_MID, Location.FLOOR3_HALLWAY_CENTER, PathType.NORMAL);
        addEdge(Location.FLOOR3_STAIR_RIGHT, Location.FLOOR3_HALLWAY_RIGHT, PathType.NORMAL);

        addBidirectionalEdge(Location.FLOOR3_HALLWAY_LEFT, Location.FLOOR3_HALLWAY_CENTER, PathType.NORMAL);
        addBidirectionalEdge(Location.FLOOR3_HALLWAY_LEFT, Location.FLOOR3_TESTING_LAB, PathType.NORMAL);

        addBidirectionalEdge(Location.FLOOR3_HALLWAY_CENTER, Location.FLOOR3_HALLWAY_RIGHT, PathType.NORMAL);

        addBidirectionalEdge(Location.FLOOR3_HALLWAY_RIGHT, Location.FLOOR3_COMPUTER_LAB, PathType.NORMAL);
        addEdge(Location.FLOOR3_HALLWAY_RIGHT, Location.FLOOR3_AT_DOOR, PathType.NORMAL);

        // ==================== VENTS ====================

        addEdge(Location.FLOOR1_GARDNER, Location.IN_VENT, PathType.VENT);
        addEdge(Location.FLOOR2_CLASSROOM, Location.IN_VENT, PathType.VENT);

        addEdge(Location.IN_VENT, Location.FLOOR1_GARDNER, PathType.VENT);
        addEdge(Location.IN_VENT, Location.FLOOR2_CLASSROOM, PathType.VENT);
        addEdge(Location.IN_VENT, Location.FLOOR3_HALLWAY_RIGHT, PathType.VENT);

        // ==================== ELEVATOR ====================

        addEdge(Location.FLOOR1_ELEVATOR, Location.IN_ELEVATOR, PathType.ELEVATOR);
        addEdge(Location.IN_ELEVATOR, Location.FLOOR3_ELEVATOR_EXIT, PathType.ELEVATOR);

        addEdge(Location.FLOOR3_ELEVATOR_EXIT, Location.FLOOR3_HALLWAY_RIGHT, PathType.NORMAL);

        // ==================== FINAL ====================

        addEdge(Location.FLOOR3_AT_DOOR, Location.IN_OFFICE, PathType.NORMAL);

        // prevent test crashes (terminal states)
        addEdge(Location.IN_OFFICE, Location.IN_OFFICE, PathType.NORMAL);
    }

    private static void addEdge(Location from, Location to, PathType type) {
        List<Edge> edges = graph.get(from);

        for (Edge e : edges) {
            if (e.to == to && e.type == type) return;
        }

        edges.add(new Edge(to, type));
    }

    private static void addBidirectionalEdge(Location a, Location b, PathType type) {
        addEdge(a, b, type);
        addEdge(b, a, type);
    }

    public static Location getNextLocation(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }

        Location current = student.getCurrentLocation();
        List<Edge> edges = graph.get(current);

        if (edges == null || edges.isEmpty()) {
            throw new IllegalStateException("No valid transitions from " + current);
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

        if (student.getPersonality() == Personality.RUNNER) {
            return getRunnerPath(edges);
        }

        if (p == Personality.CONFUSED) {
            return edges.get(rand.nextInt(edges.size()));
        }

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

        return edges.get(rand.nextInt(edges.size()));
    }

    private static Edge getRunnerPath(List<Edge> edges) {
        // If at hallway right → go straight to door
        for (Edge e : edges) {
            if (e.to == Location.FLOOR3_AT_DOOR) {
                return e;
            }
        }

        // Otherwise: move toward hallway right if possible
        for (Edge e : edges) {
            if (e.to == Location.FLOOR3_HALLWAY_RIGHT) {
                return e;
            }
        }

        // fallback (should rarely happen)
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

    // ==================== GRAPH EXPORT ====================

    /**
     * Generates a GraphViz DOT string representation of the navigation graph.
     * Copy the output and render it at <a href="https://dreampuf.github.io/GraphvizOnline/">...</a>
     * @return A DOT-formatted string representing the navigation graph.
     */
    public static String toDot() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n");
        sb.append("    rankdir=TB;\n");
        sb.append("    node [shape=box, style=filled, fillcolor=white];\n\n");

        // ---- Floor Clusters ----
        addCluster(sb, "Floor 1", "FLOOR1");
        addCluster(sb, "Floor 2", "FLOOR2");
        addCluster(sb, "Floor 3", "FLOOR3");

        // ---- Special Nodes ----
        sb.append("    subgraph cluster_special {\n");
        sb.append("        label=\"Special Nodes\";\n");
        sb.append("        \"IN_VENT\"; \"IN_ELEVATOR\"; \"IN_OFFICE\";\n");
        sb.append("    }\n\n");

        // ---- Edges ----
        for (Location from : graph.keySet()) {
            for (Edge e : graph.get(from)) {
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
                        .append("\" [label=\"")
                        .append(e.type)
                        .append("\", color=\"")
                        .append(color)
                        .append("\"];\n");
            }
        }
        sb.append("}\n");
        return sb.toString();
    }

    private static void addCluster(StringBuilder sb, String label, String prefix) {
        sb.append("    subgraph cluster_").append(prefix).append(" {\n");
        sb.append("        label=\"").append(label).append("\";\n");
        for (Location l : Location.values()) {
            if (l.name().startsWith(prefix)) {
                sb.append("        \"").append(l).append("\";\n");
            }
        }
        sb.append("    }\n");
    }
}
