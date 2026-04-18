package org.cobalt.render.skia

import io.github.humbleui.skija.Canvas
import org.cobalt.Cobalt.minecraft

 /** High-level Skia drawing helpers used by UI and module renderers.
 * Provides convenience functions for text, shapes, images and scissor management.
 */
object SkiaRenderer {
  private const val BASE_WIDTH = 1920f
  private const val BASE_HEIGHT = 1080f

  private val canvas: Canvas?
    get() = SkiaContext.canvas

  /** Calculate a window scale factor relative to a 1920x1080 baseline for consistent UI sizing. */
  fun getWindowScale(): Float {
    val windowWidth = minecraft.window.width.toFloat()
    val windowHeight = minecraft.window.height.toFloat()

    return minOf(windowWidth / BASE_WIDTH, windowHeight / BASE_HEIGHT)
  }

  /** Save the current Skia canvas state. */
  @JvmStatic
  fun save() = this.canvas?.save()

  /** Restore the previously saved Skia canvas state. */
  @JvmStatic
  fun restore() = this.canvas?.restore()

  /** Translate the canvas by the given x/y offset. */
  @JvmStatic
  fun translate(x: Float, y: Float) = this.canvas?.translate(x, y)

  /** Scale the canvas by the specified X and Y factors. */
  @JvmStatic
  fun scale(x: Float, y: Float) = this.canvas?.scale(x, y)

}
