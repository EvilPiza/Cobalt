package org.cobalt.render.skia

import io.github.humbleui.skija.BlendMode
import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.ClipMode
import io.github.humbleui.skija.ColorFilter
import io.github.humbleui.skija.Image
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.SamplingMode
import io.github.humbleui.types.RRect
import io.github.humbleui.types.Rect
import org.cobalt.math.SimpleVec3
import org.cobalt.math.Dimensions

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
   * @param pos destination coordinate
   * @param dim destination dimension
   */
  @JvmStatic
  fun image(image: SkiaImage, pos: SimpleVec3, dim: Dimensions) {
    if (!isValidDimension(dim)) return
    val canvas = this.canvas ?: return

    val sourceImage = image.getOrGenerateRaster(dim.width.toInt(), dim.height.toInt()) ?: return

    drawConfiguredImage(canvas, image, pos, dim, sourceImage)
  }

  private fun drawConfiguredImage(
    canvas: Canvas,
    image: SkiaImage,
    pos: SimpleVec3,
    dim: Dimensions,
    sourceImage: Image
  ) {
    Paint().use { paint ->
      configurePaint(paint, image.colorMask)
      drawWithOptionalClip(canvas, image, pos, dim, sourceImage, paint)
    }
  }

  private fun isValidDimension(dim: Dimensions) = dim.width > 0 && dim.height > 0

  private fun drawWithOptionalClip(
    canvas: Canvas,
    image: SkiaImage,
    pos: SimpleVec3,
    dim: Dimensions,
    sourceImage: Image,
    paint: Paint
  ) {
    val rrect = if (image.radius != null && image.radius > 0f) {
      RRect.makeXYWH(pos.x, pos.y, dim.width, dim.height, image.radius)
    } else null

    withOptionalClip(canvas, rrect) {
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
    canvas: io.github.humbleui.skija.Canvas,
    rrect: RRect?,
    block: () -> Unit,
  ) {
    if (rrect != null) {
      canvas.save()
      canvas.clipRRect(rrect, ClipMode.INTERSECT, true)
      try {
        block()
      } finally {
        canvas.restore()
      }
    } else {
      block()
    }
  }

}
