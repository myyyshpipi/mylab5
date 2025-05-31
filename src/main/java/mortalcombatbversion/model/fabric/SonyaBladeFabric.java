package mortalcombatbversion.model.fabric;


import mortalcombatbversion.model.SonyaBlade;
import mortalcombatbversion.model.Enemy;

public class SonyaBladeFabric implements EnemyFabricInterface {

    @Override
    public Enemy create() {
        return new SonyaBlade(1, 80, 16);
    }

}
