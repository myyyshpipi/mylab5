package mortalcombat.fabric;


import mortalcombat.model.LiuKang;
import mortalcombat.model.Enemy;

public class LiuKangFabric implements EnemyFabricInterface {

    @Override
    public Enemy create() {
        return new LiuKang(1, 70, 20);
    }
}
