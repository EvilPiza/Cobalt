package org.cobalt.ui.screen

import net.minecraft.client.input.MouseButtonEvent
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.module.ModuleManager
import org.cobalt.module.RenderableModule
import org.cobalt.ui.UIScreen
import org.cobalt.ui.helper.DragHandler
import org.cobalt.ui.helper.SnapHelper
import org.cobalt.ui.theme.Theme
import org.cobalt.ui.theme.ThemeManager
import org.cobalt.util.Dimensions
import org.cobalt.util.MouseUtils
import org.cobalt.util.Vec2f
import org.cobalt.util.WindowUtils.windowHeight
import org.cobalt.util.WindowUtils.windowWidth
import org.cobalt.util.skia.SkiaOutlines
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaTransforms

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

  @SubscribeEvent
  fun onSkiaDraw(ignored: SkiaDrawEvent) {
    if (minecraft.screen != this) return

    modules.forEach { module ->
      val (x, y) = module.screenPosition
      val scale = module.scale

      SkiaTransforms.save()
      SkiaTransforms.translate(Vec2f(x, y))
      SkiaTransforms.scale(Vec2f(scale, scale))
      SkiaTransforms.translate(Vec2f(-x, -y))
      drawRenderableModule(module)
      SkiaTransforms.restore()
    }

    drawSnapGuides()
  }

  private fun drawSnapGuides() {
    if (!dragHandler.isMoving) {
      return
    }

    snapHelper.activeGuides.forEach { guide ->
      if (guide.isVertical) {
        SkiaOutlines.drawLine(
          Vec2f(guide.position, 0f),
          Vec2f(guide.position, windowHeight),
          theme.accentPrimary.rgb,
          thickness = 1f
        )
      } else {
        SkiaOutlines.drawLine(
          Vec2f(0f, guide.position),
          Vec2f(windowWidth, guide.position),
          theme.accentPrimary.rgb,
          thickness = 1f
        )
      }
    }
  }

  private fun drawRenderableModule(module: RenderableModule) {
    val (x, y) = module.screenPosition
    val (width, height) = module.dimensions
    val isSelected = module == selectedModule

    module.renderComponent()

    SkiaOutlines.drawOutline(
      module.screenPosition,
      module.dimensions,
      (if (isSelected) theme.accentPrimary else theme.border).rgb
    )

    if (isSelected) {
      val squareOffset = SQUARE_SIZE / 2.0f
      SkiaShapes.drawRect(
        Vec2f(x + width - squareOffset, y + height - squareOffset),
        Dimensions(SQUARE_SIZE, SQUARE_SIZE),
        theme.accentPrimary.rgb
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
          module.screenPosition.x, module.screenPosition.y,
          module.dimensions.width * module.scale,
          module.dimensions.height * module.scale
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
