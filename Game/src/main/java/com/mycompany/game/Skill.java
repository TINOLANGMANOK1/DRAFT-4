package com.mycompany.game;

public class Skill {
    public String name, desc;
    public int power, statIndex; // statIndex: 0-STR, 1-AGI, 2-INT, 3-VIT, 4-LUK
    public int manaCost;
    public int cooldown;

    public Skill(String name, String desc, int power, int statIndex, int manaCost, int cooldown) {
        this.name = name;
        this.desc = desc;
        this.power = power;
        this.statIndex = statIndex;
        this.manaCost = manaCost;
        this.cooldown = cooldown;
    }
}