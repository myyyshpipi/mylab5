package org.mortalcombat.fabric;


import org.mortalcombat.model.SubZero;
import org.mortalcombat.model.Enemy;

/**
 * Класс фабрика для SubZero
 */
public class SubZeroFabric implements EnemyFabricInterface {
    @Override
    public Enemy create() {
        return new SubZero(1, 60, 16);
    }

}
