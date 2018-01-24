package ru.spbau.mit.strategy

import ru.spbau.mit.basic.Creature
import ru.spbau.mit.basic.Item
import ru.spbau.mit.basic.Pt
import ru.spbau.mit.main.World

/**
 * Simple kb reading strategy.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class PlayerStrategy(private val getCh: () -> Char) : Strategy {
    /**
     * Choose action for act.
     * Here, we read chars from keyboard and transform them into action.
     *
     * @receiver An instance of [World], visibleWorld by [self].
     * @param self An instance of [Creature] - center unit to act. Other world is just a
     * visible world from his point of view.
     */
    override fun World.chooseAction(self: Creature): Action {
        fun Set<Item>.findBy(c: Char): Item? = firstOrNull { it.name.repr == c }

        tailrec fun readAction(): Action = when (getCh()) {
            'q' -> Action.ForceQuit
            'h' -> Action.Move(Pt.Move.HOLD)
            'w' -> Action.Move(Pt.Move.UP)
            'd' -> Action.Move(Pt.Move.RIGHT)
            's' -> Action.Move(Pt.Move.DOWN)
            'a' -> Action.Move(Pt.Move.LEFT)
            'p' -> self.equip.stored.findBy(getCh())?.let { Action.PutOnItem(it) } ?: readAction()
            't' -> self.equip.used.findBy(getCh())?.let { Action.TakeOffItem(it as Item.Gear) }
                    ?: readAction()
            'f' -> self.equip.stored.findBy(getCh())?.let { Action.DropItem(it) } ?: readAction()
            else -> readAction()
        }

        return readAction()
    }
}