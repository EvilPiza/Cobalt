package org.cobalt.ui.screen

import net.minecraft.client.input.MouseButtonEvent
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.MouseScrollEvent
import org.cobalt.module.Module
import org.cobalt.module.ModuleManager
import org.cobalt.module.type.RenderableModule
import org.cobalt.ui.helper.DragHandler
import org.cobalt.ui.helper.SnapHelper
import org.cobalt.ui.theme.Theme
import org.cobalt.ui.theme.ThemeManager
import org.cobalt.util.MouseUtils
import org.cobalt.util.WindowUtils.scaleX
import org.cobalt.util.WindowUtils.scaleY
import org.cobalt.util.WindowUtils.windowHeight
import org.cobalt.util.WindowUtils.windowWidth
import org.cobalt.util.skia.Skia
import org.cobalt.ui.UIScreen
import org.cobalt.util.helper.Multithreading

object HudEditorScreen : UIScreen() {

  init {
    EventBus.register(this)
  }

  private val modules: List<RenderableModule>
    get() = ModuleManager.modules
      .filterIsInstance<RenderableModule>()
      .filter { it.enabled }

  private var selectedModule: RenderableModule? = null
  private val snapHelper = SnapHelper()
  private val dragHandler = DragHandler()

  private inline val theme: Theme
    get() = ThemeManager.activeTheme

  override fun added() = EventBus.register(this)
  override fun removed() = EventBus.unregister(this)

  override fun renderSkia() {
    modules.forEach { module ->
      Skia.push()

      val renderX = module.xPos * scaleX
      val renderY = module.yPos * scaleY
      val finalScale = module.scale * scaleY

      Skia.translate(renderX, renderY)
      Skia.scale(finalScale, finalScale)
      Skia.translate(-module.xPos, -module.yPos)

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
        Skia.line(guide.position, 0f, guide.position, windowHeight, 1f, theme.accentPrimary)
      } else {
        Skia.line(0f, guide.position, windowWidth, guide.position, 1f, theme.accentPrimary)
      }
    }
  }

  private fun drawRenderableModule(module: RenderableModule) {
    val x = module.xPos
    val y = module.yPos
    val width = module.width
    val height = module.height

    module.renderComponent()

    val isSelected = module == selectedModule

    Skia.outline(x, y, width, height, 1f, if (isSelected) theme.accentPrimary else theme.border)

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
      val renderX = module.xPos * scaleX
      val renderY = module.yPos * scaleY
      val resScale = scaleY
      val scaledWidth = module.width * module.scale * resScale
      val scaledHeight = module.height * module.scale * resScale

      if (module == selectedModule && dragHandler.tryStartResize(
          SQUARE_SIZE,
          renderX,
          renderY,
          scaledWidth,
          scaledHeight
        )
      ) {
        return@firstOrNull true
      }

      if (MouseUtils.isHoveringOver(renderX, renderY, scaledWidth, scaledHeight)) {
        dragHandler.startMove(renderX, renderY)
        return@firstOrNull true
      }

      false
    }

    selectedModule = clickedModule
    return clickedModule != null || super.mouseClicked(event, doubleClick)
  }

  override fun mouseDragged(event: MouseButtonEvent, dx: Double, dy: Double): Boolean {
    val module = selectedModule ?: return super.mouseDragged(event, dx, dy)

    return if (dragHandler.handleDrag(module, modules, snapHelper)) {
      true
    } else {
      super.mouseDragged(event, dx, dy)
    }
  }

  override fun mouseReleased(event: MouseButtonEvent): Boolean {
    dragHandler.reset()
    snapHelper.clearGuides()

    return super.mouseReleased(event)
  }

  override fun onClose() {
    super.onClose()

    Multithreading.runAsync {
      ModuleManager.modules.forEach(Module::saveConfig)
    }
  }

  private const val SQUARE_SIZE = 10.0f
  private const val SCALE_STEP = 0.05f
  private const val MIN_SCALE = 0.75f
  private const val MAX_SCALE = 2.0f

  @SubscribeEvent
  fun onMouseScroll(event: MouseScrollEvent) {
    val module = modules.firstOrNull { candidate ->
      val renderX = candidate.xPos * scaleX
      val renderY = candidate.yPos * scaleY
      val resScale = scaleY
      val scaledWidth = candidate.width * candidate.scale * resScale
      val scaledHeight = candidate.height * candidate.scale * resScale

      MouseUtils.isHoveringOver(renderX, renderY, scaledWidth, scaledHeight)
    } ?: return

    selectedModule = module

    module.scale = (module.scale + (event.verticalAmount.toFloat() * SCALE_STEP))
      .coerceIn(MIN_SCALE, MAX_SCALE)
  }

}
