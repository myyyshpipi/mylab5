package org.mortalcombat.fabric;

import org.mortalcombat.model.Enemy;

/**
 * Интерфейс для реализации фабричного метода для доступа к фабричному классу
 */
public interface EnemyFabricInterface {
    Enemy create();
}
