package mortalcombatbversion.model.fabric;


import mortalcombatbversion.model.LiuKang;
import mortalcombatbversion.model.Enemy;

public class LiuKangFabric implements EnemyFabricInterface {

    @Override
    public Enemy create() {
        return new LiuKang(1, 70, 20);
    }
}
