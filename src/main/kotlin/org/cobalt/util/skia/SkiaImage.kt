package org.cobalt.util.skia

import io.github.humbleui.skija.Data
import io.github.humbleui.skija.Image
import io.github.humbleui.skija.ImageInfo
import io.github.humbleui.skija.Surface
import io.github.humbleui.skija.svg.SVGDOM
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import kotlinx.coroutines.runBlocking
import org.cobalt.util.WebUtils

/** Represents a lazily-loaded image resource; supports raster images and SVGs with caching and optional rounding/color masks. */
class SkiaImage(identifier: String, val radius: Float? = null, val colorMask: Int? = null) {

  /** True when the identifier points to an SVG resource. */
  val isSvg = identifier.endsWith(".svg", ignoreCase = true)
  /** Deferred Skia Image for raster formats; null for SVG resources. */
  val skiaImage: Image?
  /** Parsed SVG DOM for SVG resources; null for raster images. */
  val svgDom: SVGDOM?

  private var cachedRaster: Image? = null
  private var lastWidth: Int = -1
  private var lastHeight: Int = -1

  init {
    val bytes = getByteArray(identifier)

    if (isSvg) {
      svgDom = Data.makeFromBytes(bytes).use { data -> SVGDOM(data) }
      skiaImage = null
    } else {
      skiaImage = Image.makeDeferredFromEncodedBytes(bytes)
      svgDom = null
    }
  }

  /** Return a raster Image sized to the requested width/height. For SVGs this will generate and cache a raster snapshot. */
  fun getOrGenerateRaster(width: Int, height: Int): Image? {
    if (!isSvg) return skiaImage
    val dom = svgDom ?: return null

    if (cachedRaster != null && width == lastWidth && height == lastHeight) {
      return cachedRaster
    }

    cachedRaster?.close()

    val root = dom.root ?: return null
    val sourceWidth = root.width.value.takeIf { it > 0 } ?: width.toFloat()
    val sourceHeight = root.height.value.takeIf { it > 0 } ?: height.toFloat()

    Surface.makeRaster(ImageInfo.makeN32Premul(width, height)).use { surface ->
      surface.canvas.apply {
        scale(width / sourceWidth, height / sourceHeight)
        dom.render(this)
      }

      cachedRaster = surface.makeImageSnapshot()
    }

    lastWidth = width
    lastHeight = height
    return cachedRaster
  }

  /** Release any native image resources held by this instance. */
  fun delete() {
    skiaImage?.close()
    svgDom?.close()
    cachedRaster?.close()
  }

  companion object {
    private fun getByteArray(path: String): ByteArray {
      val trimmedPath = path.trim()
      return if (trimmedPath.startsWith("http")) runBlocking { WebUtils.getInputStream(trimmedPath).readBytes() }
      else {
        val file = File(trimmedPath)
        if (file.exists() && file.isFile) Files.readAllBytes(file.toPath())
        else this::class.java.getResourceAsStream(trimmedPath)?.readBytes() ?: throw FileNotFoundException(trimmedPath)
      }
    }
  }

}
