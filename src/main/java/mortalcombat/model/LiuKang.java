package mortalcombat.model;

import java.util.Random;

import static mortalcombat.model.Action.ATTACK;
import static mortalcombat.model.Action.DEFEND;
import static mortalcombat.model.FighterType.FIGHTER;

/**
 * LiuKang - класс бойца
 */
public class LiuKang extends Enemy {

    public LiuKang(int level, int health, int damage) {
        super(level, health, damage);
        this.name = "Liu Kang";
        this.image = "src/main/resources/images/Baraka.jpg";
        this.type = FIGHTER;
    }

    @Override
    public void action(int playerActions) {
        Random random = new Random();
        double randomN = random.nextDouble() * 100;

        if (randomN < 80 + playerActions) {
            action = ATTACK;
        } else {
            action = DEFEND;
        }
    }
}
