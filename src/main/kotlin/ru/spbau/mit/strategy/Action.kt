package ru.spbau.mit.strategy

import ru.spbau.mit.world.Item

/**
 * A sealed class of creature actions.
 * @author belaevstanislav
 */
sealed class Action {
    /**
     * Force quitting initialize by user.
     */
    class ForceQuit : Action()

    /**
     * Doing nothing action.
     */
    class Nothing : Action()

    /**
     * An action of moving.
     */
    class Move(val move: ru.spbau.mit.world.Move) : Action()

    /**
     * An action of toggling an item.
     */
    class ToggleItem(val item: Item) : Action()
}