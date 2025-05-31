package mortalcombatbversion.model.fabric;

import mortalcombatbversion.model.Baraka;
import mortalcombatbversion.model.Enemy;

public class BarakaFabric implements EnemyFabricInterface {

    @Override
    public Enemy create() {
        return new Baraka(1, 100, 12);
    }
}
