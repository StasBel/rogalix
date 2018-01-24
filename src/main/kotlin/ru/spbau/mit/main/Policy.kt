package ru.spbau.mit.main

import ru.spbau.mit.basic.Creature
import ru.spbau.mit.strategy.MobStrategy
import ru.spbau.mit.strategy.PlayerStrategy
import ru.spbau.mit.strategy.Strategy

/**
 * Policy = way to assign strategy to as creature.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
interface Policy {
    /**
     * Creature -> Strategy.
     */
    operator fun get(creature: Creature): Strategy
}

/**
 * All hold.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class HoldPolicy : Policy {
    /**
     * Creature -> Strategy.
     */
    override fun get(creature: Creature): Strategy = Strategy.Hold
}


/**
 * Reading from kb.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class KeyboardPlayerPolicy(private val getCh: () -> Char) : Policy {
    /**
     * Creature -> Strategy.
     */
    override fun get(creature: Creature): Strategy = when (creature) {
        is Creature.Player -> PlayerStrategy(getCh)
        is Creature.Mob -> MobStrategy()
    }
}

/**
 * Memoize to be able to learn.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class MemoizePolicy(private val observedPolicy: Policy) : Policy {
    private val calculated: MutableMap<Creature, Strategy> = mutableMapOf()

    /**
     * Creature -> Strategy.
     */
    override fun get(creature: Creature): Strategy =
            calculated.getOrPut(creature, { observedPolicy[creature] })
}