package mortalcombat.fabric;


import mortalcombat.model.SubZero;
import mortalcombat.model.Enemy;

public class SubZeroFabric implements EnemyFabricInterface {
    @Override
    public Enemy create() {
        return new SubZero(1, 60, 16);
    }

}
