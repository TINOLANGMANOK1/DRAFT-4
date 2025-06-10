package com.mycompany.game;

public abstract class Entity {
    public String name;
    public int hp, maxHp;
    public int[] stats = new int[5]; // [STR, AGI, INT, VIT, LUK]

    public boolean isDead() {
        return hp <= 0;
    }

    public void takeDamage(int dmg) {
        this.hp -= dmg;
        if(this.hp < 0) this.hp = 0;
        System.out.println(name + " took " + dmg + " damage! (" + hp + "/" + maxHp + " HP)");
    }
}