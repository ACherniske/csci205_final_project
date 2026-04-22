package org.five_nights_at_dana.AI;

import java.util.Scanner;
import org.five_nights_at_dana.AI.Pathing.Path;
import org.five_nights_at_dana.Managers.StudentManager;

/**
 * A console based test for some mechanics like pathing and student behavior
 */
public class MVP {

    /**
     * main for game loop for console testing
     * @param args to be passed
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;
        int inGameHour = 12;
        int ticksPassed = 0;
        Player player = new Player();
        StudentManager studentManager = new StudentManager(player);
        Power power = new Power();

        System.out.println("Survive until 6 AM.");

        while (isRunning) {
            System.out.println("Time: " + inGameHour + " AM");
            System.out.println("Power: " + power + "%");
            System.out.println("Office Door: " + (player.isDoorClosed() ? "[CLOSED]" : "[OPEN]"));
            System.out.println("Cameras: " + (player.isLookingAtCam() ? "[ONLINE]" : "[OFFLINE]"));
            System.out.println("Commands: [w]ait, [d]oor, [c]amera, [q]uit");
            System.out.print("> ");

            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("q") || input.equals("quit")) {
                System.out.println("Game Over.");
                break;
            } else if (input.equals("d") || input.equals("door")) {
                if (power.consumePower(2)) {
                    player.setDoorClosed(!player.isDoorClosed());

                    if (player.isDoorClosed()) {
                        System.out.println("*CLUNK* Door Closed");
                    } else {
                        System.out.println("*SCREEE* Door Opened");
                    }
                } else {
                    System.out.println("Insufficient Power!");
                }


            } else if ((input.equals("c") || input.equals("camera"))) {
                if (power.consumePower(1)) {
                    player.setLookingAtCam(true);
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
                            case 1: player.setCurrentCamRoom(Path.ENTRANCE); break;
                            case 2: player.setCurrentCamRoom(Path.MIDDLE_STAIRS_1); break;
                            case 3: player.setCurrentCamRoom(Path.LEFT_STAIRS_1); break;
                            case 4: player.setCurrentCamRoom(Path.ELEVATOR_IN); break;
                            case 5: player.setCurrentCamRoom(Path.LEFT_STAIRS_2); break;
                            case 6: player.setCurrentCamRoom(Path.HALLWAY_2A); break;
                            case 7: player.setCurrentCamRoom(Path.HALLWAY_2B); break;
                            case 8: player.setCurrentCamRoom(Path.MIDDLE_STAIRS_2); break;
                            case 9: player.setCurrentCamRoom(Path.HALLWAY_3B); break;
                            case 10: player.setCurrentCamRoom(Path.RIGHT_STAIRS3); break;
                            case 11: player.setCurrentCamRoom(Path.OFFICE); break;
                            case 12: player.setCurrentCamRoom(Path.CLASSROOM_3C); break;
                            default:
                                player.setLookingAtCam(false);
                                System.out.println("Camera off");
                                break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Camera system crashed.");
                        player.setLookingAtCam(false);
                    }
                } else {
                    System.out.println("Insufficient Power!");
                }
            } else {
                System.out.println("waiting...");
            }

            studentManager.update();

            if (!player.isAlive()) {
                isRunning = false;
                System.out.println("YOU LOSE!");
            }

            ticksPassed++;
            if (ticksPassed >= 10) {
                ticksPassed = 0;
                inGameHour++;
                if (inGameHour == 13) {
                    inGameHour = 1;
                }

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

