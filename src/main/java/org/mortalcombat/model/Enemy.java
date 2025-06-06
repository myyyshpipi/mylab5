package org.mortalcombat.model;

/**
 * Класс врага
 */
public class Enemy extends Fighter {
    public Enemy(int level, int health, int damage) {
        super(level, health, damage);
    }

    /**
     * Действие врага
     */
    public void action(int playerActions) {
        action = Action.ATTACK;
    }
}
