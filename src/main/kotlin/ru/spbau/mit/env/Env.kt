package ru.spbau.mit.env

import ru.spbau.mit.basic.Pt

/**
 * Basic class for observing environment.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
interface Env {
    companion object {
        val NULL = object : Env {
            override fun get(pt: Pt): Tile = Tile.VOID
        }
    }

    /**
     * Gets tile at given point.
     */
    operator fun get(pt: Pt): Tile

    /**
     * Tiles preset.
     */
    enum class Tile {
        FLOOR,
        WALL,
        PORTAL,
        SHADOW,
        VOID
    }
}

/**
 * Env from tiles 2d array.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class TilesEnv(private val tiles: Tiles) : Env {
    /**
     * Gets tile at given point.
     */
    override fun get(pt: Pt): Env.Tile = if (pt in tiles) tiles[pt] else Env.Tile.VOID
}

/**
 * Lazy evaluated memoization wrapper for observed instance of [Env].
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class MemoizeEnv(private val observedEnv: Env) : Env {
    private val calculated: MutableMap<Pt, Env.Tile> = mutableMapOf()

    /**
     * Gets tile at given point.
     */
    override fun get(pt: Pt): Env.Tile = calculated.getOrPut(pt, { observedEnv[pt] })
}
