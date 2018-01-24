package ru.spbau.mit.gen

import ru.spbau.mit.basic.*
import ru.spbau.mit.basic.Unit
import ru.spbau.mit.env.*
import ru.spbau.mit.env.Env
import ru.spbau.mit.main.SimpleZoo
import ru.spbau.mit.main.World
import ru.spbau.mit.main.Zoo
import ru.spbau.mit.random

/**
 * Full world generation, randomly placing portals, players, enemies and items.
 * Depends on env generation passed as [envGenerator].
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class RandomArenaWorldGenerator(
        private val envGenerator: LevelGenerator<Pair<Env, Box>> = CorridorGenerator(),
        private val portals: Int = 1,
        private val players: Int = 1,
        private val enemies: Int = 15,
        private val items: Int = 9
) : LevelGenerator<World> {
    /**
     * Initial level.
     */
    override fun start(): World = genArena(envGenerator.start())

    /**
     * Next after [self] finished at [portalAt].
     */
    override fun nextAfter(self: Creature, portalAt: Pt): World =
            genArena(envGenerator.nextAfter(self, portalAt), self)

    private fun genArena(nextPair: Pair<Env, Box>, self: Creature? = null): World {
        /** Creating enviroment */
        val (env, bb) = nextPair
        val tiles = TilesPrimitives.from(env, bb)
        val floors =
                tiles.bb.points().filter { tiles[it] == Env.Tile.FLOOR }.toMutableSet()

        fun randomFloorPt(): Pt =
                floors.random().run { floors.remove(this); this }
        for (i in 1..portals) tiles[randomFloorPt()] = Env.Tile.PORTAL  // Portals

        /** Populating enviroment */
        val units: MutableSet<Unit> = mutableSetOf()
        val pos: MutableMap<Unit, Pt> = mutableMapOf()
        val status: MutableMap<Creature, Creature.Status> = mutableMapOf()
        val equip: MutableMap<Creature, Creature.Equip> = mutableMapOf()
        val params: MutableMap<Creature, Creature.Params> = mutableMapOf()

        fun calcInitParams(creature: Creature): Creature.Params {
            val initParams = creature.initParams + equip[creature]!!.usedEffect
            return initParams.copy(hp = initParams.maxHp)
        }

        if (self != null) {
            pos[self] = randomFloorPt()
        }
        for (i in 1..(if (self != null) players - 1 else players)) {
            val player = Creature.from(Creature.Name.DEFAULT_PLAYER)
            units.add(player)
            pos[player] = randomFloorPt()
            status[player] = Creature.Status.ALIVE
            equip[player] = Creature.Equip(used = setOf(Item.from(Item.Name.SWORD) as Item.Gear))
            params[player] = calcInitParams(player)
        }
        for (i in 1..enemies) {
            val enemy = Creature.from(Creature.Name.DEFAULT_MOB)
            units.add(enemy)
            pos[enemy] = randomFloorPt()
            status[enemy] = Creature.Status.ALIVE
            equip[enemy] = Creature.Equip.EMPTY
            params[enemy] = calcInitParams(enemy)
        }
        for (i in 1..items) {
            val item = Item.random()
            units.add(item)
            pos[item] = randomFloorPt()
        }

        /** Creating world */
        return object : World {
            override val env: Env = TilesEnv(tiles)

            override val zoo: Zoo = SimpleZoo(units)

            override val Unit.pos: Pt
                get() = pos[this]!!

            override val Creature.status: Creature.Status
                get() = status[this]!!

            override val Creature.equip: Creature.Equip
                get() = equip[this]!!

            override val Creature.params: Creature.Params
                get() = params[this]!!
        }
    }
}
