package ru.spbau.mit.world

/**
 * A type of [Creature].
 * @author belaevstanislav
 */
enum class CreatureType(val bonus: Params) {
    PLAYER(Params(hp = +5, maxHp = +5, attack = +2)),
    MOB(Params(attack = +3, armor = +1));

    companion object {
        val DEFAULT = MOB
    }
}

/**
 * Race of [Creature].
 * @author belaevstanislav
 */
enum class Race(val bonus: Params) {
    HUMAN(Params(armor = +2)),
    ORK(Params(hpRegen = +1));

    companion object {
        val DEFAULT = ORK
    }
}

/**
 * Status of [Creature].
 * @author belaevstanislav
 */
enum class CreatureStatus {
    ALIVE,
    DEAD;

    companion object {
        val DEFAULT = ALIVE
    }
}

/**
 * Creature abstract class. You can create an instance of it only using Builder.
 * Creature is essential of living unit of the game field which have some params.
 * @author belaevstanislav
 * @param pos [Pos] of creature
 * @param creatureType [CreatureType] of creature
 * @param race [Race] of creature
 * @param params [Params] of creature
 * @param items [Items] of creature
 * @param status [CreatureStatus] of creature
 */
abstract class Creature private constructor(pos: Pos,
                                            val creatureType: CreatureType,
                                            val race: Race,
                                            val params: Params,
                                            val items: Items,
                                            var status: CreatureStatus) : Unit(pos) {
    /**
     * A class implementing Builder pattern for Creature class.
     * Has bunch of setter for [Creature] field, finishing with build() func to
     * complete create.
     */
    class Builder private constructor() {
        companion object {
            fun create() = Builder()
        }

        private var pos: Pos = Pos.NULL
        private var creatureType: CreatureType = CreatureType.DEFAULT
        private var race: Race = Race.DEFAULT
        private var params: Params = Params.DEFAULT
        private var items: Items = Items()
        private val status: CreatureStatus = CreatureStatus.DEFAULT

        fun setPos(pos: Pos): Builder = run { this.pos = pos; this }

        fun setCreatureType(creatureType: CreatureType): Builder = run { this.creatureType = creatureType; this }

        fun setRace(race: Race): Builder = run { this.race = race; this }

        fun setParams(params: Params): Builder = run { this.params = params; this }

        fun addItems(items: Set<Item>): Builder = run {
            items.forEach {
                it.status = ItemStatus.STORED
                this.items.addItem(it)
            }
            this
        }

        fun build(): Creature = object : Creature(
                pos = pos,
                creatureType = creatureType,
                race = race,
                params = params + creatureType.bonus + race.bonus,
                items = items,
                status = status
        ) {}
    }

    /**
     * Adds item to [this] [Creature] changing it status to STORED.
     * @param item an item to add
     */
    fun addItem(item: Item) {
        items.findItem(item).apply {
            if (this == null) {
                when (item.status) {
                    ItemStatus.DROPPED -> {
                        item.status = ItemStatus.STORED
                        this@Creature.items.addItem(item)
                    }
                    ItemStatus.USED -> run {}
                    ItemStatus.STORED -> run {}
                }
            } else {
                run {}
            }
        }
    }

    /**
     * Adds item to [this] [Creature] changing it status from USED to STORED and vice versa.
     * @param item an item to toggle
     */
    fun toggleItem(item: Item) {
        items.findItem(item).apply {
            if (this == null) {
                run {}
            } else {
                when (status) {
                    ItemStatus.DROPPED -> run {}
                    ItemStatus.USED -> {
                        this@Creature.params -= type.params
                        status = ItemStatus.STORED
                    }
                    ItemStatus.STORED -> {
                        this@Creature.params += type.params
                        status = ItemStatus.USED
                    }
                }
            }
        }
    }

    /**
     * Proceeding a periodically events.
     */
    fun periodically() {
        // hp regen
        params.apply { this += Params(hp = +this.hpRegen) }
    }
}