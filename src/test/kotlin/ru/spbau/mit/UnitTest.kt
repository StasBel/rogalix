package ru.spbau.mit

import org.junit.Test
import ru.spbau.mit.basic.Creature
import ru.spbau.mit.basic.Effect
import ru.spbau.mit.basic.Item
import kotlin.test.assertTrue

class UnitTest {
    @Test
    fun testItem() {
        val allItems = Item.Name.values()
        assertTrue(
                allItems.size == allItems.map { it.repr }.distinct().count(),
                "Check that all items have their own unique repr"
        )
    }

    @Test
    fun testCreation() {
        assertTrue(
                Item.from(Item.Name.POTION) != Item.from(Item.Name.SHIELD),
                "Check different items"
        )
        assertTrue(
                Item.from(Item.Name.POTION) != Item.from(Item.Name.POTION),
                "Check different items"
        )
        assertTrue(
                Item.from(Item.Name.SWORD) != Item.from(Item.Name.SWORD),
                "Check different items"
        )
    }

    @Test
    fun checkHandicap() {
        val player = Creature.from(Creature.Name.DEFAULT_PLAYER) as Creature.Player
        assertTrue(
                player.handicap != Effect.NIL,
                "Check that we have non zero handicap"
        )
    }
}