package mortalcombatbversion.model.fabric;


import mortalcombatbversion.model.SubZero;
import mortalcombatbversion.model.Enemy;

public class SubZeroFabric implements EnemyFabricInterface {
    @Override
    public Enemy create() {
        return new SubZero(1, 60, 16);
    }

}
