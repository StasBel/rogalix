package ru.spbau.mit.view

import ru.spbau.mit.basic.*
import ru.spbau.mit.basic.Unit
import ru.spbau.mit.env.Env
import ru.spbau.mit.env.TilesPrimitives
import ru.spbau.mit.main.World
import ru.spbau.mit.memoize
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.WindowEvent
import java.awt.image.ImageObserver
import java.io.File
import java.util.concurrent.LinkedBlockingQueue
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.math.max


/**
 * Rendering current state using swing [Graphics].
 *
 * @constructor Straightforward
 * @param dim Screen dimension.
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class SwingTilesView(dim: Pt = Pt(30, 30)) : View {
    private val jFrame = JFrame()
    private val tilesPanel = TilesJPanel(dim)
    private var tickNum = 0
    private val queue = LinkedBlockingQueue<Char>()

    init {
        SwingUtilities.invokeLater {
            jFrame.title = "Tick $tickNum"
            jFrame.add(tilesPanel)
            jFrame.addKeyListener(object : KeyListener {
                override fun keyTyped(e: KeyEvent?) {}

                override fun keyPressed(e: KeyEvent) {
                    queue.add(e.keyChar)
                }

                override fun keyReleased(e: KeyEvent?) {}
            })
            jFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            jFrame.pack()
            jFrame.isResizable = false
            jFrame.isVisible = true
        }
    }

    /**
     * Render current game state.
     *
     * @receiver An instance of [World], visibleWorld by [self].
     * @param self An instance of [Creature] - center unit to draw. Other world is just a
     * visible world from his point of view.
     */
    override fun World.renderFrom(self: Creature.Player) {
        SwingUtilities.invokeLater {
            tickNum += 1
            jFrame.title = "Tick $tickNum"
            tilesPanel.world = this
            tilesPanel.self = self
            jFrame.repaint()
        }
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     */
    override fun close() = jFrame.dispatchEvent(WindowEvent(jFrame, WindowEvent.WINDOW_CLOSING))

    /**
     * Function, for getting next char from user keyboard.
     * Can only capture this properly inside [JFrame] as a listener, so has to provide it as public.
     */
    fun getCh(): Char = queue.take()!!
}

/**
 * Main class for rendering current game state.
 * Each game element is drawing using tiles.
 *
 * @constructor Takes some of the consts.
 * @param dim Screen dimension.
 * @param tileSize Tile size (in pixels).
 *
 * @author Stanislav Belyaev stasbelyaev96@gmail.com
 */
class TilesJPanel(
        private val dim: Pt = Pt(30, 30),
        private val tileSize: Int = 20
) : JPanel() {
    // Nasty stuff, but this updated a lot faster than a separate class for each rendering
    lateinit var world: World
    lateinit var self: Creature

    init {
        val realDim = calcHalfDim() * 2 - 1
        preferredSize = Dimension(
                realDim.x * tileSize,
                realDim.y * tileSize + tileSize
        )
    }

    /**
     * Calls the UI delegate's paint method, if the UI delegate
     * is non-`null`.  We pass the delegate a copy of the
     * `Graphics` object to protect the rest of the
     * paint code from irrevocable changes
     * (for example, `Graphics.translate`).
     *
     *
     * If you override this in a subclass you should not make permanent
     * changes to the passed in `Graphics`. For example, you
     * should not alter the clip `Rectangle` or modify the
     * transform. If you need to do these operations you may find it
     * easier to create a new `Graphics` from the passed in
     * `Graphics` and manipulate it. Further, if you do not
     * invoker super's implementation you must honor the opaque property,
     * that is
     * if this component is opaque, you must completely fill in the background
     * in a non-opaque color. If you do not honor the opaque property you
     * will likely see visual artifacts.
     *
     *
     * The passed in `Graphics` object might
     * have a transform other than the identify transform
     * installed on it.  In this case, you might get
     * unexpected results if you cumulatively apply
     * another transform.
     */
    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if (!::world.isInitialized or !::self.isInitialized) {
            drawLoading(g as Graphics)
            return
        }
        world.draw(self, g as Graphics2D)
    }

    private fun drawLoading(g: Graphics) {
        val loadingImage =
                ImageIO.read(File("src/main/resources/loading.png"))
        g.drawImage(
                loadingImage,
                (width - loadingImage.width) / 2, (height - loadingImage.height) / 2,
                null
        )
    }

    private fun calcHalfDim(): Pt = (dim / 2).let {
        when {
            (it.x % 2 == 0) and (it.y % 2 == 0) -> Pt(it.x + 1, it.y + 1)
            (it.x % 2 == 0) -> Pt(it.x + 1, it.y)
            (it.y % 2 == 0) -> Pt(it.x, it.y + 1)
            else -> it
        }
    }

    private val tileToImage = { t: Env.Tile ->
        when (t) {
            Env.Tile.FLOOR -> ImageIO.read(File("src/main/resources/floor.png"))
            Env.Tile.WALL -> ImageIO.read(File("src/main/resources/wall.png"))
            Env.Tile.PORTAL -> ImageIO.read(File("src/main/resources/portal.png"))
            Env.Tile.SHADOW -> ImageIO.read(File("src/main/resources/shadow.png"))
            Env.Tile.VOID -> ImageIO.read(File("src/main/resources/void.png"))
        }
    }.memoize()

    private val unitToImage = { u: Unit ->
        when (u) {
            is Creature -> when (u) {
                is Creature.Player ->
                    ImageIO.read(File("src/main/resources/player.png"))
                is Creature.Mob -> when (u.race) {
                    Creature.Race.HUMAN ->
                        ImageIO.read(File("src/main/resources/mob_human.png"))
                    Creature.Race.ORK ->
                        ImageIO.read(File("src/main/resources/mob_ork.png"))
                    Creature.Race.ELF ->
                        ImageIO.read(File("src/main/resources/mob_elf.png"))
                }
            }
            is Item -> when (u.name) {
                Item.Name.SWORD ->
                    ImageIO.read(File("src/main/resources/item_sword.png"))
                Item.Name.SHIELD ->
                    ImageIO.read(File("src/main/resources/item_shield.png"))
                Item.Name.POTION ->
                    ImageIO.read(File("src/main/resources/item_potion.png"))
            }
        }
    }.memoize()

    private fun World.draw(self: Creature, g: Graphics2D) {
        val halfDim = calcHalfDim()
        val shift = halfDim - 1
        val (cols, rows) = halfDim * 2 - 1
        val rect = Pt(width / cols, (height - tileSize) / rows)
        val imageObserver = ImageObserver { _, _, _, _, _, _ -> false }

        drawTiles(halfDim, cols, rows, g, rect, imageObserver)
        drawUnits(g, shift, rect, imageObserver)
        drawHealthBars(shift, rect, g)
        drawStatusBar(cols, g, rect, rows, imageObserver, self)
    }

    private fun World.drawTiles(
            halfDim: Pt, cols: Int, rows: Int, g: Graphics2D, rect: Pt, imageObserver: ImageObserver
    ) {
        val tiles =
                TilesPrimitives.from(env, Box(-halfDim + 1, halfDim - 1))
        for (i in 0 until cols) {
            for (j in 0 until rows) {
                g.drawImage(
                        tileToImage(tiles[i][j]),
                        i * rect.x, j * rect.y,
                        tileSize, tileSize,
                        imageObserver
                )
            }
        }
    }

    private fun World.drawUnits(g: Graphics2D, shift: Pt, rect: Pt, imageObserver: ImageObserver) {
        for (unit in (zoo.creatures + zoo.items)) {
            g.drawImage(
                    unitToImage(unit),
                    (unit.pos.x + shift.x) * rect.x, (unit.pos.y + shift.y) * rect.y,
                    tileSize, tileSize,
                    imageObserver
            )
        }
    }

    private fun World.drawHealthBars(shift: Pt, rect: Pt, g: Graphics2D) {
        for (creature in zoo.creatures) {
            val pos = (creature.pos + shift) * rect
            g.color = Color.GREEN
            g.fillRect(
                    pos.x + 2, pos.y - 6,
                    max(creature.params.hp * (tileSize - 4) / creature.params.maxHp, 1),
                    3
            )
        }
    }

    private fun World.drawStatusBar(
            cols: Int, g: Graphics2D, rect: Pt, rows: Int,
            imageObserver: ImageObserver, self: Creature
    ) {
        for (i in 0 until cols) {
            g.drawImage(
                    tileToImage(Env.Tile.VOID),
                    i * rect.x, rows * rect.y,
                    tileSize, tileSize,
                    imageObserver
            )
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        )
        val status = """
                |`Info` ${self.race}/${self.clazz}/${self.params}
                |  `Used` ${self.equip.used.map { it.name.repr }.toList()}
                |  `Stored` ${self.equip.stored.map { it.name.repr }.toList()}
                """.trimMargin()
        drawCenteredString(
                g,
                status,
                Rectangle(0, rows * tileSize, cols * tileSize, tileSize),
                Font("Comic Sans MS", Font.BOLD, tileSize / 2),
                Color.YELLOW
        )
    }

    private fun drawCenteredString(
            g: Graphics, text: String, rect: Rectangle, font: Font, color: Color
    ) {
        val metrics = g.getFontMetrics(font)
        val x = rect.x + (rect.width - metrics.stringWidth(text)) / 2
        val y = rect.y + (rect.height - metrics.height) / 2 + metrics.ascent
        g.font = font
        g.color = color
        g.drawString(text, x, y)
    }
}
