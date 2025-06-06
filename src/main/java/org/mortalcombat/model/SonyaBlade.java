package org.mortalcombat.model;

import java.util.Random;

import static org.mortalcombat.model.Action.*;
import static org.mortalcombat.model.FighterType.SOLDIER;

/**
 * SonyaBlade - класс солдата
 */
public class SonyaBlade extends Enemy {
    public SonyaBlade(int level, int health, int damage) {
        super(level, health, damage);
        this.name = "Sonya Blade (солдат)";
        this.image = "images/Sonya Blade.jpg";
        this.type = SOLDIER;
    }

    @Override
    public void action(int playerActions) {
        Random random = new Random();
        double randomN = random.nextDouble() * 100;

        if (randomN < 50 + playerActions) {
            action = ATTACK;
        } else {
            action = DEFEND;
        }
    }
}
