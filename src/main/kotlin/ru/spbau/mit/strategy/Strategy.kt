package ru.spbau.mit.strategy

import ru.spbau.mit.basic.Creature
import ru.spbau.mit.basic.Pt
import ru.spbau.mit.main.World


/**
 * Class for implementing actor strategy.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
interface Strategy {
    /**
     * Choose action for act.
     *
     * @receiver An instance of [World], visibleWorld by [self].
     * @param self An instance of [Creature] - center unit to act. Other world is just a
     * visible world from his point of view.
     */
    fun World.chooseAction(self: Creature): Action

    /**
     * Stupid always hold strategy.
     */
    object Hold : Strategy {
        /**
         * Choose action for act.
         *
         * @receiver An instance of [World], visibleWorld by [self].
         * @param self An instance of [Creature] - center unit to act. Other world is just a
         * visible world from his point of view.
         */
        override fun World.chooseAction(self: Creature): Action = Action.Move(Pt.Move.HOLD)
    }

    /**
     * Stupid random move strategy.
     */
    object RandomMove : Strategy {
        /**
         * Choose action for act.
         *
         * @receiver An instance of [World], visibleWorld by [self].
         * @param self An instance of [Creature] - center unit to act. Other world is just a
         * visible world from his point of view.
         */
        override fun World.chooseAction(self: Creature): Action =
                Action.Move(Pt.Move.random())
    }
}

