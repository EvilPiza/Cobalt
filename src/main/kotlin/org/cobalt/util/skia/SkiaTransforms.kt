package org.cobalt.util.skia

import io.github.humbleui.skija.ClipMode
import io.github.humbleui.types.Rect
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaContext.canvas

object SkiaTransforms {

  private var scissorStackDepth = 0

  @JvmStatic
  fun save() = canvas?.save()

  @JvmStatic
  fun restore() = canvas?.restore()

  @JvmStatic
  fun translate(pos: Vec2f) = canvas?.translate(pos.x, pos.y)

  @JvmStatic
  fun scale(scale: Vec2f) = canvas?.scale(scale.x, scale.y)

  @JvmStatic
  fun pushScissor(pos: Vec2f, dim: Dimensions) {
    val canvas = canvas ?: return

    canvas.save()
    canvas.clipRect(
      Rect.makeXYWH(pos.x, pos.y, dim.width, dim.height),
      ClipMode.INTERSECT, true
    )

    scissorStackDepth++
  }

  @JvmStatic
  fun popScissor() {
    if (scissorStackDepth <= 0) {
      return
    }

    canvas?.restore()
    scissorStackDepth--
  }

}
