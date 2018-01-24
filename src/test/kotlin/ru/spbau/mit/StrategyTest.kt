package ru.spbau.mit

import org.junit.Test
import ru.spbau.mit.basic.Creature
import ru.spbau.mit.gen.OneRoomGenerator
import ru.spbau.mit.gen.RandomArenaWorldGenerator
import ru.spbau.mit.main.*
import ru.spbau.mit.strategy.MobStrategy
import ru.spbau.mit.strategy.Strategy
import ru.spbau.mit.view.LoggerView
import kotlin.test.assertEquals

class StrategyTest {
    @Test
    fun testStrategy() {
        val view = LoggerView()
        val engine = GridEngine(
                generator = RandomArenaWorldGenerator(
                        envGenerator = OneRoomGenerator(),
                        enemies = 2
                )
        )
        val policy = MemoizePolicy(object : Policy {
            override fun get(creature: Creature): Strategy = when (creature) {
                is Creature.Player -> MobStrategy()
                is Creature.Mob -> Strategy.RandomMove
            }
        })
        val game = RogalixGame(view, engine, policy)
        assertEquals(game.run(), Game.Outcome.WIN, "Smarter strategy wins")
    }
}