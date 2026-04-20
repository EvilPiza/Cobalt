package org.cobalt.util.skia

import io.github.humbleui.skija.BlendMode
import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.ClipMode
import io.github.humbleui.skija.ColorFilter
import io.github.humbleui.skija.Image
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.SamplingMode
import io.github.humbleui.types.RRect
import io.github.humbleui.types.Rect
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaContext.canvas

object SkiaImages {

  private val images = mutableMapOf<ImageCacheKey, SkiaImage>()

  /**
   * Loads an image configuration and caches it by identifier, radius, and color mask.
   *
   * Radius values <= 0 are normalized to `null` (no rounded clipping).
   *
   * @param identifier image/resource identifier
   * @param radius optional corner radius for rounded clipping
   * @param colorMask optional ARGB color mask applied during draw
   * @return cached [SkiaImage] instance for the given configuration
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
   * Draws a configured image to the current Skia canvas.
   *
   * Returns early when dimensions are invalid, the canvas is unavailable,
   * or raster generation fails.
   *
   * @param image configured [SkiaImage] to draw
   * @param pos target position in screen space
   */
  @JvmStatic
  fun drawImage(image: SkiaImage, pos: Vec2f, dim: Dimensions) {
    if (!isValidDimension(dim)) return
    val canvas = canvas ?: return

    val sourceImage = image.getOrGenerateRaster(dim.width.toInt(), dim.height.toInt()) ?: return

    drawConfiguredImage(canvas, image, pos, dim, sourceImage)
  }

  private fun drawConfiguredImage(
    canvas: Canvas,
    image: SkiaImage,
    pos: Vec2f,
    dim: Dimensions,
    sourceImage: Image,
  ) {
    Paint().use { paint ->
      configurePaint(paint, image.colorMask)
      drawWithOptionalClip(canvas, image, pos, dim, sourceImage, paint)
    }
  }

  private fun drawWithOptionalClip(
    canvas: Canvas,
    image: SkiaImage,
    pos: Vec2f,
    dim: Dimensions,
    sourceImage: Image,
    paint: Paint,
  ) {
    val roundedRect = if (image.radius != null && image.radius > 0f) {
      RRect.makeXYWH(pos.x, pos.y, dim.width, dim.height, image.radius)
    } else null

    withOptionalClip(canvas, roundedRect) {
      canvas.drawImageRect(
        sourceImage,
        Rect.makeWH(sourceImage.width.toFloat(), sourceImage.height.toFloat()),
        Rect.makeXYWH(pos.x, pos.y, dim.width, dim.height),
        SamplingMode.MITCHELL,
        paint,
        false
      )
    }
  }

  private fun configurePaint(paint: Paint, colorMask: Int?) {
    if (colorMask != null) {
      paint.colorFilter = ColorFilter.makeBlend(colorMask, BlendMode.SRC_ATOP)
    }
  }

  private inline fun withOptionalClip(
    canvas: Canvas,
    roundedRect: RRect?,
    block: () -> Unit,
  ) {
    if (roundedRect != null) {
      canvas.save()
      canvas.clipRRect(roundedRect, ClipMode.INTERSECT, true)
      try {
        block()
      } finally {
        canvas.restore()
      }
    } else {
      block()
    }
  }

  private fun isValidDimension(dim: Dimensions) =
    dim.width > 0 && dim.height > 0

  private data class ImageCacheKey(
    val identifier: String,
    val radius: Float?,
    val colorMask: Int?,
  )

}
