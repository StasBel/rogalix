package ru.spbau.mit

import org.junit.Test
import ru.spbau.mit.basic.Creature
import ru.spbau.mit.gen.RandomArenaWorldGenerator
import kotlin.test.assertEquals

class GenTest {
    @Test
    fun testGen() {
        val gen = RandomArenaWorldGenerator(players = 1, enemies = 10, items = 3)
        val world = gen.start()
        assertEquals(
                1,
                world.zoo.creatures.count { it is Creature.Player },
                "Check players"
        )
        assertEquals(
                10,
                world.zoo.creatures.count { it is Creature.Mob },
                "Check enemies"
        )
        assertEquals(
                3,
                world.zoo.items.count(),
                "Check items"
        )
    }
}