package ru.spbau.mit

import org.junit.Test
import ru.spbau.mit.basic.Pt
import ru.spbau.mit.env.Env
import ru.spbau.mit.env.TilesPrimitives
import ru.spbau.mit.env.bb
import ru.spbau.mit.env.get
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EnvTest {
    @Test
    fun testFill() {
        val tiles = TilesPrimitives.fill(Pt(10, 10), Env.Tile.VOID)
        assertEquals(tiles.bb.points().size, 10 * 10, "Check size")
        assertTrue(tiles.bb.points().all { tiles[it] == Env.Tile.VOID }, "Check all VOID")
    }

    @Test
    fun testRoom() {
        val room = TilesPrimitives.room(Pt(10, 10))
        val floors = room.bb.points().count { room[it] == Env.Tile.FLOOR }
        val walls = room.bb.points().count { room[it] == Env.Tile.WALL }
        assertEquals(64, floors, "Num of floors")
        assertEquals(100 - 64, walls, "Num of walls")
    }
}