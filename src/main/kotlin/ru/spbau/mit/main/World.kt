package ru.spbau.mit.main

import ru.spbau.mit.basic.Creature
import ru.spbau.mit.basic.Pt
import ru.spbau.mit.basic.Unit
import ru.spbau.mit.env.Env

/**
 * State of the world, observable by someone.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
interface World {
    /**
     * Observable [Env].
     */
    val env: Env

    /**
     * Observable [Zoo].
     */
    val zoo: Zoo

    /**
     * Static extensions.
     */
    val Unit.pos: Pt
    val Creature.status: Creature.Status
    val Creature.equip: Creature.Equip
    val Creature.params: Creature.Params
}