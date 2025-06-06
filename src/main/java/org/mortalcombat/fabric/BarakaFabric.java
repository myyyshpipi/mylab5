package org.mortalcombat.fabric;

import org.mortalcombat.model.Baraka;
import org.mortalcombat.model.Enemy;

public class BarakaFabric implements EnemyFabricInterface {

    @Override
    public Enemy create() {
        return new Baraka(1, 100, 12);
    }
}
