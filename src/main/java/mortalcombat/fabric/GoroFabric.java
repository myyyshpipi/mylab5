package mortalcombat.fabric;

import mortalcombat.model.Goro;
import mortalcombat.model.Enemy;

public class GoroFabric implements EnemyFabricInterface {

    @Override
    public Enemy create() {
        return new Goro(3, 145, 44);
    }
}
