package org.mortalcombat.fabric;


import org.mortalcombat.model.SonyaBlade;
import org.mortalcombat.model.Enemy;

/**
 * Класс фабрика для SonyBlade
 */
public class SonyaBladeFabric implements EnemyFabricInterface {

    @Override
    public Enemy create() {
        return new SonyaBlade(1, 80, 16);
    }

}
