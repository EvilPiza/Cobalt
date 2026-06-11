package org.cobalt.ui.helper

import org.cobalt.module.RenderableModule
import org.cobalt.util.MouseUtils
import org.cobalt.util.MouseUtils.mouseX
import org.cobalt.util.MouseUtils.mouseY
import org.cobalt.util.WindowUtils.windowHeight
import org.cobalt.util.WindowUtils.windowWidth

class DragHandler {

  var isMoving = false
    private set

  var isResizing = false
    private set

  val isActive
    get() = isMoving || isResizing

  private var dragOffsetX = 0f
  private var dragOffsetY = 0f
  private var initialMouseX = 0f
  private var initialWidth = 0f

  fun startMove(module: RenderableModule) {
    isMoving = true
    dragOffsetX = mouseX - module.xPos
    dragOffsetY = mouseY - module.yPos
  }

  fun tryStartResize(module: RenderableModule, squareSize: Float): Boolean {
    val (x, y) = module.screenPosition
    val scaledWidth = module.getWidth() * module.scale
    val scaledHeight = module.getHeight() * module.scale
    val squareSizeScaled = squareSize * module.scale
    val squareOffset = squareSizeScaled / 2f

    if (
      !MouseUtils.isHoveringOver(
        x + scaledWidth - squareOffset,
        y + scaledHeight - squareOffset,
        squareSizeScaled, squareSizeScaled
      )
    ) {
      return false
    }

    initialMouseX = mouseX
    initialWidth = scaledWidth
    isResizing = true

    return true
  }

  fun handleDrag(
    module: RenderableModule,
    allModules: List<RenderableModule>,
    snapHelper: SnapHelper,
  ): Boolean = when {
    isResizing -> handleResize(module)
    isMoving -> handleMove(module, allModules, snapHelper)
    else -> false
  }

  private fun handleResize(module: RenderableModule): Boolean {
    val baseWidth = module.getWidth().takeIf { it > 0f } ?: return false
    module.scale = ((initialWidth + (mouseX - initialMouseX)) / baseWidth).coerceIn(0.75f, 2.0f)
    return true
  }

  private fun handleMove(
    module: RenderableModule,
    allModules: List<RenderableModule>,
    snapHelper: SnapHelper,
  ): Boolean {
    val width = module.getWidth() * module.scale
    val height = module.getHeight() * module.scale
    val clampedX = (mouseX - dragOffsetX).coerceIn(0f, windowWidth - width)
    val clampedY = (mouseY - dragOffsetY).coerceIn(0f, windowHeight - height)

    val otherBounds = allModules
      .filter { it != module }
      .map { other ->
        val (ox, oy) = other.screenPosition
        SnapHelper.ModuleBounds(ox, oy, other.getWidth() * other.scale, other.getHeight() * other.scale)
      }

    val (snappedX, snappedY) = snapHelper.findAlignmentGuides(
      clampedX, clampedY, width, height, windowWidth, windowHeight, otherBounds
    )

    val (offsetX, offsetY) = computeOffsets(
      module.anchor, snappedX, snappedY, width, height, windowWidth, windowHeight
    )

    module.offsetX = offsetX
    module.offsetY = offsetY
    return true
  }

  fun reset() {
    isMoving = false
    isResizing = false
    dragOffsetX = 0f
    dragOffsetY = 0f
  }

  private fun computeOffsets(
    anchor: RenderableModule.Anchor,
    x: Float, y: Float,
    width: Float, height: Float,
    screenWidth: Float, screenHeight: Float,
  ): Pair<Float, Float> {
    val anchorX = when (anchor) {
      RenderableModule.Anchor.TOP_LEFT,
      RenderableModule.Anchor.CENTER_LEFT,
      RenderableModule.Anchor.BOTTOM_LEFT,
        -> x

      RenderableModule.Anchor.TOP_CENTER,
      RenderableModule.Anchor.CENTER,
      RenderableModule.Anchor.BOTTOM_CENTER,
        -> x + width / 2f

      RenderableModule.Anchor.TOP_RIGHT,
      RenderableModule.Anchor.CENTER_RIGHT,
      RenderableModule.Anchor.BOTTOM_RIGHT,
        -> x + width
    }

    val anchorY = when (anchor) {
      RenderableModule.Anchor.TOP_LEFT,
      RenderableModule.Anchor.TOP_CENTER,
      RenderableModule.Anchor.TOP_RIGHT,
        -> y

      RenderableModule.Anchor.CENTER_LEFT,
      RenderableModule.Anchor.CENTER,
      RenderableModule.Anchor.CENTER_RIGHT,
        -> y + height / 2f

      RenderableModule.Anchor.BOTTOM_LEFT,
      RenderableModule.Anchor.BOTTOM_CENTER,
      RenderableModule.Anchor.BOTTOM_RIGHT,
        -> y + height
    }

    return Pair(anchorX / screenWidth, anchorY / screenHeight)
  }
}
