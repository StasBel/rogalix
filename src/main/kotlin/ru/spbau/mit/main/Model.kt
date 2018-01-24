package ru.spbau.mit.main

import mu.KotlinLogging
import ru.spbau.mit.basic.Creature
import ru.spbau.mit.strategy.Action
import ru.spbau.mit.view.View

private val logger = KotlinLogging.logger {}

/**
 * Game model.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
interface Model {
    /**
     * Simulates one tick one the game (each actor makes one action).
     */
    fun tick(): Outcome

    /**
     * Tick outcome.
     */
    enum class Outcome(val message: String) {
        WIN("The winner tick. All goals are fulfilled!"),
        LOSE("The loser tick. You probably just died or run out of time."),
        NORMAL("Just regular tick. Nothing interesting happened."),
        QUIT("You choose to force quit the game.")
    }
}

/**
 * Rogalix model implementation.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class RogalixModel(
        private val view: View,
        private val engine: Engine,
        private val policy: Policy
) : Model {
    /**
     * Simulates one tick one the game (each actor makes one action).
     */
    override fun tick(): Model.Outcome = with(engine) {
        fun proceed(): Model.Outcome {
            for (actor in actorsOrder) {
                val visibleWorld = actor.takeALook()
                if (actor is Creature.Player) with(view) { visibleWorld.renderFrom(actor) }
                val action = with(policy[actor]) { visibleWorld.chooseAction(actor) }
                if (action == Action.ForceQuit) return Model.Outcome.QUIT else actor.perform(action)
            }
            return evalOutcome()
        }

        val outcome = proceed()
        if (outcome != Model.Outcome.NORMAL) {
            logger.info { "Tick ended. ${outcome.message}" }
            view.close()
        }
        return outcome
    }
}