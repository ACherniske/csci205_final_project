package org.five_nights_at_dana.Tools;

import org.five_nights_at_dana.AI.*;
import org.five_nights_at_dana.Managers.NavigationManager;

import java.util.*;

public class NavigationHeatmap {

    public static Map<Location, Integer> generateHeatmap(
            Personality personality,
            Location start,
            int steps,
            int runs
    ) {
        Map<Location, Integer> heatmap = new HashMap<>();

        for (Location loc : Location.values()) {
            heatmap.put(loc, 0);
        }

        for (int r = 0; r < runs; r++) {
            Location actualStart = (personality == Personality.RUNNER)
                    ? Location.FLOOR3_COMPUTER_LAB
                    : start;

            Student s = new Student("Sim", "", personality);
            s.setLocation(actualStart);

            // ===== SPECIAL HANDLING: RUNNER =====
            if (personality == Personality.RUNNER) {
                s.startSprint();

                // force into sprint immediately (skip timing system)
                for (int i = 0; i < 120; i++) {
                    s.update();
                }
            }

            for (int i = 0; i < steps; i++) {

                Location current = s.getCurrentLocation();
                heatmap.put(current, heatmap.get(current) + 1);

                // ===== FORCE MOVEMENT =====
                Location next = NavigationManager.getNextLocation(s);
                s.setLocation(next);

                if (personality == Personality.RUNNER) {
                    if (!s.getCurrentLocation().name().startsWith("FLOOR3")
                            && s.getCurrentLocation() != Location.IN_OFFICE) {

                        throw new RuntimeException(
                                "Runner escaped floor 3: " + s.getCurrentLocation()
                        );
                    }
                }

                // stop once office reached (prevents heat inflation)
                if (next == Location.IN_OFFICE) break;
            }
        }

        return heatmap;
    }

    public static String buildStatsReport(Personality personality,
                                          Map<Location, Integer> heatmap,
                                          int totalSamples) {

        StringBuilder sb = new StringBuilder();

        sb.append("====================================\n");
        sb.append("Personality: ").append(personality).append("\n");
        sb.append("Total Samples: ").append(totalSamples).append("\n");
        sb.append("====================================\n");

        // Sort by most visited
        List<Map.Entry<Location, Integer>> sorted =
                new ArrayList<>(heatmap.entrySet());

        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        for (Map.Entry<Location, Integer> entry : sorted) {
            Location loc = entry.getKey();
            int count = entry.getValue();

            double percent = (100.0 * count) / totalSamples;

            sb.append(String.format("%-30s : %6d (%.2f%%)\n",
                    loc.name(), count, percent));
        }

        sb.append("\nTOP 5 LOCATIONS:\n");

        for (int i = 0; i < Math.min(5, sorted.size()); i++) {
            sb.append("  ").append(sorted.get(i).getKey()).append("\n");
        }

        sb.append("\n\n");

        return sb.toString();
    }
}
