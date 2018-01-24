package ru.spbau.mit

/**
 * Bunch of Rogalix specific exceptions.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
sealed class RogalixException(message: String) : Exception(message) {
    /**
     * Trying to handle force quit action in engine is forbid, because it's top-level logic.
     * Can't shutdown the engine within engine.
     */
    object HandleQuitInEngine : RogalixException("Trying to handle ForceQuit action in engine.")

    /**
     * Raise, when level generator output map with shadows, which is odd (because, why we need
     * shadows?).
     */
    object ShadowMapGen : RogalixException("Find shadow in generated enviroment.")
}