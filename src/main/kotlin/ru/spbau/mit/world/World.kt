package ru.spbau.mit.world

/**
 * A simple class to store world entities like creatures and items.
 * @author belaevstanislav
 * @param creatures a creatures set to store
 * @param items a items set so store
 */
data class World(val creatures: MutableSet<Creature>,
                 val items: MutableSet<Item>)