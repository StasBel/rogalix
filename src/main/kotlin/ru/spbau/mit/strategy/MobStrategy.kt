package ru.spbau.mit.strategy

import ru.spbau.mit.game.Game
import ru.spbau.mit.map.CellType
import ru.spbau.mit.map.Map
import ru.spbau.mit.world.Creature
import ru.spbau.mit.world.CreatureStatus
import ru.spbau.mit.world.Move
import ru.spbau.mit.world.World

/**
 * Simple mob strategy moving straight to seeing enemy.
 * @author belaevstanislav
 */
class MobStrategy : Strategy {
    /**
     * Doing a simple act of moving straight to first enemy.
     * @param self an instance of [Creature] to act
     * @param world an instance of [World] to observe
     * @param map an instance of [Map] using to work with mark
     * @param game an instance of [Game] using to get some consts
     * @return an instance of [Action] as a result of doing strategy
     */
    override fun act(self: Creature, world: World, map: Map, game: Game): Action {
        return world.creatures.filter {
            c ->
            (c.creatureType != self.creatureType)
                    .and(c.status == CreatureStatus.ALIVE)
        }.firstOrNull()?.let {
            val xM: Move = (self.pos.x - it.pos.x).let {
                if (it < 0) {
                    Move.DOWN
                } else if (it == 0) {
                    Move.HOLD
                } else {
                    Move.UP
                }
            }
            if (xM == Move.HOLD || map.getCellType(self.pos + xM.delta) == CellType.WALL) {
                val yM: Move = (self.pos.y - it.pos.y).let {
                    if (it < 0) {
                        Move.RIGHT
                    } else if (it == 0) {
                        Move.HOLD
                    } else {
                        Move.LEFT
                    }
                }
                return Action.Move(yM)
            } else {
                return Action.Move(xM)
            }
        } ?: Action.Nothing()
    }
}