package ru.spbau.mit.basic

import ru.spbau.mit.random
import java.util.*
import kotlin.math.hypot

/**
 * Screen:
 *   0
 *  *---------------------→ x
 * 0|. Pt(0, 0)
 *  |
 *  |
 *  |
 *  |      . Pt(6, 4)
 *  |
 *  |
 *  |
 *  |
 *  ↓
 *  y
 */

/**
 * Simple point class for denoting both position and intention to move (just for convenience).
 * Includes all types of operations. Most of them straightforward, other have docs.
 * There are special one directions called moves. Check the [Pt.Move] for that.
 */
data class Pt(val x: Int, val y: Int) {
    companion object {
        val NIL = Pt(0, 0)  // Zero vec
        val NULL = Pt(Int.MIN_VALUE, Int.MIN_VALUE)  // For denoting absentation
    }

    /**
     * Calculated distance to given point and coercing in into natural int.
     */
    infix fun distTo(that: Pt) = hypot((x - that.x).toDouble(), (y - that.y).toDouble()).toInt()

    operator fun plus(move: Move) = this + move.dir

    infix fun move(move: Move) = this + move

    operator fun unaryPlus() = Pt(x, y)

    operator fun unaryMinus() = Pt(-x, -y)

    operator fun plus(pt: Pt) = Pt(x + pt.x, y + pt.y)

    operator fun minus(pt: Pt) = this + (-pt)

    operator fun plus(int: Int) = Pt(x + int, y + int)

    operator fun minus(int: Int) = this + (-int)

    operator fun times(int: Int) = Pt(x * int, y * int)

    operator fun times(that: Pt) = Pt(x * that.x, y * that.y)

    operator fun div(int: Int) = Pt(x / int, y / int)

    /**
     * Expand point along every 2d direction, transforming it to box.
     */
    infix fun expand(int: Int) = Box(this - int + 1, this + int - 1)

    /**
     * One step moving into some direction.
     */
    enum class Move(val dir: Pt) {
        HOLD(Pt(0, 0)),
        UP(Pt(0, -1)),
        RIGHT(Pt(+1, 0)),
        DOWN(Pt(0, +1)),
        LEFT(Pt(-1, 0));

        /**
         * Opposite move.
         */
        fun opposite() = when (this) {
            HOLD -> HOLD
            UP -> DOWN
            RIGHT -> LEFT
            DOWN -> UP
            LEFT -> RIGHT
        }

        companion object {
            fun random() = values().random()
        }
    }
}

/**
 * Simple class denoting a box on the 2D grid.
 *
 * @constructor Takes [upperLeft] and [lowerRight] corners of the box to represent.
 * Note that box includes both of corners.
 */
data class Box(val upperLeft: Pt, val lowerRight: Pt) {
    /**
     * Check if input [pt] contains into the box.
     */
    operator fun contains(pt: Pt) =
            (pt.x in upperLeft.x..lowerRight.x) and (pt.y in upperLeft.y..lowerRight.y)

    /**
     * Output all [Pt]'s, that contains into the box.
     */
    fun points(): Set<Pt> =
            (upperLeft.x..lowerRight.x).flatMap { x ->
                (upperLeft.y..lowerRight.y).map { y ->
                    Pt(x, y)
                }
            }.toSet()

    /**
     * Output random point from the box.
     */
    fun random(): Pt {
        val pts = points()
        return pts.toList()[Random().nextInt(pts.size)]
    }
}