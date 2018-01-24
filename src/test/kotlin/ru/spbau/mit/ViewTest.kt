package ru.spbau.mit

import org.junit.Test
import ru.spbau.mit.main.GridEngine
import ru.spbau.mit.main.HoldPolicy
import ru.spbau.mit.main.RogalixModel
import ru.spbau.mit.view.LoggerView
import ru.spbau.mit.view.SwingTilesView

class ViewTest {
    @Test
    fun testTickGoesWell() {
        val model1 = RogalixModel(LoggerView(), GridEngine(), HoldPolicy())
        model1.tick()
        val model2 = RogalixModel(SwingTilesView(), GridEngine(), HoldPolicy())
        model2.tick()
    }
}