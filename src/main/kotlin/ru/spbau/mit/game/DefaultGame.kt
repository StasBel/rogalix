package ru.spbau.mit.game

import ru.spbau.mit.strategy.PlayerStrategy
import ru.spbau.mit.strategy.Strategy
import java.util.*

/**
 * My default in-game consts.
 * @see [Game]
 * @author belaevstanislav
 */
class DefaultGame(
        /**
         * The name of that roguelike game.
         */
        override val gameName: String = "Roguelike v0.1",

        /**
         * @see [GameInputMode]
         */
        override val gameInputMode: GameInputMode = GameInputMode.BLOCKING,

        /**
         * Max output screen size for x axis (from up to down).
         */
        override val xSize: Int = 20,

        /**
         * Max output screen size for y axis (from left to right)
         */
        override val ySize: Int = 35,

        /**
         * Maximum visibility range for observe other creatures and items on playing grid.
         */
        override val maxVisibilityRange: Int = 10,

        /**
         * Player strategy implementation.
         */
        override val playerStrategy: Strategy = PlayerStrategy()) : Game {

    /**
     * Storing java [Random] class.
     */
    private val random: Random = Random()

    /**
     * The random functions using for different purposes.
     * @param mod module for random int
     */
    override fun random(mod: Int): Int = random.nextInt(mod)
}