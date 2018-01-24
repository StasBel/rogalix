package ru.spbau.mit.gen

import ru.spbau.mit.basic.Box
import ru.spbau.mit.basic.Creature
import ru.spbau.mit.basic.Pt
import ru.spbau.mit.env.Env
import ru.spbau.mit.env.TilesEnv
import ru.spbau.mit.env.TilesPrimitives

/**
 * Basic one room level generation.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class OneRoomGenerator(private val dim: Pt = Pt(20, 20)) : LevelGenerator<Pair<Env, Box>> {
    /**
     * Initial level.
     */
    override fun start(): Pair<Env, Box> = genRoom()

    /**
     * Next after [self] finished at [portalAt].
     */
    override fun nextAfter(self: Creature, portalAt: Pt): Pair<Env, Box> = genRoom()

    private fun genRoom(): Pair<Env, Box> {
        val tiles = TilesPrimitives.room(dim)
        TilesPrimitives.filterOutWalls(tiles)
        return Pair(TilesEnv(tiles), Box(Pt.NIL, dim - 1))
    }
}
