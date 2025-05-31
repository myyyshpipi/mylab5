package mortalcombat.fabric;

import mortalcombat.model.ShaoKahn;
import mortalcombat.model.Enemy;

public class ShaoKahnFabric implements EnemyFabricInterface {

    @Override
    public Enemy create() {
        return new ShaoKahn(3, 100, 30);
    }
}
