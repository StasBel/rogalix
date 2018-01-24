package ru.spbau.mit.strategy

import ru.spbau.mit.basic.Item
import ru.spbau.mit.basic.Pt

/**
 * Action to perform by actor.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
sealed class Action {
    /**
     * Force quitting.
     */
    object ForceQuit : Action()

    /**
     * Moving.
     */
    class Move(val move: Pt.Move) : Action()

    /**
     * Putting item on.
     */
    class PutOnItem(val item: Item) : Action()

    /**
     * Taking item off.
     */
    class TakeOffItem(val item: Item.Gear) : Action()

    /**
     * Dropping item.
     */
    class DropItem(val item: Item) : Action()
}