package mortalcombat.fabric;

import mortalcombat.model.Baraka;
import mortalcombat.model.Enemy;

public class BarakaFabric implements EnemyFabricInterface {

    @Override
    public Enemy create() {
        return new Baraka(1, 100, 12);
    }
}
