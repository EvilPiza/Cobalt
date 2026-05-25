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

  @JvmStatic
  fun drawImage(image: SkiaImage, pos: Vec2f, dim: Dimensions) {
    if (!isValidDimension(dim)) {
      return
    }

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
      image.color?.let { color ->
        paint.colorFilter = ColorFilter.makeBlend(color, BlendMode.MODULATE)
      }

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
    val radius = image.radius
    val roundedRect = if (radius != null && radius > 0f) {
      RRect.makeXYWH(pos.x, pos.y, dim.width, dim.height, radius)
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
