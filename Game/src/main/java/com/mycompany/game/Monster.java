package com.mycompany.game;

import java.util.HashMap;
import java.util.Random;

public class Monster extends Entity {
    public int atk, def, level;
    public String skill;
    public boolean isBoss;

    private static final HashMap<String, String[]> monsterVariants = new HashMap<>();
    private static final HashMap<String, String> bossNames = new HashMap<>();
    static {
        monsterVariants.put("Slime", new String[]{"Green Slime", "Spiked Slime", "Gelatinous Slime"});
        bossNames.put("Slime", "King Slime");

        monsterVariants.put("Goblin", new String[]{"Goblin Thug", "Goblin Archer", "Goblin Shaman"});
        bossNames.put("Goblin", "Goblin Chieftain");

        monsterVariants.put("Spider", new String[]{"Thorn Spider", "Venom Spider", "Shadow Spider"});
        bossNames.put("Spider", "Spider Matriarch");

        monsterVariants.put("Wolf", new String[]{"Grey Wolf", "Dire Wolf", "Frost Wolf"});
        bossNames.put("Wolf", "Great Alpha Wolf");

        monsterVariants.put("Orc", new String[]{"Orc Grunt", "Orc Mage", "Orc Brute"});
        bossNames.put("Orc", "Orc Warlord");

        monsterVariants.put("Undead", new String[]{"Skeleton Warrior", "Zombie", "Ghoul"});
        bossNames.put("Undead", "Lich King");

        monsterVariants.put("Dragon", new String[]{"Fire Drake", "Lesser Wyvern", "Drake Whelp"});
        bossNames.put("Dragon", "Ancient Red Dragon");

        monsterVariants.put("Demon", new String[]{"Demon Imp", "Hellhound", "Abyssal Guard"});
        bossNames.put("Demon", "Archdemon Belial");

        monsterVariants.put("Angel", new String[]{"Cherub", "Seraph", "Guardian Angel"});
        bossNames.put("Angel", "Fallen Seraph");
    }

    public Monster(String name, int hp, int atk, int def, int level, String skill, boolean isBoss) {
        this.name = name;
        this.hp = this.maxHp = hp;
        this.atk = atk;
        this.def = def;
        this.level = level;
        this.skill = skill;
        this.isBoss = isBoss;
        this.stats = new int[]{atk, 0, 0, def, 0};
    }

    public static Monster generate(String monsterType, int playerLevel) {
        String[] variants = monsterVariants.getOrDefault(monsterType, new String[]{monsterType});
        String name = variants[new Random().nextInt(variants.length)];
        int level = playerLevel + (int)(Math.random()*3);
        int hp = 50 + playerLevel * 5 + (int)(Math.random()*20);
        int atk = 7 + playerLevel * 2 + (int)(Math.random()*5);
        int def = 3 + playerLevel + (int)(Math.random()*3);
        String[] skills = {"Poison Bite", "Swift Strike", "Web Shot", "Fury", "Venom Cloud", "Wild Charge"};
        String s = skills[new Random().nextInt(skills.length)];
        return new Monster(name, hp, atk, def, level, s, false);
    }

    public static Monster boss(String monsterType, int minLevel) {
        String name = bossNames.getOrDefault(monsterType, monsterType + " Boss");
        int hp = 200 + minLevel*12;
        int atk = 30 + minLevel*3;
        int def = 15 + minLevel*2;
        int level = minLevel + 10;
        String[] bossSkills = {"Mega Smash", "Death Roar", "Ultimate Venom", "Hellfire", "Judgement"};
        String skill = bossSkills[new Random().nextInt(bossSkills.length)];
        return new Monster(name, hp, atk, def, level, skill, true);
    }
}