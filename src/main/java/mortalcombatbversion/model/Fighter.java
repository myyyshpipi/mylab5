package mortalcombatbversion.model;

import static mortalcombatbversion.model.Action.*;

/**
 * Основной класс для создания бойцов
 */
public class Fighter {
    protected int level;
    protected int health;
    protected int maxHealth;
    protected int damage;
    protected Action action;
    protected String image;
    protected String name;
    protected FighterType type;
    protected int vulnerable;
    private double damageDebuf = 1;
    private double incomeDebuf = 1;

    public Fighter(int level, int health, int damage) {
        this.level = level;
        this.health = health;
        this.damage = damage;
        this.action = ATTACK;
        this.maxHealth = health;
        vulnerable = 0;
    }

    /**
     * Повышение уровня бойца
     */
    public void levelUP() {
        level++;
    }

    /**
     * Повышение здоровье бойца в зависимости от его уровня
     */
    public void levelHP(Player player) {
        int hp = switch (player.getLevel()) {
            case 1 -> 32;
            case 2 -> 30;
            case 3 -> 23;
            case 4 -> 25;
            default -> 31;
        };
        setMaxHealth(getMaxHealth() * hp / 100);
        health = maxHealth;
    }

    /**
     * Изменение уровня урона бойца в зависимости от его уровня</p>
     */
    public void levelDMG(Player player) {
        int damage = switch (player.getLevel()) {
            case 1 -> 25;
            case 2 -> 20;
            case 3 -> 24;
            case 4 -> 26;
            default -> 30;
        };
        setDamage(getDamage() * damage / 100);
        health = maxHealth;
    }

    /**
     * Расчет получаемого урона бойцом
     */
    public void takeDamage(int damage) {
        damage = (int) (damage * incomeDebuf);
        if (this.health > damage) this.health -= damage;
        else this.health = 0;
    }

    /**
     * Расчет исцеления бойца
      */
    public void heal(int heal) {
        if (this.maxHealth < this.health + heal) this.health = maxHealth;
        else this.health += heal;
    }

    /**
     * Ослабление бойца
     */
    public void setVulnerable() {
        vulnerable = level + 1;
        damageDebuf = damageDebuf * 0.5;
        incomeDebuf = 1.25;
    }

    /**
     * Обработка действия бойца
     */
    public void setAction(Action action) {
        this.action = action;
        if (vulnerable == 1) {
            vulnerable = 0;
            damageDebuf = damageDebuf / 0.5;
            incomeDebuf = 1;
        } else if (vulnerable > 0) vulnerable--;
    }

    /**
     * Сброс ослабления
     */
    public void resetVulnerable() {
        vulnerable = 0;
        damageDebuf = damageDebuf / 0.5;
        incomeDebuf = 1;
    }

    /**
     * Повышенный урон если противник ослаблен
     */
    public void buff() {
        damageDebuf *= 1.15;
    }

    /**
     * проверка ослаблен ли боец
     */
    public boolean isVulnerable() {
        return vulnerable > 0;
    }

    /**
     * Восстановление здоровьбя и готовность бойца к бою
     */
    public void reset() {
        health = maxHealth;
        action = ATTACK;
    }

    public void setMaxHealth(int health) {
        this.maxHealth += health;
    }

    public int getLevel() {
        return this.level;
    }

    public int getHealth() {
        return this.health;
    }

    public int getDamage() {
        return (int) (this.damage * damageDebuf);
    }

    public Action getAction() {
        return this.action;
    }

    public int getMaxHealth() {
        return this.maxHealth;
    }

    public String getImage() {
        return image;
    }

    public FighterType getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setDamage(int damage) {
        this.damage += damage;
    }
}
