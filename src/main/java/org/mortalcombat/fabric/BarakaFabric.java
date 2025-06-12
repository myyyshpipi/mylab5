package org.mortalcombat.fabric;

import org.mortalcombat.model.Baraka;
import org.mortalcombat.model.Enemy;

/**
 * Класс фабрика для Baraka
 */
public class BarakaFabric implements EnemyFabricInterface {

    @Override
    public Enemy create() {
        return new Baraka(1, 100, 12);
    }
}
