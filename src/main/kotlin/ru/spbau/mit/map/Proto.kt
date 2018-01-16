package ru.spbau.mit.map

import ru.spbau.mit.world.CreatureType
import ru.spbau.mit.world.ItemType
import ru.spbau.mit.world.Pos
import ru.spbau.mit.world.Race

/**
 * Storing proto creature. Proto creature is essentially a bunch
 * but incomplete characteristic of [Creature].
 * @author belaevstanislav
 */
data class ProtoCreature(val pos: Pos,
                         val creatureType: CreatureType,
                         val race: Race)

/**
 * Storing proto item. Proto item is essentially a bunch
 * but incomplete characteristic of [Item].
 * @author belaevstanislav
 */
data class ProtoItem(val pos: Pos,
                     val type: ItemType)

/**
 * Storing proto world. Proto world is essentially a bunch
 * but incomplete characteristic of [World].
 * @author belaevstanislav
 */
data class ProtoWorld(val creatures: List<ProtoCreature>,
                      val items: List<ProtoItem>)