package ru.spbau.mit.main

import mu.KotlinLogging
import ru.spbau.mit.view.LoggerView
import ru.spbau.mit.view.SwingTilesView
import ru.spbau.mit.view.View

private val logger = KotlinLogging.logger {}

/**
 * Game abstraction.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
interface Game {
    /**
     * Running a new instance of game.
     */
    fun run(): Outcome

    /**
     * Game outcome.
     */
    enum class Outcome(val message: String) {
        WIN("Congratulations, you won!"),
        LOSE("Sorry, but you lost. Try better next time."),
        QUIT("Exit the game."),
        TICKSLIMIT("Reached maximum ticks number.")
    }
}

/**
 * Rogalix game.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class RogalixGame(
        private val view: View = LoggerView(),
        private val engine: Engine = GridEngine(),
        private val policy: Policy = HoldPolicy(),
        private val maxTicks: Int? = null
) : Game {
    /**
     * Running a new instance of game.
     */
    override fun run(): Game.Outcome {
        val model = RogalixModel(view, engine, policy)

        tailrec fun rollsTicks(tickNum: Int = 0): Game.Outcome {
            return if (tickNum == maxTicks) Game.Outcome.TICKSLIMIT
            else when (model.tick()) {
                Model.Outcome.WIN -> Game.Outcome.WIN
                Model.Outcome.LOSE -> Game.Outcome.LOSE
                Model.Outcome.QUIT -> Game.Outcome.QUIT
                Model.Outcome.NORMAL -> rollsTicks(tickNum + 1)
            }
        }

        val outcome = rollsTicks()
        logger.info("Game over. ${outcome.message}")
        return outcome
    }
}

/**
 * Running instance of Rogalix with keyboard player strategy.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
fun makeKeyboardSession(maxTicks: Int? = null): Game {
    val view = SwingTilesView()
    val engine = GridEngine()
    val policy = MemoizePolicy(KeyboardPlayerPolicy(view::getCh))
    return RogalixGame(view, engine, policy, maxTicks)
}