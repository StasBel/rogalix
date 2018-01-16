package ru.spbau.mit.world

/**
 * Useful data class for basic unit mutable params with bunch of operators.
 * @author belaevstanislav
 */
data class Params(var hp: Int = 0,
                  var maxHp: Int = 0,
                  var hpRegen: Int = 0,
                  var armor: Int = 0,
                  var attack: Int = 0) {
    companion object {
        val DEFAULT = Params(
                hp = 10,
                maxHp = 10,
                hpRegen = 1,
                armor = 2,
                attack = 4
        )
    }

    private operator fun unaryMinus(): Params {
        return Params(
                hp = -hp,
                maxHp = -maxHp,
                hpRegen = -hpRegen,
                armor = -armor,
                attack = -attack
        )
    }

    operator fun plusAssign(params: Params) {
        hp += params.hp
        maxHp += params.maxHp
        hp = Math.max(hp, 0)
        hp = Math.min(hp, maxHp)
        maxHp = Math.max(maxHp, 0)
        hpRegen += params.hpRegen
        armor += params.armor
        attack += params.attack
        attack = Math.max(attack, 0)
    }

    operator fun minusAssign(params: Params) {
        this += (-params)
    }

    operator fun plus(params: Params): Params {
        val newParams = Params()
        newParams += this
        newParams += params
        return newParams
    }
}