package ru.spbau.mit

import org.junit.Test
import ru.spbau.mit.basic.Creature
import ru.spbau.mit.basic.Effect
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EffectTest {
    @Test
    fun testMerge() {
        val effect1 = Effect(hp = +200, attack = -2)
        val effect2 = Effect(hp = +300, armor = +5)
        assertEquals(
                Effect(hp = +500, attack = -2, armor = +5),
                effect1 + effect2,
                "Attrs deltas algebra"
        )
    }

    @Test
    fun testAdd() {
        val params = Creature.Params.DEFAULT
        val effect = Effect(hp = +100)
        assertEquals(
                Creature.Params.DEFAULT.hp,
                (params + effect).hp,
                "Add 100 hp and nothing changed"
        )
    }

    @Test
    fun testBoundAttr() {
        val params = Creature.Params.DEFAULT
        val effect1 = Effect(maxHp = +50)
        val effect2 = Effect(maxHp = -100)
        val res = params + effect1 + effect2
        assertEquals(
                Creature.Params.DEFAULT.hp - 50,
                res.hp,
                "Add 50 maxHp, than sub 100, got only initial minus 50 hp now"
        )
        assertEquals(
                Creature.Params.DEFAULT.hp - 50,
                res.maxHp,
                "Add 50 maxHp, than sub 100, got only initial minus 50 maxHp now"
        )

    }

    @Test
    fun testEquals() {
        // Bunch of straightforward stuff
        assertTrue { Creature.Params.DEFAULT == Creature.Params.DEFAULT }
        assertTrue {
            Effect(hp = +10) + Effect(hp = -5) == Effect(hp = +5)
        }
        assertFalse { Creature.Params.DEFAULT + Effect(maxHp = +5) == Creature.Params.DEFAULT }
        assertTrue { Creature.Params.DEFAULT + Effect(hp = +5) == Creature.Params.DEFAULT }
    }

    @Test
    fun testNatural() {
        val params = Creature.Params.DEFAULT
        val effect = Effect(armor = -10000)
        assertTrue((params + effect).armor == 0, "Can't have negative armor")
    }
}