package ru.spbau.mit.map

import ru.spbau.mit.world.Pos

/**
 * Just simple [Map] implementation with array of arrays of [CellType].
 * @author belaevstanislav
 */
class SimpleMap(private val cells: Array<Array<CellType>>) : Map {
    /**
     * Get type of the cell in the pos.
     * @param pos a pos to watch cell type in
     * @return cell type
     */
    override fun getCellType(pos: Pos): CellType = cells[pos.x][pos.y]

    /**
     * Get maximum size of x axis.
     * @return size of x axis
     */
    override fun getXSize(): Int = cells.size

    /**
     * Get maximum size of y axis.
     * @return size of y axis
     */
    override fun getYSIze(): Int = cells.map { it.size }.max() ?: 0
}