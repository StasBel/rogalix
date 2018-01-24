package ru.spbau.mit.basic

import ru.spbau.mit.basic.Creature.Class
import ru.spbau.mit.basic.Creature.Race
import ru.spbau.mit.basic.Item.Name
import ru.spbau.mit.random
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Base class for all in game units.
 * Unit is, essentially, some thing, that has position, meaning and use.
 */
sealed class Unit

/**
 * The most basic creature class for all living creatures, who have their own subjectivity and
 * can act on their own.
 *
 * @constructor Takes unchanged constant within living time characteristics.
 * @param race An instance of [Race].
 * @param clazz An instance of [Class].
 * @param fov Field of view, which shows how far that creature can see.
 */
sealed class Creature(
        val race: Race,
        val clazz: Class,
        val fov: Int
) : Unit() {
    /**
     * Special subclass for all players creatures.
     *
     * @constructor Here, we fix some of the values, like [Race] and fov.
     * @param handicap Additional bonus on start to help adjust your gaming level.
     * @param clazz [Class] to choose to play for.
     */
    class Player(val handicap: Effect, clazz: Class) : Creature(Race.HUMAN, clazz, 10) {
        /**
         * Calculate initial params, summing default one with all kind of bonuses.
         */
        override val initParams: Params = super.initParams + handicap
    }

    /**
     * Subclass for all hostile to player living creatures.
     */
    class Mob(race: Race, clazz: Class) : Creature(race, clazz, 5)

    companion object {
        /**
         * Creating creature from preset of names.
         */
        fun from(name: Name) = when (name) {
            Name.DEFAULT_PLAYER -> Player(Effect(attack = +7), Class.random())
            Name.DEFAULT_MOB -> Mob(Race.random(), Class.random())
        }
    }

    /**
     * Calculate initial params, summing default one with all kind of bonuses.
     */
    open val initParams = Params.DEFAULT + race.effect + clazz.effect

    /**
     * Some name from preset of names for convenient creatures creation.
     */
    enum class Name {
        DEFAULT_PLAYER, DEFAULT_MOB
    }

    /**
     * Racial attr of creature.
     * Assuming, there if only those races to deal with.
     */
    enum class Race(val effect: Effect) {
        HUMAN(Effect(maxHp = +50)),
        ORK(Effect(attack = +3)),
        ELF(Effect(armor = +2));

        companion object {
            fun random() = values().random()
        }
    }

    /**
     * Class attr of creature.
     * Assuming, there if only those classes to deal with.
     */
    enum class Class(val effect: Effect) {
        SWORDSMAN(Effect(attack = +5, armor = +2)),
        PALADIN(Effect(attack = +2, armor = +4)),
        MAGE(Effect(maxHp = +50, armor = +3));

        companion object {
            fun random() = values().random()
        }
    }

    /**
     * Current creature status.
     */
    enum class Status {
        ALIVE, DEAD
    }

    /**
     * Player equipment. Can be a lot complex than just bag of items.
     * For example: distinct slots for head, boot and weapon.
     */
    data class Equip(val used: Set<Item.Gear> = setOf(), val stored: Set<Item> = setOf()) {
        companion object {
            val EMPTY = Equip()
        }

        /**
         * Summed up all effects from used items.
         */
        val usedEffect: Effect = used.fold(Effect.NIL, { e, i -> e + i.effect })

        /**
         * Add new item, giving back updated version of equipment.
         * If we have this item in stored, than now we have it at used. If we do not have this item,
         * that we have it at stored.
         */
        operator fun plus(item: Item): Equip = when (item) {
            is Item.Gear -> when (item) {
                in used -> copy()
                in stored -> Equip(used + item, stored - item)
                else -> Equip(used, stored + item)
            }
            else -> when (item) {
                in stored -> copy()
                else -> Equip(used, stored + item)
            }
        }

        /**
         * Delete item, giving back updated version of equipment.
         * If we have this item at used, than now we have it at stored. If we have this item at
         * stored, than now we don't have it anymore.
         */
        operator fun minus(item: Item): Equip = when (item) {
            is Item.Gear -> when (item) {
                in used -> Equip(used - item, stored + item)
                in stored -> Equip(used, stored - item)
                else -> copy()
            }
            else -> when (item) {
                in stored -> Equip(used, stored - item)
                else -> copy()
            }
        }
    }

    /**
     * Class for denoting all creature parameters.
     * Can be updated over time using an instance of [Effect].
     */
    data class Params(
            val hp: Int = 100,
            val maxHp: Int = hp,
            val attack: Int = 10,
            val armor: Int = 7
    ) {
        companion object {
            val DEFAULT = Params()
        }

        operator fun plus(effect: Effect) =
                Params(
                        hp = min(max(hp + effect.hp, 0), maxHp + effect.maxHp),
                        maxHp = max(maxHp + effect.maxHp, 0),
                        attack = max(attack + effect.attack, 0),
                        armor = max(armor + effect.armor, 0)
                )

        operator fun minus(effect: Effect) = this + (-effect)
    }
}

/**
 * The most basic item class, which using for all sorts of in-game creature's items.
 * Item, basically, gives you a boost of some sort. For example: just increase your params or
 * heals your.
 *
 * @constructor Takes unchanged constant within living time characteristics.
 * @param name An instance of [Name], which helps distinct one concrete item features from another.
 */
sealed class Item(val name: Name) : Unit() {
    /**
     * Class for denoting all items that can be toggled unlimited number of times.
     */
    class Gear(val effect: Effect, name: Name) : Item(name)

    /**
     * Class for denoting all items, that has one time usage effect.
     */
    class OneOff(val effect: Effect, name: Name) : Item(name)

    companion object {
        /**
         * Creating item from name.
         */
        fun from(name: Name) = when (name) {
            Name.SWORD -> Gear(Effect(attack = +3), name)
            Name.SHIELD -> Gear(Effect(armor = +2), name)
            Name.POTION -> OneOff(Effect(hp = +50), name)
        }

        /**
         * Creating random item (uniform distribution over all types of items).
         */
        fun random() = from(Name.values()[Random().nextInt(Name.values().size)])
    }

    /**
     * Class for providing additional information concrete item. Includes information,
     * description and other stuff.
     *
     * @constructor Takes description string and char representation.
     */
    enum class Name(val desc: String, val repr: Char) {
        SWORD("Sword of the 7 kingdoms", 's'),
        SHIELD("Strong shield", 'h'),
        POTION("Magic potion", 'p')
    }
}