package ru.spbau.mit

import org.junit.Test
import ru.spbau.mit.basic.Box
import ru.spbau.mit.basic.Pt
import kotlin.test.assertEquals

class GeomTest {
    @Test
    fun testAlgebra() {
        assertEquals(
                Pt(0, 0),
                Pt(-1, 5) + Pt(1, -5),
                "Pts additive sum"
        )

        assertEquals(
                Pt(2, 3),
                Pt(-10, 80) - Pt(-12, 77),
                "Pts additive diff"
        )
    }

    @Test
    fun testMove() {
        assertEquals(
                Pt(0, 1),
                Pt.NIL move Pt.Move.RIGHT move Pt.Move.DOWN move Pt.Move.LEFT,
                "Seq of moves"
        )

        val pt = Pt.NIL move Pt.Move.RIGHT move Pt.Move.RIGHT move Pt.Move.DOWN
        assertEquals(
                Pair(2, 1),
                Pair(pt.x, pt.y),
                "Seq of moves"
        )
    }

    @Test
    fun testExpand() {
        assertEquals(
                Box(Pt(-4, -4), Pt(4, 4)),
                Pt.NIL expand 5,
                "Expanding zero point to the 5"
        )
    }
}