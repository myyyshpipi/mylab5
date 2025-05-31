package mortalcombatbversion.model.fabric;

import mortalcombatbversion.model.Goro;
import mortalcombatbversion.model.Enemy;

public class GoroFabric implements EnemyFabricInterface {

    @Override
    public Enemy create() {
        return new Goro(3, 145, 44);
    }
}
