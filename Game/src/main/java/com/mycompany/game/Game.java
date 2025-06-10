package com.mycompany.game;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class Game {
    private static Scanner scanner = new Scanner(System.in);
    private static Hero player;
    private static String playerName;
    private static HashMap<String, Dungeon> dungeons = new HashMap<>();
    private static Arena arena;
    private static LinkedList<String> dungeonHistory = new LinkedList<>();
    private static boolean isGameOver = false;
    private static boolean arenaCleared = false;

    public static void main(String[] args) {
        requestPlayerName();
        cinematicIntro();

        HeroType chosenType = HeroType.chooseHeroType(scanner);
        player = new Hero(playerName, chosenType);

        DeathDialogue.setPlayerName(playerName);

        printTitle("SYSTEM WINDOW");
        System.out.println("  [A cold system window flickers before you, sharp and unreal.]");
        System.out.println("  Welcome to DEATH'S GAME");
        printSectionEnd();
        System.out.println("[System] You have been chosen for a cruel encore. Fight, grow, and prove your worth.");
        System.out.println("[System] Your class: " + chosenType.name);
        DeathDialogue.onFirstAwakening();
        pause();

        initializeDungeons();
        arena = new Arena();

        mainMenuLoop();

        if (!arenaCleared && !isGameOver) {
            System.out.println("[System] The time for rehearsal is over. The Arena awaits.");
            arenaEntry();
        }

        if (isGameOver) endGame();
    }

    private static void requestPlayerName() {
        printTitle("The End...?");
        printSectionEnd();
        System.out.print("Enter your name: ");
        playerName = scanner.nextLine();
        System.out.println();
    }

    private static void cinematicIntro() {
        System.out.println("Darkness presses in. You remember pain, regret, the weight of a life unlived.");
        System.out.println("A final, desperate act... Then silence.");
        pause();
        System.out.println("You died.");
        printDivider();
        System.out.println("*A void. And then, laughter - rich, echoing, impossible to ignore.*");
        pause();
        DeathDialogue.onFirstDeath();
        System.out.println("[A SYSTEM WINDOW appears: < DEATH'S GAME >]");
        pause();
    }

    private static void mainMenuLoop() {
        while (!arenaCleared && !isGameOver) {
            printTitle("MAIN MENU");
            System.out.println("[The world waits. Your only escape is Death's Arena. Dungeons are just rehearsal.]");
            printMenuOption(1, "Face a Dungeon (Grow, Adapt, Level Up)");
            printMenuOption(2, "Check Your Status");
            printMenuOption(3, "Confront Death's Arena (Final Trial)");
            printMenuOption(4, "Surrender (Abandon Hope)");
            printSectionEnd();
            printInputPrompt();
            String choice = scanner.nextLine();

            System.out.println();

            switch (choice) {
                case "1":
                    chooseDungeon();
                    break;
                case "2":
                    player.printStatus();
                    break;
                case "3":
                    arenaEntry();
                    break;
                case "4":
                    DeathDialogue.onQuit();
                    System.out.print("[System] Are you sure you want to surrender? (y/n): ");
                    if (scanner.nextLine().equalsIgnoreCase("y")) {
                        isGameOver = true;
                    }
                    break;
                default:
                    System.out.println("A system beep. [Invalid option.]");
            }
        }
    }

    private static void chooseDungeon() {
        printTitle("TRAINING DUNGEONS");
        System.out.println("[Each dungeon is a stage, every monster a line in your second chance script.]");
        int idx = 1;
        for (String key : dungeons.keySet()) {
            printMenuOption(idx, dungeons.get(key).name + " (" + dungeons.get(key).rank + ")");
            idx++;
        }
        printSectionEnd();
        printInputPrompt();
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            System.out.println();
            if (choice > 0 && choice <= dungeons.size()) {
                String selected = (String)dungeons.keySet().toArray()[choice-1];
                Dungeon dungeon = dungeons.get(selected);
                DeathDialogue.onDungeonTrainingStart(dungeon.name);
                dungeon.runDungeon(player, scanner, null, dungeonHistory);
                if (player.isDead()) {
                    isGameOver = true;
                    DeathDialogue.onQuit();
                } else {
                    DeathDialogue.onTrainingWin(dungeon.name, dungeon.rank);
                }
            } else {
                System.out.println("The world flickers. [Invalid dungeon.]");
            }
        } catch (NumberFormatException ex) {
            System.out.println("The system rejects your input. [Please enter a number.]");
        }
    }

    private static void arenaEntry() {
        DeathDialogue.onArenaEntry();
        boolean survived = arena.startArena(player, scanner, null);
        if (survived) {
            arenaCleared = true;
            DeathDialogue.onArenaWin();
            isGameOver = true;
        } else {
            DeathDialogue.onArenaLose();
            isGameOver = true;
        }
    }

    private static void endGame() {
        printDivider();
        System.out.println("[Curtain Call]");
        if (arenaCleared) {
            System.out.println();
            System.out.println("*A hush falls. Light seeps in. You awaken, breathless—alive.*");
            System.out.println("\"You have earned your encore. Live well, " + playerName + ". This curtain may never rise again.\"");
            System.out.println();
            System.out.println("[System] TRUE ENDING: You have escaped Death's grasp.");
        } else {
            System.out.println("[System] The void embraces you. Death's Game claims another soul.");
        }
        printDivider();
    }

    private static void initializeDungeons() {
        dungeons.put("F", new Dungeon("Slime Fields", "F", "Beginner's Trial", "Slime", 30, 1));
        dungeons.put("E", new Dungeon("Goblin Den", "E", "Forest of Mischief", "Goblin", 35, 5));
        dungeons.put("D", new Dungeon("Spider Nest", "D", "Webbed Abyss", "Spider", 38, 10));
        dungeons.put("C", new Dungeon("Wolf Cavern", "C", "Moonlit Grotto", "Wolf", 42, 15));
        dungeons.put("B", new Dungeon("Orc Fortress", "B", "Crimson Hold", "Orc", 46, 20));
        dungeons.put("A", new Dungeon("Undead Crypt", "A", "Halls of the Damned", "Undead", 50, 25));
        dungeons.put("S", new Dungeon("Dragon Lair", "S", "Eternal Flame", "Dragon", 60, 30));
        dungeons.put("SS", new Dungeon("Demon Abyss", "SS", "Abyssal Gate", "Demon", 70, 40));
        dungeons.put("SSS", new Dungeon("Celestial Arena", "SSS", "Heaven's Door", "Angel", 80, 50));
    }

    public static void printTitle(String s) {
        System.out.println();
        System.out.println("--- " + s + " ---");
    }

    public static void printSectionEnd() {
        System.out.println();
    }

    public static void printMenuOption(int num, String text) {
        System.out.printf("  %d. %-30s\n", num, text);
    }

    public static void printInputPrompt() {
        System.out.print("> ");
    }

    public static void printDivider() {
        System.out.println("-------------------------------");
    }

    private static void pause() {
        System.out.print("[Press Enter to continue]");
        scanner.nextLine();
        System.out.println();
    }
}