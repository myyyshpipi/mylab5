package org.mortalcombat.model;

import java.util.Random;

import static org.mortalcombat.model.Action.*;
import static org.mortalcombat.model.FighterType.TANK;

/**
 * Реализаация для Baraka (танк)
 */
public class Baraka extends Enemy {
    public Baraka(int level, int health, int damage){
        super (level, health, damage);
        this.name = "Baraka (танк)";
        this.image = "images/Baraka.jpg";
        this.type = TANK;
    }

    @Override
    public void action(int playerActions) {
        Random random = new Random();
        double randomN = random.nextDouble() * 100;

        if (randomN < 30 + playerActions) {
            action = ATTACK;
        } else {
            action = DEFEND;
        }
    }
}
