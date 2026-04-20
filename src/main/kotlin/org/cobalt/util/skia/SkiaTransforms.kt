package org.cobalt.util.skia

import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaContext.canvas

object SkiaTransforms {

  /**
   * Saves the current canvas state.
   *
   * No-op when no canvas is available.
   */
  @JvmStatic
  fun save() = canvas?.save()

  /**
   * Restores the most recently saved canvas state.
   *
   * No-op when no canvas is available.
   */
  @JvmStatic
  fun restore() = canvas?.restore()

  /**
   * Translates the canvas by the given position offset.
   *
   * No-op when no canvas is available.
   *
   * @param pos translation offset on X/Y axes
   */
  @JvmStatic
  fun translate(pos: Vec2f) = canvas?.translate(pos.x, pos.y)

  /**
   * Scales the canvas by the given X/Y factors.
   *
   * No-op when no canvas is available.
   *
   * @param scale scale factors on X/Y axes
   */
  @JvmStatic
  fun scale(scale: Vec2f) = canvas?.scale(scale.x, scale.y)

}
