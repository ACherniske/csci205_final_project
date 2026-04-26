package org.five_nights_at_dana.Tools;

import org.five_nights_at_dana.AI.*;
import org.five_nights_at_dana.Managers.NavigationManager;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class GraphExporter {

    private static final String OUTPUT_DIR =
            "src/main/java/org/five_nights_at_dana/Tools/";

    public static void main(String[] args) {

        exportBaseGraph();

        StringBuilder globalReport = new StringBuilder();

        for (Personality personality : Personality.values()) {

            System.out.println("Generating heatmap for: " + personality);

            Location start =
                    (personality == Personality.RUNNER)
                            ? Location.FLOOR3_COMPUTER_LAB
                            : Location.FLOOR1_ENTRANCE;

            Map<Location, Integer> heatmap =
                    NavigationHeatmap.generateHeatmap(
                            personality,
                            start,
                            300,
                            500
                    );

            exportHeatGraph(personality, heatmap);

            int total = heatmap.values().stream().mapToInt(i -> i).sum();

            String report = NavigationHeatmap.buildStatsReport(
                    personality,
                    heatmap,
                    total
            );

            exportTextReport(personality, report);

            globalReport.append(report);
        }

        exportGlobalReport(globalReport.toString());

        System.out.println("All graphs + reports exported.");
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

    private static void exportTextReport(Personality personality,
                                         String report) {

        try (FileWriter writer = new FileWriter(
                OUTPUT_DIR + "Navigation_" + personality.name() + "_STATS.txt")) {

            writer.write(report);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void exportGlobalReport(String report) {
        try (FileWriter writer = new FileWriter(
                OUTPUT_DIR + "Navigation_ALL_STATS.txt")) {

            writer.write(report);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
