package org.mortalcombat.model;

import java.util.Random;

import static org.mortalcombat.model.Action.*;
import static org.mortalcombat.model.FighterType.BOSS;

/**
 * Goro - это класс босса
 */
public class Goro extends Enemy {
    public Goro(int level, int health, int damage) {
        super(level, health, damage);
        this.name = "Goro (босс)";
        this.image = "images/Goro.jpg";
        this.type = BOSS;
    }

    @Override
    public void action(int playerActions) {
        Random random = new Random();
        double randomN = random.nextDouble() * 90;

        if (randomN < 40 + playerActions) {
            action = ATTACK;
        } else if (randomN < 80){
            action = DEFEND;
        } else {
            action = REGENERATE;
        }
    }
}
