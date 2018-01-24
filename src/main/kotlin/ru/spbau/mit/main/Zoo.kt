package ru.spbau.mit.main

import ru.spbau.mit.basic.Creature
import ru.spbau.mit.basic.Item
import ru.spbau.mit.basic.Unit

/**
 * Basic interface to observe all units.
 * For now, it's just getting set of creatures and items.
 * This is just for the convenience, because usually we apply different policies to observe
 * creatures and items.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
interface Zoo {
    /**
     * All creatures.
     */
    val creatures: Set<Creature>

    /**
     * All items.
     */
    val items: Set<Item>
}

/**
 * The simplest [Zoo] implementation from just [Set] of [Unit] with memoization.
 */
class SimpleZoo(units: Set<Unit>) : Zoo {
    /**
     * All creatures.
     */
    override val creatures: Set<Creature> by lazy { units.filterIsInstance<Creature>().toSet() }

    /**
     * All items.
     */
    override val items: Set<Item> by lazy { units.filterIsInstance<Item>().toSet() }
}