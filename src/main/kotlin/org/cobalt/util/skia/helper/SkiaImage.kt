package org.cobalt.util.skia.helper

import io.github.humbleui.skija.BlendMode
import io.github.humbleui.skija.ColorFilter
import io.github.humbleui.skija.Data
import io.github.humbleui.skija.Image
import io.github.humbleui.skija.ImageInfo
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.Surface
import io.github.humbleui.skija.svg.SVGDOM
import java.awt.Color
import org.cobalt.util.ResourceUtils

class SkiaImage(val location: String) {

  val isSvg: Boolean = location.endsWith(".svg", ignoreCase = true)
  val bytes: ByteArray by lazy {
    ResourceUtils.read(location)
  }

  internal var refCount = 0

  private var image: Image? = null
  private var svgDom: SVGDOM? = null

  private var rasterCache: Image? = null
  private var lastWidth = -1
  private var lastHeight = -1
  private var lastColor: Color? = null

  init {
    if (isSvg) {
      svgDom = SVGDOM(Data.makeFromBytes(bytes))
    } else {
      image = Image.makeDeferredFromEncodedBytes(bytes)
    }
  }

  internal fun resolve(width: Int, height: Int, color: Color?): Image {
    image?.let { return it }

    val dom = svgDom ?: throw IllegalStateException("Image ($location) doesn't exist")

    val needsRebuild = rasterCache == null ||
      width != lastWidth ||
      height != lastHeight ||
      color != lastColor

    if (needsRebuild) {
      rasterCache?.close()

      val root = dom.root
        ?: throw IllegalStateException("Failed to read SVG root: $location")

      val sourceWidth = root.width.value.takeIf { it > 0f } ?: width.toFloat()
      val sourceHeight = root.height.value.takeIf { it > 0f } ?: height.toFloat()

      val surface = Surface.makeRaster(ImageInfo.makeN32Premul(width, height))

      surface.canvas.apply {
        clear(0)
        scale(width / sourceWidth, height / sourceHeight)
        dom.render(this)
      }

      var snapshot = surface.makeImageSnapshot()

      if (color != null) {
        val tinted = Surface.makeRaster(ImageInfo.makeN32Premul(width, height))

        Paint().use { paint ->
          paint.colorFilter = ColorFilter.makeBlend(color.rgb, BlendMode.SRC_IN)
          tinted.canvas.drawImage(snapshot, 0f, 0f, paint)
        }

        snapshot.close()
        snapshot = tinted.makeImageSnapshot()
        tinted.close()
      }

      surface.close()

      rasterCache = snapshot
      lastWidth = width
      lastHeight = height
      lastColor = color
    }

    return rasterCache!!
  }

  internal fun close() {
    image?.close()
    svgDom?.close()
    rasterCache?.close()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is SkiaImage) return false
    return location == other.location
  }

  override fun hashCode(): Int = location.hashCode()

}
