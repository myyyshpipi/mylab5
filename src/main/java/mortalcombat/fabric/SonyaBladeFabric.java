package mortalcombat.fabric;


import mortalcombat.model.SonyaBlade;
import mortalcombat.model.Enemy;

public class SonyaBladeFabric implements EnemyFabricInterface {

    @Override
    public Enemy create() {
        return new SonyaBlade(1, 80, 16);
    }

}
