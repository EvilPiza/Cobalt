package org.cobalt.render.skia

import io.github.humbleui.skija.BlendMode
import io.github.humbleui.skija.ClipMode
import io.github.humbleui.skija.ColorFilter
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.SamplingMode
import io.github.humbleui.types.RRect
import io.github.humbleui.types.Rect

/** Utilities for loading and drawing cached Skia images.
 * Images may be rounded and color-masked when drawn.
 */
object SkiaImages {
  private data class ImageCacheKey(
    val identifier: String,
    val radius: Float?,
    val colorMask: Int?,
  )

  private val images = mutableMapOf<ImageCacheKey, SkiaImage>()
  private val canvas get() = SkiaContext.canvas

  /**
   * Load or create a cached image for the given identifier.
   *
   * @param identifier resource identifier for the image
   * @param radius optional corner radius to apply when rendering
   * @param colorMask optional ARGB color mask to blend over the image
   * @return a cached or newly created [SkiaImage]
   */
  @JvmStatic
  fun loadImage(
    identifier: String,
    radius: Float? = null,
    colorMask: Int? = null,
  ): SkiaImage {
    val normalizedRadius = radius?.takeIf { it > 0f }
    val key = ImageCacheKey(identifier, normalizedRadius, colorMask)

    return images.computeIfAbsent(key) {
      SkiaImage(identifier, normalizedRadius, colorMask)
    }
  }

  /**
   * Draw the provided [SkiaImage] into the destination rectangle.
   *
   * @param image the cached image to draw
   * @param x destination x coordinate
   * @param y destination y coordinate
   * @param width destination width in pixels
   * @param height destination height in pixels
   */
  @JvmStatic
  fun image(image: SkiaImage, x: Float, y: Float, width: Float, height: Float) {
    val canvas = this.canvas ?: return
    if (width <= 0 || height <= 0) return

    val sourceImage = image.getOrGenerateRaster(width.toInt(), height.toInt()) ?: return

    Paint().use { paint ->
      image.colorMask?.let {
        paint.colorFilter = ColorFilter.makeBlend(it, BlendMode.SRC_ATOP)
      }

      if (image.radius != null && image.radius > 0f) {
        canvas.save()
        canvas.clipRRect(RRect.makeXYWH(x, y, width, height, image.radius), ClipMode.INTERSECT, true)
      }

      canvas.drawImageRect(
        sourceImage,
        Rect.makeWH(sourceImage.width.toFloat(), sourceImage.height.toFloat()),
        Rect.makeXYWH(x, y, width, height),
        SamplingMode.MITCHELL,
        paint,
        false
      )

      if (image.radius != null && image.radius > 0f) {
        canvas.restore()
      }
    }
  }

}


