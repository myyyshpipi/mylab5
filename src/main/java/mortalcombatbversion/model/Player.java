package mortalcombatbversion.model;

import static mortalcombatbversion.model.Action.*;
import static mortalcombatbversion.model.FighterType.PLAYER;

/**
 * Player - класс игрока
 */
public class Player extends Fighter {
    private int points;
    private int experience;
    private int nextExperience;
    private int actions;


    public Player(int level, int health, int damage) {
        super(level, health, damage);
        this.points = 0;
        this.experience = 0;
        this.nextExperience = 40;
        this.name = "Kitana";
        this.image = "src/main/resources/images/Kitana.jpg";
        this.type = PLAYER;
    }

    /**
     * Расчет и сохраранение очков действий игрока
     */
    @Override
    public void setAction(Action action) {
        super.setAction(action);

        if (action == ATTACK) actions -= 5;
        else if (action == DEFEND) actions += 5;
        else if (action == WEAKEN) actions -= 3;

        actions = Math.min(actions, 20);
        actions = Math.max(actions, -20);
    }

    public int getActions() {
        return actions;
    }

    public int getPoints() {
        return this.points;
    }

    public int getExperience() {
        return this.experience;
    }

    public void setNextExperience(int experience) {
        this.nextExperience = experience;
    }

    public int getNextExperience() {
        return this.nextExperience;
    }

    /**
     * Добаавление очков игроку за победу над врагом
     */
    public void addPoints() {
        switch (level) {
            case 0:
                experience += 20;
                points += 25 + health / 4;
                break;
            case 1:
                experience += 25;
                points += 30 + health / 4;
                break;
            case 2:
                experience += 30;
                points += 35 + health / 4;
                break;
            case 3:
                experience += 40;
                points += 45 + health / 4;
                break;
            case 4:
                experience += 50;
                points += 55 + health / 4;
                break;
        }
    }

    /**
     * Добавление очков игрока за победу над боссом
     */
    public void addPointsBoss() {
        experience += level * 10 + 10;
        points += level * 20 + health / 2;
    }
}
