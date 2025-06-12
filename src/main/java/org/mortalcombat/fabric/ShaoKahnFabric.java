package org.mortalcombat.fabric;

import org.mortalcombat.model.ShaoKahn;
import org.mortalcombat.model.Enemy;

/**
 * Класс фабрика для ShaoKahn
 */
public class ShaoKahnFabric implements EnemyFabricInterface {

    @Override
    public Enemy create() {
        return new ShaoKahn(3, 100, 30);
    }
}
