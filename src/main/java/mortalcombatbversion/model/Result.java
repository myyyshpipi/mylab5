
package mortalcombatbversion.model;

/**
 * Класс для хранения результатов раунда
 */
public class Result {

    private final String name;
    private final int points;

    public Result(String name, int points) {
        this.name = name;
        this.points = points;
    }

    public String getName() {
        return this.name;
    }

    public int getPoints() {
        return this.points;
    }

}
