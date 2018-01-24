package ru.spbau.mit.gen

import ru.spbau.mit.basic.Box
import ru.spbau.mit.basic.Creature
import ru.spbau.mit.basic.Pt
import ru.spbau.mit.env.Env
import ru.spbau.mit.env.TilesEnv
import ru.spbau.mit.env.TilesPrimitives
import java.util.*

/**
 * Corridor advanced level generation.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class CorridorGenerator(
        private val dim: Pt = Pt(75, 75),
        private val boxesNum: Int = 10,
        private val halfBoxSizes: Pt = Pt(4, 8)
) : LevelGenerator<Pair<Env, Box>> {
    /**
     * Initial level.
     */
    override fun start(): Pair<Env, Box> = genLevel()

    /**
     * Next after [self] finished at [portalAt].
     */
    override fun nextAfter(self: Creature, portalAt: Pt): Pair<Env, Box> = genLevel()

    private fun genLevel(): Pair<Env, Box> {
        val tiles = TilesPrimitives.fill(dim, Env.Tile.WALL)
        val random = Random()
        val pickHalfBoxSize: () -> Int =
                { halfBoxSizes.x + random.nextInt(halfBoxSizes.y - halfBoxSizes.x + 1) }
        val centers = arrayListOf<Pt>()
        for (i in 1..boxesNum) {
            val halfBoxSize = pickHalfBoxSize()
            val center =
                    Box(Pt(halfBoxSize, halfBoxSize), dim - halfBoxSize).random()
            centers.add(center)
            TilesPrimitives.fill(tiles, center expand halfBoxSize, Env.Tile.FLOOR)
        }
        for (c1 in centers) {
            for (c2 in centers) {
                TilesPrimitives.placeCorridor(tiles, c1, c2)
            }
        }
        TilesPrimitives.filterOutWalls(tiles)
        return Pair(TilesEnv(tiles), Box(Pt.NIL, dim - 1))
    }
}