package ru.spbau.mit.env

import ru.spbau.mit.basic.Box
import ru.spbau.mit.basic.Pt
import kotlin.math.max
import kotlin.math.min


typealias Tiles = Array<Array<Env.Tile>>

/**
 * Funcs to deal with tiles.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
object TilesPrimitives {
    /**
     * Fill tiles with given dim and value.
     */
    fun fill(dim: Pt, value: Env.Tile = Env.Tile.VOID): Tiles =
            Array(dim.x, { Array(dim.y, { value }) })

    /**
     * Fill tiles within the box with value.
     */
    fun fill(tiles: Tiles, bb: Box, value: Env.Tile = Env.Tile.VOID) {
        for (pt in bb.points()) {
            tiles[pt] = value
        }
    }

    /**
     * Making a tile room.
     */
    fun room(dim: Pt): Tiles {
        val tiles = fill(dim, Env.Tile.FLOOR)
        for (x in 0 until dim.x) {
            tiles[x][0] = Env.Tile.WALL
            tiles[x][dim.y - 1] = Env.Tile.WALL
        }
        for (y in 0 until dim.y) {
            tiles[0][y] = Env.Tile.WALL
            tiles[dim.x - 1][y] = Env.Tile.WALL
        }
        return tiles
    }

    /**
     * Place floor corridor between pts.
     */
    fun placeCorridor(tiles: Tiles, pt1: Pt, pt2: Pt) {
        for (x in min(pt1.x, pt2.x)..max(pt1.x, pt2.x)) {
            tiles[x][min(pt1.y, pt2.y)] = Env.Tile.FLOOR
        }
        for (y in min(pt1.y, pt2.y)..max(pt1.y, pt2.y)) {
            tiles[min(pt1.x, pt2.x)][y] = Env.Tile.FLOOR
        }
    }

    /**
     * Filter out unnecessary walls.
     */
    fun filterOutWalls(tiles: Tiles) {
        val validTile: (Env.Tile) -> Boolean = { (it == Env.Tile.WALL) or (it == Env.Tile.VOID) }
        for (x in 0 until tiles.size) {
            for (y in 0 until tiles[x].size) {
                var wallsAndVoids = 0
                var needed = 0
                if (x > 0) {
                    needed += 1
                    if (validTile(tiles[x - 1][y])) wallsAndVoids += 1
                }
                if (x < tiles.size - 1) {
                    needed += 1
                    if (validTile(tiles[x + 1][y])) wallsAndVoids += 1
                }
                if (y > 0) {
                    needed += 1
                    if (validTile(tiles[x][y - 1])) wallsAndVoids += 1
                }
                if (y < tiles[x].size - 1) {
                    needed += 1
                    if (validTile(tiles[x][y + 1])) wallsAndVoids += 1
                }
                if (wallsAndVoids == needed) tiles[x][y] = Env.Tile.VOID
            }
        }
    }

    /**
     * Form a tiles from env and bounding box.
     */
    fun from(env: Env, bb: Box): Tiles {
        val tiles = fill(bb.lowerRight - bb.upperLeft + 1)
        for (x in 0 until tiles.size) {
            for (y in 0 until tiles[x].size) {
                tiles[x][y] = env[Pt(x, y) + bb.upperLeft]
            }
        }
        return tiles
    }
}

/**
 * Some extensions as well.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */

val Tiles.dim: Pt
    get() = Pt(size, map { it.size }.max() ?: 0)

val Tiles.bb: Box
    get() = Box(Pt.NIL, dim - 1)

operator fun Tiles.get(pt: Pt) = get(pt.x)[pt.y]

operator fun Tiles.set(pt: Pt, value: Env.Tile) = run { this[pt.x][pt.y] = value }

operator fun Tiles.contains(pt: Pt) = pt in bb