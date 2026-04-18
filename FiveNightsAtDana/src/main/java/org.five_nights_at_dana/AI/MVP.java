package org.five_nights_at_dana.AI;

import org.five_nights_at_dana.AI.Pathing.Path;
import org.five_nights_at_dana.AI.Pathing.PathPoint;
import org.five_nights_at_dana.AI.Personalities.Eager;
import org.five_nights_at_dana.AI.Personalities.Lazy;
import org.five_nights_at_dana.AI.Personalities.Shy;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MVP {
    public static void main(String[] args) {
        Student shyGuy = new Student(new Shy(), 15);
        Student eagerGuy = new Student(new Eager(), 15);
        Student lazyGuy = new Student(new Lazy(), 15);

        List<Student> students = new ArrayList<>();
        students.add(shyGuy);
        students.add(eagerGuy);
        students.add(lazyGuy);

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;
        boolean isDoorClosed = false;
        boolean isLookingAtCam = false;
        PathPoint currentCameraRoom = null;
        int inGameHour = 12;
        int ticksPassed = 0;
        int power = 40;

        System.out.println("Survive until 6 AM.");

        while (isRunning) {
            System.out.println("Time: " + inGameHour + " AM");
            System.out.println("Power: " + power * 100 / 40 + "%");
            System.out.println("Office Door: " + (isDoorClosed ? "[CLOSED]" : "[OPEN]"));
            System.out.println("Cameras: " + (isLookingAtCam ? "[ONLINE]" : "[OFFLINE]"));
            System.out.println("-------------------------------------");
            System.out.println("Commands: [w]ait, [d]oor, [c]amera, [q]uit");
            System.out.print("> ");

            String input = scanner.nextLine().trim().toLowerCase();

            isLookingAtCam = false;
            isDoorClosed = false;
            if (input.equals("q") || input.equals("quit")) {
                System.out.println("Game Over.");
                break;
            } else if ((input.equals("d") || input.equals("door") && power >= 2)) {
                isDoorClosed = true;
                power-= 2;
                System.out.println("*CLUNK*");
            } else if ((input.equals("c") || input.equals("camera")) && power >= 1) {
                power--;
                isLookingAtCam = true;
                System.out.println("[1] Main Entrance (Dana)");
                System.out.println("[2] Middle Stairwell");
                System.out.println("[3] 1st Floor - Left Stairwell Approach");
                System.out.println("[4] 1st Floor - Elevator");
                System.out.println("[5] 2nd Floor - Left Stairwell");
                System.out.println("[6] Hallway 2.A");
                System.out.println("[7] Hallway 2.B");
                System.out.println("[8] Middle Staircase (Upper)");
                System.out.println("[9] Hallway 3.B (Office Approach)");
                System.out.println("[10] Right Stairwell (Office Approach)");
                System.out.println("[11] Lily's Office Window");
                System.out.println("[12] The Classroom");
                System.out.print("Select a camera (1-12) or 0 to close: ");

                try {
                    int camChoice = Integer.parseInt(scanner.nextLine().trim());

                    switch (camChoice) {
                        case 1: currentCameraRoom = Path.ENTRANCE; break;
                        case 2: currentCameraRoom = Path.MIDDLE_STAIRS_1; break;
                        case 3: currentCameraRoom = Path.LEFT_STAIRS_1; break;
                        case 4: currentCameraRoom = Path.ELEVATOR_IN; break;
                        case 5: currentCameraRoom = Path.LEFT_STAIRS_2; break;
                        case 6: currentCameraRoom = Path.HALLWAY_2A; break;
                        case 7: currentCameraRoom = Path.HALLWAY_2B; break;
                        case 8: currentCameraRoom = Path.MIDDLE_STAIRS_2; break;
                        case 9: currentCameraRoom = Path.HALLWAY_3B; break;
                        case 10: currentCameraRoom = Path.RIGHT_STAIRS3; break;
                        case 11: currentCameraRoom = Path.OFFICE; break;
                        case 12: currentCameraRoom = Path.CLASSROOM_3C; break;
                        default:
                            isLookingAtCam = false;
                            System.out.println("Camera off");
                            break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Camera system crashed.");
                    isLookingAtCam = false;
                }
            } else {
                System.out.println("waiting...");
            }

            for (Student student : students) {
                boolean isLookingAtStudent = currentCameraRoom == student.getCurrentLocation() && isLookingAtCam;

                student.update(1, isDoorClosed, isLookingAtStudent);

                if (student.isJumpScared()) {
                    System.out.println("GAME OVER.");
                    isRunning = false;
                }
                System.out.println("Student in " + student.getCurrentLocation().getLocation());
            }

            ticksPassed++;
            if (ticksPassed >= 10) {
                ticksPassed = 0;
                inGameHour++;
                if (inGameHour == 13) inGameHour = 1;

                System.out.println(inGameHour + " AM");
            }

            if (inGameHour == 6 && isRunning) {
                System.out.println("Win");
                isRunning = false;
            }
        }

        scanner.close();
    }
}

