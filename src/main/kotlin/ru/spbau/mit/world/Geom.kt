package ru.spbau.mit.world

/**
 * Move type using in game.
 * @author belaevstanislav
 */
enum class Move(val delta: Pos) {
    HOLD(Pos(0, 0)),
    UP(Pos(-1, 0)),
    LEFT(Pos(0, -1)),
    DOWN(Pos(1, 0)),
    RIGHT(Pos(0, 1));
}

/**
 * Basic position on the map grid class which essentially is a pair of [Int].
 * Has some operators overloaded.
 * @author belaevstanislav
 * @param x x pos
 * @param y y pos
 */
data class Pos(var x: Int, var y: Int) {
    companion object {
        /**
         * NULL pos in some cases useful for denoting unattainable items.
         */
        val NULL = Pos(-1, -1)
    }

    operator fun plusAssign(pos: Pos) {
        x += pos.x
        y += pos.y
    }

    operator fun plus(pos: Pos): Pos {
        val res = Pos(x, y)
        res += pos
        return res
    }

    operator fun plusAssign(move: Move) {
        this += move.delta
    }

    /**
     * Basic math operation - checking if [pos] is in [this] visibility rande.
     * @param pos pos to observe
     * @param maxRange this unit seeing range
     * @return true for positive
     */
    fun isSee(pos: Pos, maxRange: Int) = Math.hypot(x.toDouble() - pos.x, y.toDouble() - pos.y) < maxRange
}