/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/26/2026
 * Time: 12:51 AM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Tools
 * Class: GraphExporter
 *
 * Description:
 *
 * ****************************************
 */

package org.five_nights_at_dana.Tools;

import org.five_nights_at_dana.Managers.NavigationManager;

import java.io.FileWriter;
import java.io.IOException;

public class GraphExporter {
    public static void main(String[] args) {
        try {
            String dot = NavigationManager.toDot();

            FileWriter writer = new FileWriter("src/main/java/org/five_nights_at_dana/Tools/Navigation.dot");
            writer.write(dot);
            writer.close();

            System.out.println("Graph exported to nav_graph.dot");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}