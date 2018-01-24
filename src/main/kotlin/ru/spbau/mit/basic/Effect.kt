package ru.spbau.mit.basic

/**
 * Highly usable class for denoting every effect that can temporarily or constantly affect params.
 * Implementation includes convenient operators for combining effects.
 * Note, that to refer to zero effect, better call [Effect.NIL], than just create an instance with
 * no parameters (this increase readability).
 */
data class Effect(
        val hp: Int = 0,
        val maxHp: Int = 0,
        val attack: Int = 0,
        val armor: Int = 0
) {
    companion object {
        val NIL = Effect()
    }

    operator fun unaryPlus() = copy()

    operator fun unaryMinus() =
            Effect(
                    hp = -hp,
                    maxHp = -maxHp,
                    attack = -attack,
                    armor = -armor
            )

    operator fun plus(that: Effect) =
            Effect(
                    hp = hp + that.hp,
                    maxHp = maxHp + that.maxHp,
                    attack = attack + that.attack,
                    armor = armor + that.armor
            )

    operator fun minus(that: Effect) = this + (-that)
}