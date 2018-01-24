package ru.spbau.mit.gen

import ru.spbau.mit.basic.Creature
import ru.spbau.mit.basic.Pt

/**
 * Abstraction for sequential level generation.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
interface LevelGenerator<out T> {
    /**
     * Initial level.
     */
    fun start(): T

    /**
     * Next after [self] finished at [portalAt].
     */
    fun nextAfter(self: Creature, portalAt: Pt): T
}