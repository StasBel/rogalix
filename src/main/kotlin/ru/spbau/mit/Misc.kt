package ru.spbau.mit

import java.util.*
import kotlin.reflect.KProperty

/**
 * Here, you can find lots of misc stuff, used in various parts of project.
 */


@Suppress("unused")
/**
 * Lazy-evaluated property.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class LazyPropertyDelegate<R, out T>(private val initializer: R.() -> T) {
    private var values: MutableMap<R, T> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
            with(thisRef as R) { values.getOrPut(thisRef) { thisRef.initializer() } }
}

/**
 * Outputting random element from an iterable.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
fun <T> Iterable<T>.random(): T = toList().let { it[Random().nextInt(it.size)] }

/**
 * Outputting random element from an array.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
fun <T> Array<T>.random(): T = toList().let { it[Random().nextInt(it.size)] }


/**
 * Memoization for one-arg function.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class Memoize1<in T, out R>(private val f: (T) -> R) : (T) -> R {
    private val values = mutableMapOf<T, R>()
    override fun invoke(x: T): R {
        return values.getOrPut(x, { f(x) })
    }
}

/**
 * Applying memoization for one-arg function.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
fun <T, R> ((T) -> R).memoize(): (T) -> R = Memoize1(this)