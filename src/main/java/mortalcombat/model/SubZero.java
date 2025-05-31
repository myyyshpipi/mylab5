package mortalcombat.model;

import java.util.Random;

import static mortalcombat.model.Action.*;
import static mortalcombat.model.FighterType.WITCH;

/**
 * Subzero - класс мага
 */
public class SubZero extends Enemy {
    public SubZero(int level, int health, int damage) {
        super(level, health, damage);
        this.name = "SubZero (маг)";
        this.image = "src/main/resources/images/Sub-Zero.jpg";
        this.type = WITCH;
    }

    @Override
    public void action(int playerActions) {
        Random random = new Random();
        double randomN = random.nextDouble() * 100;

        if (randomN < 30 + playerActions) {
            action = ATTACK;
        } else if (randomN < 60){
            action = DEFEND;
        } else {
            action = WEAKEN;
        }
    }
}
