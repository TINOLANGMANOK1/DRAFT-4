package com.mycompany.game;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public class Dungeon {
    public String name, rank, desc, monsterType;
    public int monsterCount, minLevel;
    private DungeonRoom[][] map;
    private int size;
    private int playerX, playerY;
    private boolean bossDefeated = false;
    private Random rand = new Random();

    // Room descriptions for flavor/mood
    private static final String[] forestRooms = {
        "an ancient oak grove, shadows dancing between the roots",
        "a clearing shimmering with moonlight",
        "a tangled thicket, the air thick with mist",
        "a mossy path winding deeper into the unknown",
        "a sunken glade, silent and watchful"
    };
    private static final String[] caveRooms = {
        "a damp cavern glittering with faint crystals",
        "a narrow tunnel echoing with distant howls",
        "a stalagmite field, slick with dew",
        "a dark alcove, the walls carved by ancient claws",
        "a flooded chamber, water lapping at your boots"
    };
    private static final String[] cryptRooms = {
        "a hall lined with broken sarcophagi",
        "a candlelit shrine to forgotten gods",
        "a spiral staircase vanishing into darkness",
        "a chamber of faded runes on cold stone",
        "a collapsed antechamber, dust swirling in the gloom"
    };
    private static final String[] genericRooms = {
        "a strange, empty space. Something feels off here.",
        "a crossroads where the air grows colder",
        "a narrow corridor, walls close and oppressive",
        "a room filled with echoes of battles past",
        "a quiet niche safe for a moment's breath"
    };

    public Dungeon(String name, String rank, String desc, String monsterType, int monsterCount, int minLevel) {
        this.name = name; this.rank = rank; this.desc = desc;
        this.monsterType = monsterType; this.monsterCount = monsterCount; this.minLevel = minLevel;
        this.size = 5; // 5x5 grid for demonstration
        this.map = new DungeonRoom[size][size];
        generateMap();
    }

    private void generateMap() {
        String[] roomTypes = getRoomTypes();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                String desc = roomTypes[rand.nextInt(roomTypes.length)];
                map[i][j] = new DungeonRoom(desc, false, false, false, false);
            }
        }
        // Place entrance at (0,0)
        map[0][0].desc = "the dungeon entrance. The way back is behind you.";
        map[0][0].entrance = true;
        // Place exit (boss room) at (size-1, size-1)
        map[size-1][size-1].desc = "the heart of the dungeon. A powerful presence awaits - the Boss!";
        map[size-1][size-1].boss = true;
    }

    private String[] getRoomTypes() {
        if (name.toLowerCase().contains("forest") || name.toLowerCase().contains("wolf"))
            return forestRooms;
        if (name.toLowerCase().contains("crypt") || name.toLowerCase().contains("undead"))
            return cryptRooms;
        if (name.toLowerCase().contains("cavern") || name.toLowerCase().contains("den") || name.toLowerCase().contains("nest"))
            return caveRooms;
        return genericRooms;
    }

    public void runDungeon(Hero player, java.util.Scanner scanner, Stack<String> deathStack, LinkedList<String> dungeonHistory) {
        playerX = 0; playerY = 0;
        bossDefeated = false;

        System.out.println("\n[Dungeon: " + name + " | Rank " + rank + "]");
        System.out.println("[Death whispers: \"This is rehearsal. The Arena is the true stage. Learn, bleed, grow.\"]");
        System.out.println("NOTE: This dungeon is a training ground. Explore and defeat the boss to clear it.");
        if (player.level < minLevel) {
            System.out.println("A cold wind. [Warning: Your level is below this dungeon's minimum (" + minLevel + ").]");
        }
        dungeonHistory.add(name);

        int encounterCount = 0;
        int encountersBeforeBoss = monsterCount;
        Random rand = new Random();

        while (!player.isDead() && !bossDefeated) {
            DungeonRoom current = map[playerX][playerY];
            System.out.println();
            System.out.println("You are in " + current.desc);

            // Random encounter (not at boss room)
            if (!current.boss && rand.nextDouble() < 0.4 && encounterCount < encountersBeforeBoss && !current.cleared) {
                encounterCount++;
                Monster m = Monster.generate(monsterType, Math.max(player.level, minLevel));
                System.out.println("A " + m.name + " appears! [" + m.skill + "]");
                while (m.hp > 0 && !player.isDead()) {
                    Hero.printBattleStatus(player, m);
                    System.out.println("1. Attack  2. Skill  3. Run");
                    String action = scanner.nextLine();
                    switch (action) {
                        case "1":
                            if (player.attack(m)) {
                                System.out.println("The " + m.name + " falls. You gain EXP. Death's applause echoes faintly.");
                                player.gainExp(7 + minLevel);
                                current.cleared = true;
                            }
                            break;
                        case "2":
                            if (player.useSkillMenu(m, scanner)) {
                                System.out.println("The " + m.name + " falls. You gain EXP. Death's applause echoes faintly.");
                                player.gainExp(7 + minLevel);
                                current.cleared = true;
                            }
                            break;
                        case "3":
                            System.out.println("You retreat, heart pounding, deeper into the dungeon.");
                            break;
                    }
                    if (m.hp > 0 && !player.isDead()) {
                        int dmg = m.atk - player.stats[3]/2;
                        if (dmg < 1) dmg = 1;
                        player.takeDamage(dmg);
                    }
                }
                if (player.isDead()) break;
            }

            if (current.boss) {
                System.out.println("A massive presence looms. The Boss is here!");
                Monster boss = Monster.boss(monsterType, minLevel + 5);
                while (boss.hp > 0 && !player.isDead()) {
                    Hero.printBattleStatus(player, boss);
                    System.out.println("1. Attack  2. Skill  3. Run");
                    String action = scanner.nextLine();
                    switch (action) {
                        case "1":
                            if (player.attack(boss)) {
                                System.out.println("Boss defeated! Gained EXP. Death's laughter is softer now.");
                                player.gainExp(22 + minLevel*2);
                                DeathDialogue.onTrainingWin(boss.name, "Boss");
                                bossDefeated = true;
                            }
                            break;
                        case "2":
                            if (player.useSkillMenu(boss, scanner)) {
                                System.out.println("Boss defeated! Gained EXP. Death's laughter is softer now.");
                                player.gainExp(22 + minLevel*2);
                                DeathDialogue.onTrainingWin(boss.name, "Boss");
                                bossDefeated = true;
                            }
                            break;
                        case "3":
                            System.out.println("You attempt to escape, but the boss's presence is overwhelming. No escape!");
                            break;
                    }
                    if (boss.hp > 0 && !player.isDead()) {
                        int dmg = boss.atk - player.stats[3]/2;
                        if (dmg < 1) dmg = 1;
                        player.takeDamage(dmg);
                    }
                }
                if (player.isDead()) break;
                System.out.println("A reward: a rare accessory, humming with potential. (+2 to all stats)");
                for (int i = 0; i < player.stats.length; i++) player.stats[i] += 2;
                System.out.print("Step back into the light? (y/n): ");
                if (scanner.nextLine().equalsIgnoreCase("y")) break;
            }

            if (bossDefeated || player.isDead()) break;

            // Movement
            System.out.println("Where will you go?");
            if (playerY > 0) System.out.print(" [N]orth");
            if (playerY < size - 1) System.out.print(" [S]outh");
            if (playerX > 0) System.out.print(" [W]est");
            if (playerX < size - 1) System.out.print(" [E]ast");
            System.out.print(" [R]est [C]heck Status\n> ");

            String move = scanner.nextLine().trim().toUpperCase();

            if (move.equals("N") && playerY > 0) {
                playerY--;
                System.out.println("You move northward, branches and shadows shifting as you go.");
            } else if (move.equals("S") && playerY < size - 1) {
                playerY++;
                System.out.println("You push south, the terrain changing beneath your feet.");
            } else if (move.equals("W") && playerX > 0) {
                playerX--;
                System.out.println("You edge west, senses alert for danger.");
            } else if (move.equals("E") && playerX < size - 1) {
                playerX++;
                System.out.println("You advance east, the air thick with anticipation.");
            } else if (move.equals("R")) {
                int heal = player.maxHp/10;
                player.hp += heal;
                if (player.hp > player.maxHp) player.hp = player.maxHp;
                System.out.println("You rest for a moment, regaining " + heal + " HP.");
            } else if (move.equals("C")) {
                player.printStatus();
            } else {
                System.out.println("You hesitate, uncertain. (Invalid move)");
            }
        }
    }

    // Simple inner class for each room on the dungeon map
    private static class DungeonRoom {
        String desc;
        boolean entrance, boss, cleared, visited;

        DungeonRoom(String desc, boolean entrance, boolean boss, boolean cleared, boolean visited) {
            this.desc = desc;
            this.entrance = entrance;
            this.boss = boss;
            this.cleared = cleared;
            this.visited = visited;
        }
    }
}