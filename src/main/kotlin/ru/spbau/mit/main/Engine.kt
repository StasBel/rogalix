package ru.spbau.mit.main

import mu.KotlinLogging
import ru.spbau.mit.RogalixException
import ru.spbau.mit.basic.*
import ru.spbau.mit.basic.Unit
import ru.spbau.mit.env.Env
import ru.spbau.mit.env.MemoizeEnv
import ru.spbau.mit.gen.LevelGenerator
import ru.spbau.mit.gen.RandomArenaWorldGenerator
import ru.spbau.mit.strategy.Action
import kotlin.math.max
import kotlin.reflect.KProperty

private val logger = KotlinLogging.logger {}

/**
 * Engine abstraction.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
interface Engine {
    /**
     * Actors order.
     */
    val actorsOrder: List<Creature>

    /**
     * Getting instance of world from @receiver view perspective.
     */
    fun Creature.takeALook(): World

    /**
     * Perform action from @receiver view perspective.
     */
    fun Creature.perform(action: Action)

    /**
     * Eval current outcome.
     */
    fun evalOutcome(): Model.Outcome
}

/**
 * 2D Grid engine. Here's all magic happens.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class GridEngine(
        private val generator: LevelGenerator<World> = RandomArenaWorldGenerator()
) : Engine {
    private var env: Env = Env.NULL  // Has no shadow in it
    private val creatures: MutableSet<Creature> = LinkedHashSet() /* For remain act order */
    private val droppedItems: MutableSet<Item> = mutableSetOf()
    private var Unit.pos: Pt by UnitMapDelegate { Pt.NULL }
    private var Creature.status: Creature.Status by UnitMapDelegate { Creature.Status.ALIVE }
    private var Creature.equip: Creature.Equip by UnitMapDelegate { Creature.Equip.EMPTY }
    private var Creature.params: Creature.Params by UnitMapDelegate { Creature.Params.DEFAULT }

    init {
        adjustNewWorld(generator.start())
    }

    /**
     * Actors order.
     */
    override val actorsOrder: List<Creature> = creatures.toList()

    /**
     * Getting instance of world from @receiver view perspective.
     */
    override fun Creature.takeALook(): World {
        val engine = this@GridEngine
        val self = this
        return object : World {
            override val env = MemoizeEnv(object : Env {
                override fun get(pt: Pt): Env.Tile {
                    val realPt = pt + with(engine) { self.pos }
                    return with(engine) {
                        if ((pos distTo realPt <= fov)
                                or (env[realPt] == Env.Tile.VOID)) env[realPt]
                        else Env.Tile.SHADOW
                    }
                }
            })

            override val zoo = with(engine) {
                SimpleZoo(
                        (creatures + droppedItems)
                                .filter { pos distTo it.pos <= fov }
                                .toSet()
                )
            }

            override val Unit.pos: Pt
                get() = with(engine) { pos - self.pos }

            override val Creature.params: Creature.Params
                get() = with(engine) { params }

            override val Creature.status: Creature.Status
                get() = with(engine) { status }

            override val Creature.equip: Creature.Equip
                get() = if (this === self) with(engine) { equip } // Can observe equip.
                else Creature.Equip(with(engine) { equip.used }, setOf())  // No `Vanga` stuff here!
        }
    }

    /**
     * Perform action from @receiver view perspective.
     */
    override fun Creature.perform(action: Action) = when (action) {
        Action.ForceQuit -> throw RogalixException.HandleQuitInEngine
        is Action.Move -> move(action.move)
        is Action.PutOnItem -> putOnItem(action.item)
        is Action.TakeOffItem -> takeOffItem(action.item)
        is Action.DropItem -> dropItem(action.item)
    }

    /**
     * Eval current outcome.
     */
    override fun evalOutcome(): Model.Outcome {
        creatures.removeAll { it.status == Creature.Status.DEAD }  // Removing corpses.
        return when {
            !creatures.any { it is Creature.Mob } -> Model.Outcome.WIN
            !creatures.any { it is Creature.Player } -> Model.Outcome.LOSE
            else -> Model.Outcome.NORMAL
        }
    }

    private fun Creature.move(move: Pt.Move) {
        val newPos = pos + move.dir

        val enemy: Creature? =
                creatures.firstOrNull { (it.pos == newPos) and (it::class != this::class) }
        if (enemy != null) attack(enemy)

        val item: Item? = droppedItems.firstOrNull { it.pos == newPos }
        if (item != null) pickUp(item)

        if ((enemy == null) and (item == null)) {
            when (env[newPos]) {
                Env.Tile.FLOOR -> pos = newPos
                Env.Tile.PORTAL -> adjustNewWorld(generator.nextAfter(this, newPos))
                Env.Tile.SHADOW -> throw RogalixException.ShadowMapGen
                else -> {
                    logger.warn { "Trying to move to impassable tile." }
                }
            }
        }
    }

    private fun Creature.putOnItem(item: Item) {
        logger.info { "Putting on the ${item.name.desc}." }
        if (item in equip.stored) {
            when (item) {
                is Item.Gear -> {
                    equip += item
                    params += item.effect
                }
                is Item.OneOff -> {
                    equip -= item
                    params += item.effect
                }
            }
        }
    }

    private fun Creature.takeOffItem(item: Item.Gear) {
        logger.info { "Taking off the ${item.name.desc}." }
        if (item in equip.used) {
            equip -= item
            params -= item.effect
        }
    }

    private fun Creature.dropItem(item: Item) {
        logger.info { "Dropping the ${item.name.desc}." }
        if (item in equip.stored) {
            equip -= item
            droppedItems += item
            item.pos = pos
        }
    }

    private fun Creature.attack(enemy: Creature) {
        val damage = max(params.attack - enemy.params.armor, 0)
        logger.info { "Attack enemy, dealing $damage damage!" }
        enemy.params += Effect(hp = -damage)
        if (enemy.params.hp == 0) enemy.status = Creature.Status.DEAD
    }

    private fun Creature.pickUp(item: Item) {
        logger.info { "Picking up the ${item.name.desc}." }
        equip += item
        droppedItems -= item
        item.pos = Pt.NULL
    }

    private fun adjustNewWorld(world: World) {
        logger.info { "Adjusting newly generated world." }
        env = with(world) { env }
        droppedItems.clear()
        droppedItems.addAll(world.zoo.items)
        creatures.removeAll { it !is Creature.Player }
        creatures.addAll(world.zoo.creatures)
        for (unit in (world.zoo.creatures + world.zoo.items)) {
            unit.pos = with(world) { unit.pos }
        }
        for (creature in world.zoo.creatures) {
            creature.params = with(world) { creature.params }
            creature.status = with(world) { creature.status }
            creature.equip = with(world) { creature.equip }
        }
    }

    private class UnitMapDelegate<T>(private val default: () -> T) {
        private var unitToT: MutableMap<Unit, T> = mutableMapOf()

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
                unitToT.getOrPut(thisRef as Unit, default)

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) =
                run { unitToT[thisRef as Unit] = value }
    }
}