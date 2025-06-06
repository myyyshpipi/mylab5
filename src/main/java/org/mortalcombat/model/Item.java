package org.mortalcombat.model;

/**
 * Класс предметов для игрока
 */
public class Item {
    private final String name;
    private int amount;

    public Item(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return this.name;
    }

    public int getAmount() {
        return this.amount;
    }

    public void increaseAmount() {
        this.amount++;
    }

    public void decreaseAmount() {
        this.amount--;
    }
}
