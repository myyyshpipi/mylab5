package org.mortalcombat.fabric;

import org.mortalcombat.model.Goro;
import org.mortalcombat.model.Enemy;

/**
 * Класс фабрика для Goro
 */
public class GoroFabric implements EnemyFabricInterface {

    @Override
    public Enemy create() {
        return new Goro(3, 145, 44);
    }
}
