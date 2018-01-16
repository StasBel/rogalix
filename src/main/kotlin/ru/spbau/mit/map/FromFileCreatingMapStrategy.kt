package ru.spbau.mit.map

import ru.spbau.mit.world.CreatureType
import ru.spbau.mit.world.ItemType
import ru.spbau.mit.world.Pos
import ru.spbau.mit.world.Race
import java.io.File

/**
 * Implementation of creating map strategy [CreatingMapStrategy]. Reads map
 * from a file, then added blocking walls to it.
 * @author belaevstanislav
 * @param filePath a path to a file to open
 */
class FromFileCreatingMapStrategy(private val filePath: String) : CreatingMapStrategy {
    /**
     * Some reading parameters helping to convert chars to inner class structure.
     */
    private object ReaderParams {
        val PLAYER_STARTING_POSS_SET = setOf(
                'S'
        )

        val CELL_TYPE_MAP = mapOf(
                '#' to CellType.WALL,
                '.' to CellType.EMPTY
        )

        val ITEM_TYPE_MAP = mapOf(
                '1' to ItemType.SWORD,
                '0' to ItemType.SHIELD,
                'h' to ItemType.MAGIC_HAT
        )

        val MOB_MAP = mapOf(
                'O' to Race.ORK,
                'H' to Race.HUMAN
        )
    }

    /**
     * Reads map from a file. Add blocking walls around. Add mobs and players
     * according to the maps in [ReaderParams].
     * @return pair of [Map] and [ProtoWorld]
     */
    override fun create(): Pair<Map, ProtoWorld> {
        // reading from file
        val file = File(filePath)
        val chars: List<List<Char>> = file.readLines()
                .map { it.trim().toCharArray().toList() }
                .let { // adding up and down block wall
                    fun gen(size: Int): List<List<Char>> {
                        val list = mutableListOf<Char>()
                        for (i in 0..size - 1) {
                            list.add('#')
                        }
                        return list.let { it -> listOf(it.toList()) }
                    }

                    gen(it[0].size).plus(it).plus(gen(it[it.size - 1].size))
                }
                .map { // adding left and right block wall
                    it ->
                    listOf('#').plus(it).plus('#')
                }

        // adding delta to every cell
        val charsWithPoss = chars.mapIndexed { x, l -> l.mapIndexed { y, c -> Pair(c, Pos(x, y)) } }

        // map cells
        val mapCells = chars
                .map { it -> it.map { c -> ReaderParams.CELL_TYPE_MAP[c] ?: CellType.EMPTY }.toTypedArray() }
                .toTypedArray()

        // proto creatures
        val protoCreatures = mutableListOf<ProtoCreature>()

        // players
        charsWithPoss
                .flatten()
                .filter { p -> p.first in ReaderParams.PLAYER_STARTING_POSS_SET }
                .map { it.second }
                .forEach { protoCreatures.add(ProtoCreature(it, CreatureType.PLAYER, Race.HUMAN)) }

        // mobs
        charsWithPoss
                .flatten()
                .filter { p -> p.first in ReaderParams.MOB_MAP.keys }
                .forEach {
                    protoCreatures.add(ProtoCreature(
                            it.second,
                            CreatureType.MOB,
                            ReaderParams.MOB_MAP[it.first]!!))
                }

        // proto items
        val protoItems = mutableListOf<ProtoItem>()
        charsWithPoss
                .flatten()
                .filter { p -> p.first in ReaderParams.ITEM_TYPE_MAP.keys }
                .forEach { protoItems.add(ProtoItem(it.second, ReaderParams.ITEM_TYPE_MAP[it.first]!!)) }

        return Pair(SimpleMap(mapCells), ProtoWorld(protoCreatures, protoItems))
    }
}