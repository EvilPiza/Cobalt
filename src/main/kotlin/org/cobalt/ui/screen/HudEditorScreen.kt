package org.cobalt.ui.screen

import net.minecraft.client.input.MouseButtonEvent
import org.cobalt.event.EventBus
import org.cobalt.module.ModuleManager
import org.cobalt.module.RenderableModule
import org.cobalt.ui.UIScreen
import org.cobalt.ui.helper.DragHandler
import org.cobalt.ui.helper.SnapHelper
import org.cobalt.ui.theme.Theme
import org.cobalt.ui.theme.ThemeManager
import org.cobalt.util.MouseUtils
import org.cobalt.util.WindowUtils.windowHeight
import org.cobalt.util.WindowUtils.windowWidth
import org.cobalt.util.skia.Skia

internal object HudEditorScreen : UIScreen() {

  private val modules: List<RenderableModule>
    get() = ModuleManager.modules
      .filterIsInstance<RenderableModule>()
      .filter { it.enabled }

  private var selectedModule: RenderableModule? = null
  private val snapHelper = SnapHelper()
  private val dragHandler = DragHandler()

  private val theme: Theme
    get() = ThemeManager.activeTheme

  override fun added() = EventBus.register(this)
  override fun removed() = EventBus.unregister(this)

  override fun renderSkia() {
    modules.forEach { module ->
      val (x, y) = module.screenPosition
      val scale = module.scale

      Skia.push()
      Skia.translate(x, y)
      Skia.scale(scale, scale)
      Skia.translate(-x, -y)

      drawRenderableModule(module)

      Skia.pop()
    }

    drawSnapGuides()
  }

  private fun drawSnapGuides() {
    if (!dragHandler.isMoving) {
      return
    }

    snapHelper.activeGuides.forEach { guide ->
      if (guide.isVertical) {
        Skia.line(
          guide.position, 0f,
          guide.position, windowHeight,
          1f, theme.accentPrimary
        )
      } else {
        Skia.line(
          0f, guide.position,
          windowWidth, guide.position,
          1f, theme.accentPrimary
        )
      }
    }
  }

  private fun drawRenderableModule(module: RenderableModule) {
    val (x, y) = module.screenPosition
    val (width, height) = module.dimensions
    val isSelected = module == selectedModule

    module.renderComponent()

    Skia.outline(
      x, y, width, height, 1f,
      if (isSelected) theme.accentPrimary else theme.border
    )

    if (isSelected) {
      val squareOffset = SQUARE_SIZE / 2.0f
      Skia.rect(
        x + width - squareOffset, y + height - squareOffset,
        SQUARE_SIZE, SQUARE_SIZE,
        theme.accentPrimary
      )
    }
  }

  override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
    if (event.button() != 0) {
      return super.mouseClicked(event, doubleClick)
    }

    val clickedModule = modules.firstOrNull { module ->
      if (module == selectedModule && dragHandler.tryStartResize(module, SQUARE_SIZE)) {
        return@firstOrNull true
      }

      if (
        MouseUtils.isHoveringOver(
          module.xPos, module.yPos,
          module.getWidth() * module.scale,
          module.getHeight() * module.scale
        )
      ) {
        dragHandler.startMove(module)
        return@firstOrNull true
      }

      return@firstOrNull false
    }

    selectedModule = clickedModule
    return clickedModule != null || super.mouseClicked(event, doubleClick)
  }

  override fun mouseDragged(event: MouseButtonEvent, dx: Double, dy: Double): Boolean {
    val module = selectedModule ?: return super.mouseDragged(event, dx, dy)
    val handled = dragHandler.handleDrag(module, modules, snapHelper)

    return if (handled) {
      true
    } else {
      super.mouseDragged(event, dx, dy)
    }
  }

  override fun mouseReleased(event: MouseButtonEvent): Boolean {
    if (dragHandler.isActive) {
      selectedModule?.saveConfig()
    }

    dragHandler.reset()
    snapHelper.clearGuides()

    return super.mouseReleased(event)
  }

  private const val SQUARE_SIZE = 10.0f

}
