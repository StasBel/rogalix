package ru.spbau.mit.main

import ru.spbau.mit.game.Game
import ru.spbau.mit.map.CreatingMapStrategy
import ru.spbau.mit.print.Printer
import ru.spbau.mit.strategy.MobStrategy
import ru.spbau.mit.world.*

/**
 * Main Roguelike class using for initializing basic classes and
 * run the game.
 * @author belaevstanislav
 * @param creatingMapStrategy a instance of [CreatingMapStrategy]
 * @param game a instance of [Game]
 * @param printer a instance of [Printer]
 */
class Roguelike(creatingMapStrategy: CreatingMapStrategy, game: Game, printer: Printer) {
    private val gameModel: GameModel

    init {
        // creating map and proto world
        val (map, protoWorld) = creatingMapStrategy.create()

        // creating world
        val world = World(
                creatures = protoWorld.creatures
                        .map {
                            c ->
                            val creature = Creature.Builder.create()
                                    .setPos(c.pos)
                                    .setCreatureType(c.creatureType)
                                    .setRace(c.race)
                                    .build()
                            if (c.creatureType == CreatureType.PLAYER) {
                                val sword = Item.Factory.create(ItemType.SWORD)
                                creature.addItem(sword)
                                creature.toggleItem(sword)
                            }
                            creature
                        }.toHashSet(),
                items = protoWorld.items
                        .map { i -> Item.Factory.create(i.type, i.pos) }
                        .toHashSet()
        )

        // assigning strategies to creatures
        val strategies = world.creatures.map {
            if (it.creatureType == CreatureType.PLAYER) {
                Pair(it, game.playerStrategy)
            } else {
                Pair(it, MobStrategy())
            }
        }.toMap()

        // game logic
        gameModel = GameModel(map, world, game, strategies, printer)
    }

    /**
     * Running game for a [maxRounds] ticks (or inf ticks if we passed -1 to func).
     * @param maxRounds a number of rounds to simulate
     * @return invocation result as an instance of [GameOverType]
     */
    fun run(maxRounds: Int = -1): GameOverType {
        var curRound = 0
        var lastResult = GameOverType.NOTHING
        while (curRound != maxRounds) {
            lastResult = gameModel.tick()
            if (lastResult != GameOverType.NOTHING) break
            curRound++
        }

        println("GAME OVER! YOU ${lastResult.name}!")
        return lastResult
    }
}