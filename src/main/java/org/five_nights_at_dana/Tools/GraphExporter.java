package org.five_nights_at_dana.Tools;

import org.five_nights_at_dana.AI.Location;
import org.five_nights_at_dana.AI.Personality;
import org.five_nights_at_dana.Managers.NavigationManager;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class GraphExporter {

    private static final String OUTPUT_DIR =
            "src/main/java/org/five_nights_at_dana/Tools/";

    public static void main(String[] args) {

        exportBaseGraph();

        for (Personality personality : Personality.values()) {

            System.out.println("Generating heatmap for: " + personality);

            Map<Location, Integer> heatmap =
                    NavigationHeatmap.generateHeatmap(
                            personality,
                            Location.FLOOR1_ENTRANCE,
                            300,   // steps
                            300    // runs
                    );

            exportHeatGraph(personality, heatmap);
        }

        System.out.println("All graphs exported.");
    }

    private static void exportBaseGraph() {
        try (FileWriter writer = new FileWriter(
                OUTPUT_DIR + "Navigation_BASE.dot")) {

            writer.write(NavigationManager.toDot());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void exportHeatGraph(Personality personality,
                                        Map<Location, Integer> heatmap) {

        try (FileWriter writer = new FileWriter(
                OUTPUT_DIR + "Navigation_" + personality.name() + "_HEAT.dot")) {

            writer.write(NavigationManager.toDotWithHeat(heatmap));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
