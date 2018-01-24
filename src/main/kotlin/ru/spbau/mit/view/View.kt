package ru.spbau.mit.view

import mu.KotlinLogging
import ru.spbau.mit.basic.Creature
import ru.spbau.mit.main.World
import java.io.Closeable

private val logger = KotlinLogging.logger {}

/**
 * Interface for classes used for rendering current game state.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
interface View : Closeable {
    /**
     * Render current game state.
     *
     * @receiver An instance of [World], visibleWorld by [self].
     * @param self An instance of [Creature] - center unit to draw. Other world is just a
     * visible world from his point of view.
     */
    fun World.renderFrom(self: Creature.Player)
}

@Suppress("unused")
/**
 * Simple logging view example / placeholder.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class LoggerView : View {
    /**
     * Render current game state.
     *
     * @receiver An instance of [World], visibleWorld by [self].
     * @param self An instance of [Creature] - center unit to draw. Other world is just a
     * visible world from his point of view.
     */
    override fun World.renderFrom(self: Creature.Player) {
        logger.info { "Render next round! Player staying at ${self.pos}" }
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     */
    override fun close() {}
}