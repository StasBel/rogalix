package ru.spbau.mit.print

import com.diogonunes.jcdp.color.ColoredPrinter
import com.diogonunes.jcdp.color.api.Ansi
import ru.spbau.mit.console.Console
import ru.spbau.mit.game.Game
import ru.spbau.mit.map.CellType
import ru.spbau.mit.map.Map
import ru.spbau.mit.world.*

/**
 * A simple implementation of terminal ascii printer, implementing [Printer] interface.
 * @author belaevstanislav
 */
class ASCIIPrinter : Printer {
    /**
     * Type of cell consist of attr, foreground color and background color.
     */
    private data class WriterCellType(val attr: Ansi.Attribute = Ansi.Attribute.NONE,
                                      val fcolor: Ansi.FColor = Ansi.FColor.WHITE,
                                      val bcolor: Ansi.BColor = Ansi.BColor.BLACK)

    /**
     * Write cell object consist of [Pos] and [WriterCellType].
     */
    private data class WriterCell(val char: Char,
                                  val type: WriterCellType)

    /**
     * Some useful params about printing.
     */
    private object WriterParams {
        val CELL_TYPE_TO_WRITER_CELL = mapOf(
                CellType.EMPTY to WriterCell('.', WriterCellType()),
                CellType.WALL to WriterCell('#', WriterCellType())
        )

        val PLAYER_WRITER_CELL = WriterCell('P', WriterCellType())

        val MOB_TO_WRITER_CELL = mapOf(
                Race.ORK to WriterCell('O', WriterCellType(fcolor = Ansi.FColor.GREEN)),
                Race.HUMAN to WriterCell('H', WriterCellType(fcolor = Ansi.FColor.BLUE))
        )

        val ITEM_TYPE_TO_WRITER_CELL = mapOf(
                ItemType.SWORD to WriterCell('s', WriterCellType(fcolor = Ansi.FColor.CYAN)),
                ItemType.SHIELD to WriterCell('d', WriterCellType(fcolor = Ansi.FColor.RED)),
                ItemType.MAGIC_HAT to WriterCell('h', WriterCellType(fcolor = Ansi.FColor.MAGENTA))
        )
    }

    private val coloredPrinter = ColoredPrinter.Builder(1, false).build()

    /**
     * Printing out state usuing colored ascii teminal output.
     * @param self a creature to centered in print
     * @param world a world to observe, consist of only entities we know about
     * @param game a instance of [Game]
     */
    override fun print(self: Creature, world: World, map: Map, game: Game) {
        // math
        val (xMaxSize, yMaxSize) = Pair(map.getXSize(), map.getYSIze())
        val xS = run {
            val maxRight = Math.min(self.pos.x + (game.xSize / 2), xMaxSize - 1)
            Math.max(maxRight - game.xSize, 0)
        }
        val xF = Math.min(xS + game.xSize, xMaxSize - 1)
        val yS = run {
            val maxRight = Math.min(self.pos.y + (game.ySize / 2), yMaxSize - 1)
            Math.max(maxRight - game.ySize, 0)
        }
        val yF = Math.min(yS + game.ySize, yMaxSize - 1)

        // initial
        val writerMap = (xS..xF).toList().map {
            x ->
            (yS..yF).toList().map {
                y ->
                WriterParams.CELL_TYPE_TO_WRITER_CELL[map.getCellType(Pos(x, y))]!!
            }.toTypedArray()
        }.toTypedArray()

        // creatures
        world.creatures.map {
            if (it.creatureType == CreatureType.PLAYER) {
                writerMap[it.pos.x - xS][it.pos.y - yS] = WriterParams.PLAYER_WRITER_CELL
            } else {
                writerMap[it.pos.x - xS][it.pos.y - yS] = WriterParams.MOB_TO_WRITER_CELL[it.race]!!
            }
        }

        // items
        world.items.map {
            writerMap[it.pos.x - xS][it.pos.y - yS] = WriterParams.ITEM_TYPE_TO_WRITER_CELL[it.type]!!
        }

        // print sep lines
        Console.clearConsole()

        // main print
        for (xarr in writerMap.withIndex()) {
            for ((char, type) in xarr.value) {
                coloredPrinter.print(char, type.attr, type.fcolor, type.bcolor)
            }
            println()
        }

        // print info
        printStatusBar(self)
    }

    /**
     * Printing status bar showing some info about [self] creature.
     * @param self creature to print info about
     */
    private fun printStatusBar(self: Creature) {
        val writerCellType = WriterCellType()
        coloredPrinter.println("RACE: ${self.race.name}")
        coloredPrinter.println(self.params, writerCellType.attr, writerCellType.fcolor, writerCellType.bcolor)
        println()
        coloredPrinter.println(self.items.getWithId(ItemStatus.USED).joinToString(
                prefix = "USED ITEMS: ",
                separator = " | ",
                transform = { i -> "${i.value.type.name} (${i.index})" }
        ), writerCellType.attr, writerCellType.fcolor, writerCellType.bcolor)
        println()
        coloredPrinter.println(self.items.getWithId(ItemStatus.STORED).joinToString(
                prefix = "STORED ITEMS: ",
                separator = " | ",
                transform = { i -> "${i.value.type.name} (${i.index})" }
        ), writerCellType.attr, writerCellType.fcolor, writerCellType.bcolor)
        println()
    }
}
