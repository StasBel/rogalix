package ru.spbau.mit

import org.junit.Test
import ru.spbau.mit.game.DefaultGame
import ru.spbau.mit.game.Game
import ru.spbau.mit.main.GameOverType
import ru.spbau.mit.main.Roguelike
import ru.spbau.mit.map.FromFileCreatingMapStrategy
import ru.spbau.mit.map.Map
import ru.spbau.mit.print.Printer
import ru.spbau.mit.strategy.Action
import ru.spbau.mit.strategy.Strategy
import ru.spbau.mit.world.*
import kotlin.test.assertEquals


/**
 * Unit tests for roguelike game.
 * @author belaevstanislav
 */
class RoguelikeTest {
    private companion object TestInfo {
        val LINE_MAP_PATH = "./src/test/resources/maps/line.map"
    }

    private class EmptyPrinter : Printer {
        override fun print(self: Creature, world: World, map: Map, game: Game) {
        }
    }

    private class UpStrategy : Strategy {
        override fun act(self: Creature, world: World, map: Map, game: Game): Action {
            return Action.Move(Move.UP)
        }
    }

    private class HoldStrategy : Strategy {
        override fun act(self: Creature, world: World, map: Map, game: Game): Action {
            return Action.Move(Move.HOLD)
        }
    }

    private class MemStrategy(var params: Params = Params()) : Strategy {
        override fun act(self: Creature, world: World, map: Map, game: Game): Action {
            params = self.params
            return (self.items.get(ItemStatus.STORED).firstOrNull()?.let { Action.ToggleItem(it) }
                    ?: Action.Move(Move.UP))
        }
    }

    /**
     * Simple test of two ai on simple line map. We should win because we stronger enough.
     */
    @Test
    fun testWin() {
        val roguelike = Roguelike(
                creatingMapStrategy = FromFileCreatingMapStrategy(LINE_MAP_PATH),
                game = DefaultGame(
                        maxVisibilityRange = 100, //everybody sees everybody
                        playerStrategy = UpStrategy()
                ),
                printer = EmptyPrinter()
        )

        // 100 rounds in enough to finish
        assertEquals(GameOverType.WIN, roguelike.run(maxRounds = 100))
    }

    /**
     * Another one simple test of two ai on simple line map. We should lose because we do nothing.
     */
    @Test
    fun testLose() {
        val roguelike = Roguelike(
                creatingMapStrategy = FromFileCreatingMapStrategy(LINE_MAP_PATH),
                game = DefaultGame(
                        maxVisibilityRange = 100, //everybody sees everybody
                        playerStrategy = HoldStrategy()
                ),
                printer = EmptyPrinter()
        )

        // 100 rounds in enough to finish
        assertEquals(GameOverType.LOSE, roguelike.run(maxRounds = 100))
    }

    /**
     * And another one simple tests of two ai on simple line map.
     * Nobody should win because creatures don't see each other.
     */
    @Test
    fun testNothing() {
        val roguelike = Roguelike(
                creatingMapStrategy = FromFileCreatingMapStrategy(LINE_MAP_PATH),
                game = DefaultGame(
                        maxVisibilityRange = 3, //nobody
                        playerStrategy = HoldStrategy()
                ),
                printer = EmptyPrinter()
        )

        // 100 rounds in enough to finish
        assertEquals(GameOverType.NOTHING, roguelike.run(maxRounds = 100))
    }

    /**
     * And another one simple tests of two ai on simple line map.
     * We pick up all 3 items on the way to enemy.
     */
    @Test
    fun testItems() {
        val strategy = MemStrategy()

        val roguelike = Roguelike(
                creatingMapStrategy = FromFileCreatingMapStrategy(LINE_MAP_PATH),
                game = DefaultGame(
                        maxVisibilityRange = 100, // everybody sees everybody
                        playerStrategy = strategy
                ),
                printer = EmptyPrinter()
        )

        // 100 rounds in enough to finish
        assertEquals(GameOverType.WIN, roguelike.run(maxRounds = 100))
        // sum all bonuses and items
        assertEquals(Params.DEFAULT
                + Race.HUMAN.bonus
                + CreatureType.PLAYER.bonus
                + ItemType.SWORD.params
                + ItemType.SWORD.params
                + ItemType.SHIELD.params
                + ItemType.MAGIC_HAT.params, strategy.params)
    }
}