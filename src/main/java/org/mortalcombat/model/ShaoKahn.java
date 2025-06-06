package org.mortalcombat.model;

import java.util.Random;

import static org.mortalcombat.model.Action.*;
import static org.mortalcombat.model.FighterType.BOSS;

/**
 * ShaoKahn - класс босса
 */
public class ShaoKahn extends Enemy {
    public ShaoKahn(int level, int health, int damage) {
        super(level, health, damage);
        this.name = "Shao Kahn (босс)";
        this.image = "images/Shao Kahn.jpg";
        this.type = BOSS;
    }

    @Override
    public void action(int playerActions) {
        Random random = new Random();
        double randomN = random.nextDouble() * 100;

        if (randomN < 40 + playerActions) {
            action = ATTACK;
        } else if (randomN < 80){
            action = DEFEND;
        } else {
            action = REGENERATE;
        }
    }
}
