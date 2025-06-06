package org.mortalcombat.fabric;


import org.mortalcombat.model.LiuKang;
import org.mortalcombat.model.Enemy;

public class LiuKangFabric implements EnemyFabricInterface {

    @Override
    public Enemy create() {
        return new LiuKang(1, 70, 20);
    }
}
