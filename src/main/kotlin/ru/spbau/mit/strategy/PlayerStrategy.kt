package ru.spbau.mit.strategy

import ru.spbau.mit.console.Console.getCh
import ru.spbau.mit.game.Game
import ru.spbau.mit.game.GameInputMode
import ru.spbau.mit.map.Map
import ru.spbau.mit.world.Creature
import ru.spbau.mit.world.Move
import ru.spbau.mit.world.World

/**
 * An implementation of player strategy which reading keyboard input
 * and convert it to instances of [Action] class.
 * @author belaevstanislav
 */
class PlayerStrategy : Strategy {
    /**
     * Some useful reader params.
     */
    private object ReaderParams {
        val QUIT_CHAR = setOf(
                'q',
                'Q',
                27.toChar() // ESC
        )
    }

    /**
     * Reading keyboard input while we cant parse an Action from it.
     * @param self an instance of [Creature] to act
     * @param world an instance of [World] to observe
     * @param map an instance of [Map] using to work with mark
     * @param game an instance of [Game] using to get some consts
     * @return an instance of [Action] as a result of doing strategy
     */
    override fun act(self: Creature, world: World, map: Map, game: Game): Action {
        fun nextChar(): Char =
                if (game.gameInputMode == GameInputMode.BLOCKING) {
                    readLine()!![0]
                } else {
                    getCh()
                }

        fun readAction(): Action =
                when (nextChar()) {
                    in ReaderParams.QUIT_CHAR -> Action.ForceQuit()
                    'n' -> Action.Nothing()
                    'w' -> Action.Move(Move.UP)
                    'a' -> Action.Move(Move.LEFT)
                    's' -> Action.Move(Move.DOWN)
                    'd' -> Action.Move(Move.RIGHT)
                    'h' -> Action.Move(Move.HOLD)
                    in setOf('i', 't') -> {
                        val num = nextChar()
                        when (num) {
                            in '0'..'9' -> Action.ToggleItem(self.items.getItem(num - '0'))
                            else -> readAction()
                        }
                    }
                    else -> readAction()
                }

        return readAction()
    }
}