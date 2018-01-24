package ru.spbau.mit.main

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    val game: Game = makeKeyboardSession(maxTicks = 500)
    logger.info { "Start to playing the game." }
    game.run()
}