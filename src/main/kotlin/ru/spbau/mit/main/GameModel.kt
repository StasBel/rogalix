package ru.spbau.mit.main

import ru.spbau.mit.game.Game
import ru.spbau.mit.map.CellType
import ru.spbau.mit.map.Map
import ru.spbau.mit.print.Printer
import ru.spbau.mit.strategy.Action
import ru.spbau.mit.strategy.NothingStrategy
import ru.spbau.mit.strategy.Strategy
import ru.spbau.mit.world.*

/**
 * Model class implementing main game logic using basic classes.
 * @author belaevstanislav
 * @param map a instance of [Map]
 * @param world a instance of [World]
 * @param game a instance of [Game]
 * @param strategies using for assigning a particular strategy to a creature
 * @param printer a instance of [Printer]
 * @constructor just store some vals
 */
class GameModel(private val map: Map,
                private val world: World,
                private val game: Game,
                private val strategies: kotlin.collections.Map<Creature, Strategy>,
                private val printer: Printer) {
    /**
     * Filtering world entities from a [self] point of view. Just
     * measure dist and compare to a max visibility range.
     * @param self creature to observe
     */
    private fun filterVisible(self: Creature): World {
        return World(
                creatures = world.creatures.filter { self.pos.isSee(it.pos, game.maxVisibilityRange) }.toHashSet(),
                items = world.items.filter { self.pos.isSee(it.pos, game.maxVisibilityRange) }.toHashSet()
        )
    }

    /**
     * An act of moving a creature using a [Move] instance.
     * @param self creature to observe
     * @param move [Move] type
     * @param world a instance of [World]
     */
    private fun move(self: Creature, move: Move, world: World) {
        val newPos = self.pos + move.delta

        // attack?
        val enemy = world.creatures.filter {
            (it.pos == newPos)
                    .and(it.creatureType != self.creatureType)
        }.firstOrNull()
        if (enemy != null) {
            attack(self, enemy)
        }

        // pick up?
        val item = world.items.filter {
            (it.pos == newPos)
        }.firstOrNull()
        if (item != null) {
            pickUp(self, item)
        }

        // or just move?
        if (enemy == null && map.getCellType(newPos) == CellType.EMPTY) {
            self.pos += move.delta
        }
    }

    /**
     * An act of attacking an enemy creature.
     * @param self creature to observe
     * @param enemy creature to attack
     */
    private fun attack(self: Creature, enemy: Creature) {
        enemy.params -= Params(
                hp = self.params.attack - enemy.params.armor
        )
        if (enemy.params.hp == 0) {
            enemy.status = CreatureStatus.DEAD
        }
    }

    /**
     * An act of picking up an item.
     * @param self creature to observe
     * @param item an item to pick up
     */
    private fun pickUp(self: Creature, item: Item) {
        item.status = ItemStatus.STORED
        self.items.addItem(item)
        world.items.remove(item)
    }

    /**
     * Calculate some post-tick things like removing dead bodies and game result.
     * @return an instance of [GameOverType] depending ot the tick results
     */
    private fun afterTick(): GameOverType {
        // removing corpses
        world.creatures.removeAll {
            c ->
            c.status == CreatureStatus.DEAD
        }

        // finish states checking
        if (world.creatures.filter { c -> c.creatureType == CreatureType.PLAYER }.isEmpty()) {
            return GameOverType.LOSE
        } else if (world.creatures.filter { c -> c.creatureType == CreatureType.MOB }.isEmpty()) {
            return GameOverType.WIN
        } else {
            return GameOverType.NOTHING
        }
    }

    /**
     * Processing a one tick creatures acting.
     * @return tick result as an instance of [GameOverType]
     */
    fun tick(): GameOverType {
        for (creature in world.creatures.filter { it.status != CreatureStatus.DEAD }) {
            // preparations
            val seeingWorld = filterVisible(creature)

            // print
            if (creature.creatureType == CreatureType.PLAYER) {
                printer.print(creature, seeingWorld, map, game)
            }

            // act
            val strategy = strategies[creature] ?: NothingStrategy()
            val action = strategy.act(creature, seeingWorld, map, game)
            when (action) {
                is Action.ForceQuit -> return GameOverType.FORCE_QUIT
                is Action.Nothing -> run {}
                is Action.Move -> move(creature, action.move, seeingWorld)
                is Action.ToggleItem -> creature.toggleItem(action.item)
            }

            // tick periodically effects
            creature.periodically()
        }

        return afterTick()
    }
}