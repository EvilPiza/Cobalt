package org.cobalt.util.skia

import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaContext.canvas

object SkiaTransforms {

  @JvmStatic
  fun save() = canvas?.save()

  @JvmStatic
  fun restore() = canvas?.restore()

  @JvmStatic
  fun translate(pos: Vec2f) = canvas?.translate(pos.x, pos.y)

  @JvmStatic
  fun scale(scale: Vec2f) = canvas?.scale(scale.x, scale.y)

}
