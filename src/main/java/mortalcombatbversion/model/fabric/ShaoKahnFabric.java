package mortalcombatbversion.model.fabric;

import mortalcombatbversion.model.ShaoKahn;
import mortalcombatbversion.model.Enemy;

public class ShaoKahnFabric implements EnemyFabricInterface {

    @Override
    public Enemy create() {
        return new ShaoKahn(3, 100, 30);
    }
}
