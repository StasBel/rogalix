package ru.spbau.mit.map

import ru.spbau.mit.world.Pos

/**
 * Roguelike world map simple interface.
 * @author belaevstanislav
 */
interface Map {
    /**
     * Get type of the cell in the pos.
     * @param pos a pos to watch cell type in
     * @return cell type
     */
    fun getCellType(pos: Pos): CellType

    /**
     * Get maximum size of x axis.
     * @return size of x axis
     */
    fun getXSize(): Int

    /**
     * Get maximum size of y axis.
     * @return size of y axis
     */
    fun getYSIze(): Int
}