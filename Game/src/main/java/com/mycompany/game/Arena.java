package com.mycompany.game;

import java.util.Random;
import java.util.Stack;

public class Arena {
    String[] levels = {"Bronze", "Silver", "Epic", "Legend", "Mythic", "Champion"};
    String[] botNames = {"Raven", "Wolf", "Crow", "Lion", "Ghost", "Blade", "Storm", "Zero"};
    Random rand = new Random();

    public String getArenaName() {
        return "Death's Arena";
    }

    // The Arena is now the main objective for resurrection
    public boolean startArena(Hero player, java.util.Scanner scanner, Stack<String> deathStack) {
        int numChallengers = 5; // A gauntlet for the final trial
        System.out.println("\n[ARENA: Death's Gauntlet]");
        System.out.println("You must face a series of challengers. Only victory will restore your life.");

        for (int round = 1; round <= numChallengers; round++) {
            String botName = botNames[rand.nextInt(botNames.length)] + " (Challenger " + round + ")";
            HeroType randomType = HeroType.types.get(rand.nextInt(5)+1);
            Hero bot = new Hero(botName, randomType);
            bot.level = Math.max(player.level, 10 + round * 5);
            for (int i = 0; i < bot.stats.length; i++) bot.stats[i] += rand.nextInt(6);

            System.out.println("\nChallenger " + round + ": " + bot.name + " (" + bot.type.name + ")");

            while (!player.isDead() && !bot.isDead()) {
                Hero.printBattleStatus(player, bot);
                System.out.println("\n1. Attack  2. Skill  3. Ultimate  4. Yield");
                String c = scanner.nextLine();
                boolean actionDone = false;
                switch (c) {
                    case "1":
                        actionDone = player.attack(bot);
                        break;
                    case "2":
                    case "3":
                        actionDone = player.useSkillMenu(bot, scanner);
                        break;
                    case "4":
                        System.out.println("You yield. Death laughs, and your story ends here.");
                        player.hp = 0;
                        return false;
                }
                if (bot.isDead()) {
                    System.out.println("You defeated " + bot.name + "!");
                    player.gainExp(50 + round * 10);
                    break;
                }
                int botMove = rand.nextInt(3);
                boolean botAction = false;
                if (botMove == 0) botAction = bot.attack(player);
                else botAction = bot.useSkillMenu(player, scanner);

                if (player.isDead()) {
                    System.out.println("You lost to " + bot.name + "!");
                    return false;
                }
            }
        }
        return !player.isDead();
    }
}