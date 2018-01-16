package ru.spbau.mit.game

import ru.spbau.mit.strategy.Strategy

/**
 * Game console input mode. Blocking mode force you to press enter after
 * every input string (thus, letter too). Non-blocking mode acting without
 * enter, waiting for you next char to press, but it does't work inside IDE's.
 * @author belaevstanislav
 */
enum class GameInputMode {
    BLOCKING,
    NON_BLOCKING
}

/**
 * Interface using for definition of a common game consts and random function.
 * @author belaevstanislav
 */
interface Game {
    /**
     * The name of that roguelike game.
     */
    val gameName: String

    /**
     * @see [GameInputMode]
     */
    val gameInputMode: GameInputMode

    /**
     * Max output screen size for x axis (from up to down).
     */
    val xSize: Int

    /**
     * Max output screen size for y axis (from left to right)
     */
    val ySize: Int

    /**
     * Maximum visibility range for observe other creatures and items on playing grid.
     */
    val maxVisibilityRange: Int

    /**
     * Player strategy implementation.
     */
    val playerStrategy: Strategy

    /**
     * The random functions using for different purposes.
     * @param mod module for random int
     */
    fun random(mod: Int): Int
}