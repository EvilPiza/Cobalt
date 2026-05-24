package org.cobalt.ui.screen

import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.module.ModuleManager
import org.cobalt.module.RenderableModule
import org.cobalt.ui.helper.SnapHelper
import org.cobalt.ui.theme.Theme
import org.cobalt.ui.theme.ThemeManager
import org.cobalt.util.Dimensions
import org.cobalt.util.MouseUtils
import org.cobalt.util.MouseUtils.mouseX
import org.cobalt.util.MouseUtils.mouseY
import org.cobalt.util.Vec2f
import org.cobalt.util.WindowUtils.windowHeight
import org.cobalt.util.WindowUtils.windowWidth
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaTransforms

internal object HudEditorScreen : Screen(Component.empty()) {

  private val modules: List<RenderableModule>
    get() = ModuleManager.modules
      .filterIsInstance<RenderableModule>()
      .filter { it.enabled }

  private var selectedModule: RenderableModule? = null
  private val snapHelper = SnapHelper()

  private val theme: Theme
    get() = ThemeManager.activeTheme

  private var isDraggingMove = false
  private var dragOffsetX: Float = 0.0f
  private var dragOffsetY: Float = 0.0f

  private var isDraggingResize = false
  private var initialMouseX = 0f
  private var initialWidth = 0f

  override fun added() {
    EventBus.register(this)
  }

  override fun removed() {
    EventBus.unregister(this)
  }

  @SubscribeEvent
  fun onSkiaDraw(@Suppress("UnusedParameter") event: SkiaDrawEvent) {
    if (minecraft.screen != this) {
      return
    }

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
    if (!isDraggingMove) {
      return
    }

    snapHelper.activeGuides.forEach { guide ->
      if (guide.isVertical) {
        SkiaShapes.drawLine(
          Vec2f(guide.position, 0f),
          Vec2f(guide.position, windowHeight),
          theme.accentPrimary.rgb,
          thickness = 1f
        )
      } else {
        SkiaShapes.drawLine(
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

    SkiaShapes.drawOutline(
      module.screenPosition,
      module.dimensions,
      (if (isSelected) theme.accentPrimary else theme.border).rgb,
      thickness = 2f
    )

    if (isSelected) {
      val squareOffset = SQUARE_SIZE / 2.0f
      val squareX = x + width - squareOffset
      val squareY = y + height - squareOffset

      SkiaShapes.drawRect(
        Vec2f(squareX, squareY),
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
      val (x, y) = module.screenPosition
      val scaledWidth = module.dimensions.width * module.scale
      val scaledHeight = module.dimensions.height * module.scale

      if (module == selectedModule && tryStartResize(module)) {
        return@firstOrNull true
      }

      if (MouseUtils.isHoveringOver(x, y, scaledWidth, scaledHeight)) {
        isDraggingMove = true
        return@firstOrNull true
      }

      return@firstOrNull false
    }

    selectedModule = clickedModule

    if (selectedModule != null && isDraggingMove) {
      dragOffsetX = mouseX - selectedModule!!.xPos
      dragOffsetY = mouseY - selectedModule!!.yPos
    }

    return clickedModule != null || super.mouseClicked(event, doubleClick)
  }

  private fun tryStartResize(module: RenderableModule): Boolean {
    val (x, y) = module.screenPosition
    val scaledWidth = module.dimensions.width * module.scale
    val scaledHeight = module.dimensions.height * module.scale
    val squareSizeScaled = SQUARE_SIZE * module.scale
    val squareOffset = squareSizeScaled / 2f
    val squareX = x + scaledWidth - squareOffset
    val squareY = y + scaledHeight - squareOffset

    if (!MouseUtils.isHoveringOver(squareX, squareY, squareSizeScaled, squareSizeScaled)) {
      return false
    }

    initialMouseX = mouseX
    initialWidth = scaledWidth
    isDraggingResize = true
    return true
  }

  override fun mouseDragged(event: MouseButtonEvent, dx: Double, dy: Double): Boolean {
    val module = selectedModule ?: return super.mouseDragged(event, dx, dy)

    val handled = when {
      isDraggingResize -> handleResize(module)
      isDraggingMove -> handleMove(module)
      else -> false
    }

    return if (handled) true else super.mouseDragged(event, dx, dy)
  }

  private fun handleResize(module: RenderableModule): Boolean {
    val baseWidth = module.getWidth()

    if (baseWidth <= 0f) {
      return false
    }

    val newWidth = initialWidth + (mouseX - initialMouseX)
    module.scale = (newWidth / baseWidth).coerceIn(MIN_SCALE, MAX_SCALE)
    return true
  }

  private fun handleMove(module: RenderableModule): Boolean {
    val rawX = mouseX - dragOffsetX
    val rawY = mouseY - dragOffsetY

    val width = module.getWidth() * module.scale
    val height = module.getHeight() * module.scale

    val clampedX = rawX.coerceIn(0f, windowWidth - width)
    val clampedY = rawY.coerceIn(0f, windowHeight - height)

    val otherBounds = modules
      .filter { it != module }
      .map { other ->
        val (ox, oy) = other.screenPosition
        SnapHelper.ModuleBounds(ox, oy, other.dimensions.width * other.scale, other.dimensions.height * other.scale)
      }

    val (snappedX, snappedY) = snapHelper.findAlignmentGuides(
      clampedX, clampedY,
      width, height,
      windowWidth, windowHeight,
      otherBounds
    )

    val (offsetX, offsetY) =
      computeOffsets(module.anchor, snappedX, snappedY, width, height, windowWidth, windowHeight)

    module.offsetX = offsetX
    module.offsetY = offsetY

    return true
  }

  private fun computeOffsets(
    anchor: RenderableModule.Anchor,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    screenWidth: Float,
    screenHeight: Float,
  ): Vec2f {
    val centerX = screenWidth / 2f - width / 2f
    val centerY = screenHeight / 2f - height / 2f
    val rightX = screenWidth - width - x
    val bottomY = screenHeight - height - y

    return when (anchor) {
      RenderableModule.Anchor.TOP_LEFT -> Vec2f(x, y)
      RenderableModule.Anchor.TOP_CENTER -> Vec2f(x - centerX, y)
      RenderableModule.Anchor.TOP_RIGHT -> Vec2f(rightX, y)
      RenderableModule.Anchor.CENTER_LEFT -> Vec2f(x, y - centerY)
      RenderableModule.Anchor.CENTER -> Vec2f(x - centerX, y - centerY)
      RenderableModule.Anchor.CENTER_RIGHT -> Vec2f(rightX, y - centerY)
      RenderableModule.Anchor.BOTTOM_LEFT -> Vec2f(x, bottomY)
      RenderableModule.Anchor.BOTTOM_CENTER -> Vec2f(x - centerX, bottomY)
      RenderableModule.Anchor.BOTTOM_RIGHT -> Vec2f(rightX, bottomY)
    }
  }

  override fun mouseReleased(event: MouseButtonEvent): Boolean {
    if (isDraggingResize || isDraggingMove) {
      selectedModule?.saveConfig()
    }

    isDraggingResize = false
    isDraggingMove = false
    dragOffsetX = 0.0f
    dragOffsetY = 0.0f

    snapHelper.clearGuides()

    return super.mouseReleased(event)
  }

  private const val SQUARE_SIZE = 10.0f
  private const val MIN_SCALE = 0.75f
  private const val MAX_SCALE = 2.0f

}
