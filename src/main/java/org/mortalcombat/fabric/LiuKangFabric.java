package org.mortalcombat.fabric;


import org.mortalcombat.model.LiuKang;
import org.mortalcombat.model.Enemy;

/**
 * Класс фабрика для LiuKang
 */
public class LiuKangFabric implements EnemyFabricInterface {

    @Override
    public Enemy create() {
        return new LiuKang(1, 70, 20);
    }
}
