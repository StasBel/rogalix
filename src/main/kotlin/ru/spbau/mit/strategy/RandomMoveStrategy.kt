package ru.spbau.mit.strategy

import ru.spbau.mit.game.Game
import ru.spbau.mit.map.Map
import ru.spbau.mit.world.Creature
import ru.spbau.mit.world.Move
import ru.spbau.mit.world.World

/**
 * A random move strategy.
 * @author belaevstanislav
 */
class RandomMoveStrategy : Strategy {
    /**
     * Doing an action of random [Move].
     * @param self an instance of [Creature] to act
     * @param world an instance of [World] to observe
     * @param map an instance of [Map] using to work with mark
     * @param game an instance of [Game] using to get some consts
     * @return an instance of [Action] as a result of doing strategy
     */
    override fun act(self: Creature, world: World, map: Map, game: Game): Action {
        return Action.Move(Move.values().let { it[game.random(it.size)] })
    }
}