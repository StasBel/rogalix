package ru.spbau.mit.strategy

import ru.spbau.mit.basic.Creature
import ru.spbau.mit.basic.Pt
import ru.spbau.mit.basic.Unit
import ru.spbau.mit.env.Env
import ru.spbau.mit.main.World
import java.util.*

/**
 * Mob strategy to attack and pick items.
 * Has to advance it later.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class MobStrategy : Strategy {
    /**
     * Choose action for act.
     * Here, we read chars from keyboard and transform them into action.
     *
     * @receiver An instance of [World], visibleWorld by [self].
     * @param self An instance of [Creature] - center unit to act. Other world is just a
     * visible world from his point of view.
     */
    override fun World.chooseAction(self: Creature): Action {
        val target = zoo.creatures.firstOrNull { it::class != self::class }
                ?: zoo.items.firstOrNull()
                ?: return Action.Move(Pt.Move.HOLD)
        val action = moveTo(self, target)
        return if (self.params.hp < 20) Action.Move(action.move.opposite())
        else action
    }

    private fun World.moveTo(self: Creature, target: Unit): Action.Move {
        val delta = self.pos - target.pos
        val valid: (Pt) -> Boolean = { env[it] == Env.Tile.FLOOR }

        if (Random().nextBoolean()) return Action.Move(Pt.Move.HOLD)  // Introduce eps-greedy move.

        if (delta.x != 0) {
            if ((delta.x > 0) and valid(self.pos + Pt.Move.LEFT)) return Action.Move(Pt.Move.LEFT)
            if ((delta.x < 0) and valid(self.pos + Pt.Move.RIGHT)) return Action.Move(Pt.Move.RIGHT)
        }

        if (delta.y != 0) {
            if ((delta.y > 0) and valid(self.pos + Pt.Move.UP)) return Action.Move(Pt.Move.UP)
            if ((delta.y < 0) and valid(self.pos + Pt.Move.DOWN)) return Action.Move(Pt.Move.DOWN)
        }

        return Action.Move(Pt.Move.HOLD)
    }

}