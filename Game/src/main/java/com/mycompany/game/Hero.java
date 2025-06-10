package com.mycompany.game;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class Hero extends Entity {
    public HeroType type;
    public int level;
    public int exp;
    public int mana;
    public int maxMana;
    public LinkedList<Skill> skills = new LinkedList<>();
    public boolean ascended = false; // Ascension is not used in the current game flow.

    private Map<String, Integer> skillCooldowns = new HashMap<>();
    private int battleCryTurns = 0;
    private int barrierTurns = 0;
    private int magicShieldTurns = 0;
    private int evadeTurns = 0;

    public Hero(String name, HeroType type) {
        this.name = name;
        this.type = type;
        this.level = 1;
        this.exp = 0;
        this.maxHp = 100 + type.baseStats[3] * 10;
        this.hp = maxHp;
        this.maxMana = 50 + type.baseStats[2] * 5;
        this.mana = maxMana;
        this.stats = Arrays.copyOf(type.baseStats, 5);
        this.skills = new LinkedList<>(type.getSkills());
        this.ascended = false;
        for (Skill s : skills) skillCooldowns.put(s.name, 0);
        skillCooldowns.put(type.ultimate.name, 0);
    }

    public void printStatus() {
        System.out.println();
        System.out.println("--- STATUS ---");
        System.out.printf("Name    : %-20s\n", name);
        System.out.printf("Class   : %-20s\n", type.name + (ascended ? " [Ascended]" : ""));
        System.out.printf("Level   : %-3d\n", level);
        System.out.printf("HP      : %-3d/%-3d\n", hp, maxHp);
        System.out.printf("Mana    : %-3d/%-3d\n", mana, maxMana);
        System.out.printf("Exp     : %-3d/%-3d\n", exp, (level*10 + 50));
        System.out.println("Stats   : STR " + stats[0] + "  AGI " + stats[1] + "  INT " + stats[2] + "  VIT " + stats[3] + "  LUK " + stats[4]);
        System.out.println("Skills  :");
        for (Skill s : skills)
            System.out.printf("  - %-15s : %s (Mana: %d, CD: %d, Ready: %s)\n",
                s.name, s.desc, s.manaCost, s.cooldown, (skillCooldowns.get(s.name) == 0 ? "Yes" : "No ("+skillCooldowns.get(s.name)+" left)"));
        System.out.printf("Ultimate: %-15s (Mana: %d, CD: %d, Ready: %s)\n", type.ultimate.name, type.ultimate.manaCost, type.ultimate.cooldown, (skillCooldowns.get(type.ultimate.name) == 0 ? "Yes" : "No ("+skillCooldowns.get(type.ultimate.name)+" left)"));
        System.out.println("Passive : " + type.passive);
        System.out.println();
    }

    public String getHeroTypeName() {
        return type.name;
    }

    public void gainExp(int amount) {
        exp += amount;
        while (exp >= level*10 + 50) {
            exp -= (level*10 + 50);
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        for (int i = 0; i < stats.length; i++) stats[i] += 1 + (int)(Math.random()*2);
        maxHp += 10 + stats[3];
        hp = maxHp;
        maxMana += 5 + stats[2];
        mana = maxMana;
        System.out.println("[System] Leveled Up! Now level " + level + ".");
        DeathDialogue.onLevelUp(level);
        // Ascension system removed for current K-Drama Death's Game flow.
    }

    // Ascension logic removed to match main story/game flow

    public boolean attack(Entity opponent) {
        decrementCooldowns();
        passiveEffects();
        int bonus = 0;
        if (type.name.equals("Warrior") && hp < maxHp/2) bonus = (int)(stats[0] * 0.1); // Berserker's Rage
        int dmg = stats[0] + (int)(Math.random()*5) + 1 + bonus;
        if (battleCryTurns > 0) dmg += 3; // Battle Cry buff
        opponent.hp -= dmg;
        System.out.println(this.name + " hit " + opponent.name + " for " + dmg + " damage! (" + opponent.hp + "/" + opponent.maxHp + " HP)");
        return opponent.hp <= 0;
    }

    public boolean useSkillMenu(Entity opponent, Scanner scanner) {
        decrementCooldowns();
        passiveEffects();
        System.out.println("Choose a skill:");
        int i = 1;
        for (Skill s : skills) {
            System.out.printf("  %d. %-15s (Mana: %d, CD: %d, Ready: %s)\n", i, s.name, s.manaCost, s.cooldown, (skillCooldowns.get(s.name) == 0 ? "Yes" : "No ("+skillCooldowns.get(s.name)+" left)"));
            i++;
        }
        System.out.printf("  %d. %-15s (Mana: %d, CD: %d, Ready: %s)\n", i, type.ultimate.name, type.ultimate.manaCost, type.ultimate.cooldown, (skillCooldowns.get(type.ultimate.name) == 0 ? "Yes" : "No ("+skillCooldowns.get(type.ultimate.name)+" left)"));
        System.out.print("> ");
        int choice = 0;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            choice = 1;
        }
        Skill chosenSkill;
        boolean isUltimate = false;
        if (choice >= 1 && choice <= skills.size()) {
            chosenSkill = skills.get(choice-1);
        } else if (choice == skills.size()+1) {
            chosenSkill = type.ultimate;
            isUltimate = true;
        } else {
            System.out.println("Invalid choice.");
            return false;
        }
        if (skillCooldowns.get(chosenSkill.name) > 0) {
            System.out.println("That skill is still on cooldown (" + skillCooldowns.get(chosenSkill.name) + " turns left).");
            return false;
        }
        if (mana < chosenSkill.manaCost) {
            System.out.println("Not enough mana!");
            return false;
        }

        mana -= chosenSkill.manaCost;
        skillCooldowns.put(chosenSkill.name, chosenSkill.cooldown + 1);

        // Apply skill effect
        if (type.name.equals("Warrior")) {
            if (chosenSkill.name.equals("Slash")) {
                int dmg = chosenSkill.power + stats[chosenSkill.statIndex];
                opponent.hp -= dmg;
                System.out.println(name + " used Slash! (" + dmg + " damage)");
            } else if (chosenSkill.name.equals("Guard")) {
                magicShieldTurns = 1;
                System.out.println(name + " used Guard! Most damage will be blocked next turn.");
            } else if (chosenSkill.name.equals("Battle Cry")) {
                battleCryTurns = 3;
                System.out.println(name + " used Battle Cry! STR increased for 3 turns.");
            } else if (chosenSkill.name.equals("Berserker Strike")) {
                int dmg = chosenSkill.power + (stats[0] * 2);
                opponent.hp -= dmg;
                System.out.println(name + " used Berserker Strike! (" + dmg + " damage)");
            }
        }
        else if (type.name.equals("Mage")) {
            if (chosenSkill.name.equals("Fireball")) {
                int dmg = chosenSkill.power + stats[chosenSkill.statIndex];
                opponent.hp -= dmg;
                System.out.println(name + " used Fireball! (" + dmg + " damage)");
            } else if (chosenSkill.name.equals("Ice Shard")) {
                int dmg = chosenSkill.power + stats[chosenSkill.statIndex];
                opponent.hp -= dmg;
                System.out.println(name + " used Ice Shard! (" + dmg + " damage)");
            } else if (chosenSkill.name.equals("Magic Shield")) {
                magicShieldTurns = 1;
                System.out.println(name + " used Magic Shield! All damage blocked for 1 turn.");
            } else if (chosenSkill.name.equals("Meteor")) {
                int dmg = chosenSkill.power + (stats[2] * 2);
                opponent.hp -= dmg;
                System.out.println(name + " used Meteor! (" + dmg + " damage)");
            }
        }
        else if (type.name.equals("Archer")) {
            if (chosenSkill.name.equals("Arrow Shot")) {
                int dmg = chosenSkill.power + stats[chosenSkill.statIndex];
                opponent.hp -= dmg;
                System.out.println(name + " used Arrow Shot! (" + dmg + " damage)");
            } else if (chosenSkill.name.equals("Double Shot")) {
                int dmg = (chosenSkill.power + stats[chosenSkill.statIndex]) * 2;
                opponent.hp -= dmg;
                System.out.println(name + " used Double Shot! (" + dmg + " damage)");
            } else if (chosenSkill.name.equals("Trap")) {
                int dmg = chosenSkill.power + stats[chosenSkill.statIndex];
                opponent.hp -= dmg;
                System.out.println(name + " set a Trap! (" + dmg + " damage)");
            } else if (chosenSkill.name.equals("Rain of Arrows")) {
                int dmg = chosenSkill.power + (stats[1] * 2);
                opponent.hp -= dmg;
                System.out.println(name + " used Rain of Arrows! (" + dmg + " damage)");
            }
        }
        else if (type.name.equals("Healer")) {
            if (chosenSkill.name.equals("Heal")) {
                int heal = chosenSkill.power + stats[chosenSkill.statIndex];
                heal = (int)(heal * 1.1); // passive
                hp = Math.min(maxHp, hp + heal);
                System.out.println(name + " used Heal! Restored " + heal + " HP.");
            } else if (chosenSkill.name.equals("Barrier")) {
                barrierTurns = 2;
                System.out.println(name + " used Barrier! Damage reduced for 2 turns.");
            } else if (chosenSkill.name.equals("Smite")) {
                int dmg = chosenSkill.power + stats[chosenSkill.statIndex];
                opponent.hp -= dmg;
                System.out.println(name + " used Smite! (" + dmg + " damage)");
            } else if (chosenSkill.name.equals("Divine Blessing")) {
                hp = maxHp;
                System.out.println(name + " used Divine Blessing! Fully healed.");
            }
        }
        else if (type.name.equals("Rogue")) {
            if (chosenSkill.name.equals("Backstab")) {
                int dmg = chosenSkill.power + stats[chosenSkill.statIndex] + (int)(stats[4]*0.5);
                opponent.hp -= dmg;
                System.out.println(name + " used Backstab! (" + dmg + " damage)");
            } else if (chosenSkill.name.equals("Poison")) {
                int dmg = chosenSkill.power + stats[chosenSkill.statIndex];
                opponent.hp -= dmg;
                System.out.println(name + " used Poison! (" + dmg + " damage, poison effect not implemented)");
            } else if (chosenSkill.name.equals("Evade")) {
                evadeTurns = 1;
                System.out.println(name + " used Evade! Next attack will be dodged.");
            } else if (chosenSkill.name.equals("Assassinate")) {
                int dmg = chosenSkill.power + (stats[4] * 2);
                opponent.hp -= dmg;
                System.out.println(name + " used Assassinate! (" + dmg + " damage)");
            }
        }
        return opponent.hp <= 0;
    }

    public void takeDamage(int dmg) {
        if (magicShieldTurns > 0) {
            System.out.println(name + "'s shield blocks all damage!");
            magicShieldTurns--;
            return;
        }
        if (barrierTurns > 0) {
            dmg = dmg / 2;
            System.out.println(name + "'s barrier reduces the damage!");
            barrierTurns--;
        }
        if (evadeTurns > 0) {
            System.out.println(name + " evaded the attack!");
            evadeTurns--;
            return;
        }
        this.hp -= dmg;
        if(this.hp < 0) this.hp = 0;
        System.out.println(name + " took " + dmg + " damage! (" + hp + "/" + maxHp + " HP)");
    }

    private void decrementCooldowns() {
        for (String k : skillCooldowns.keySet())
            if (skillCooldowns.get(k) > 0)
                skillCooldowns.put(k, skillCooldowns.get(k) - 1);
        if (battleCryTurns > 0) battleCryTurns--;
        if (barrierTurns > 0) barrierTurns--;
        if (magicShieldTurns > 0) magicShieldTurns--;
        if (evadeTurns > 0) evadeTurns--;
    }

    private void passiveEffects() {
        if (type.name.equals("Mage")) {
            mana = Math.min(maxMana, mana + 5);
        }
    }

    public boolean fightDeath() {
        int myPower = level * Arrays.stream(stats).sum();
        int deathPower = 15000;
        if (myPower > deathPower) {
            System.out.println("You defeated Death!");
            return true;
        }
        System.out.println("You lost to Death.");
        return false;
    }

    // Print a clear, readable turn interface
    public static void printBattleStatus(Hero player, Entity enemy) {
        String enemyName;
        int enemyHp, enemyMaxHp, enemyLevel;
        if (enemy == null) {
            enemyName = "(none)";
            enemyHp = 0;
            enemyMaxHp = 0;
            enemyLevel = 0;
        } else {
            enemyName = enemy.name;
            enemyHp = enemy.hp;
            enemyMaxHp = enemy.maxHp;
            if (enemy instanceof Monster) {
                enemyLevel = ((Monster)enemy).level;
            } else if (enemy instanceof Hero) {
                enemyLevel = ((Hero)enemy).level;
            } else {
                enemyLevel = 0;
            }
        }
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("PLAYER");
        System.out.printf("  Name : %-28s Lv: %-3d\n", player.name, player.level);
        System.out.printf("  HP   : %3d/%-3d        Mana: %3d/%-3d\n", player.hp, player.maxHp, player.mana, player.maxMana);
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("ENEMY");
        System.out.printf("  Name : %-28s Lv: %-3d\n", enemyName, enemyLevel);
        System.out.printf("  HP   : %3d/%-3d\n", enemyHp, enemyMaxHp);
        System.out.println("-----------------------------------------------------------------------");
    }
}