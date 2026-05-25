package org.cobalt.ui.helper

import kotlin.math.abs
import org.cobalt.util.Vec2f

internal class SnapHelper(private val snapThreshold: Float = 5f) {

  var activeGuides: List<GuideLine> = emptyList()
    private set

  fun clearGuides() {
    activeGuides = emptyList()
  }

  fun findAlignmentGuides(
    moduleX: Float,
    moduleY: Float,
    moduleW: Float,
    moduleH: Float,
    screenWidth: Float,
    screenHeight: Float,
    otherModuleBounds: List<ModuleBounds>,
  ): Vec2f {
    val xTargets = buildXTargets(screenWidth, otherModuleBounds)
    val yTargets = buildYTargets(screenHeight, otherModuleBounds)

    val (snappedX, xGuide) = snapAxis(moduleX, moduleW, xTargets, isVertical = true)
    val (snappedY, yGuide) = snapAxis(moduleY, moduleH, yTargets, isVertical = false)

    activeGuides = listOfNotNull(xGuide, yGuide)

    return Vec2f(snappedX, snappedY)
  }

  private fun buildXTargets(screenWidth: Float, bounds: List<ModuleBounds>): List<Float> =
    mutableListOf(0f, screenWidth / 2f, screenWidth).apply {
      bounds.forEach { add(it.x); add(it.x + it.width); add(it.x + it.width / 2f) }
    }

  private fun buildYTargets(screenHeight: Float, bounds: List<ModuleBounds>): List<Float> =
    mutableListOf(0f, screenHeight / 2f, screenHeight).apply {
      bounds.forEach { add(it.y); add(it.y + it.height); add(it.y + it.height / 2f) }
    }

  private fun snapAxis(pos: Float, size: Float, targets: List<Float>, isVertical: Boolean): Pair<Float, GuideLine?> {
    val center = pos + size / 2f
    val far = pos + size
    var best = pos
    var bestDiff = snapThreshold + 1f
    var guide: GuideLine? = null

    targets.forEach { target ->
      val candidates = listOf(pos to target, center to target - size / 2f, far to target - size)
      candidates.forEach { (edge, newPos) ->
        val diff = abs(edge - target)
        if (diff <= snapThreshold && diff < bestDiff) {
          bestDiff = diff
          best = newPos
          guide = GuideLine(isVertical, target)
        }
      }
    }

    return best to guide
  }

  data class GuideLine(val isVertical: Boolean, val position: Float)
  data class ModuleBounds(val x: Float, val y: Float, val width: Float, val height: Float)

}
