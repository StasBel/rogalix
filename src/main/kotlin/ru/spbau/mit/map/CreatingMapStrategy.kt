package ru.spbau.mit.map

/**
 * An interface using for implementing a map creator class.
 * @author belaevstanislav
 */
interface CreatingMapStrategy {
    /**
     * Creates a initial [Map] and set of proto objects as an instance of [ProtoWorld].
     * @return pair of [Map] and [ProtoWorld]
     */
    fun create(): Pair<Map, ProtoWorld>
}