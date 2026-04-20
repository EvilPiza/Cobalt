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

/**
 * Image wrapper used by [SkiaImages].
 *
 * @property isSvg whether the image source is an SVG file
 * @property image raster image instance (null if SVG)
 * @property svgDom parsed SVG document (null if raster image)
 *
 * @param identifier path or URL to the image resource
 * @property radius optional corner radius for rendering
 * @property colorMask optional ARGB color mask applied to the image
 *
 * @see SkiaImages
 */
class SkiaImage(identifier: String, val radius: Float? = null, val colorMask: Int? = null) {

  val isSvg = identifier.endsWith(".svg", ignoreCase = true)
  val image: Image?
  val svgDom: SVGDOM?

  private var cachedRaster: Image? = null
  private var lastWidth: Int = -1
  private var lastHeight: Int = -1

  init {
    val bytes = getByteArray(identifier)

    if (isSvg) {
      svgDom = Data.makeFromBytes(bytes).use { data -> SVGDOM(data) }
      image = null
    } else {
      image = Image.makeDeferredFromEncodedBytes(bytes)
      svgDom = null
    }
  }

  /**
   * Returns a rasterized image for the given size.
   *
   * For SVG sources, the image is generated and cached per size.
   *
   * @param width target width
   * @param height target height
   * @return raster image or null if unavailable
   */
  fun getOrGenerateRaster(width: Int, height: Int): Image? {
    if (!isSvg) return image
    val dom = svgDom ?: return null

    if (!isCachedMatch(width, height)) {
      cachedRaster?.close()
      val generated = generateRaster(dom, width, height)
      if (generated != null) {
        cachedRaster = generated
        lastWidth = width
        lastHeight = height
      }
    }

    return cachedRaster
  }

  /**
   * Releases all underlying Skia resources.
   */
  fun delete() {
    image?.close()
    svgDom?.close()
    cachedRaster?.close()
  }

  private fun isCachedMatch(width: Int, height: Int): Boolean {
    return cachedRaster != null && width == lastWidth && height == lastHeight
  }

  private fun generateRaster(dom: SVGDOM, width: Int, height: Int): Image? {
    val root = dom.root ?: return null
    val sourceWidth = root.width.value.takeIf { it > 0 } ?: width.toFloat()
    val sourceHeight = root.height.value.takeIf { it > 0 } ?: height.toFloat()

    return renderDomToSurface(dom, width, height, sourceWidth, sourceHeight)
  }

  private fun renderDomToSurface(
    dom: SVGDOM,
    width: Int,
    height: Int,
    sourceWidth: Float,
    sourceHeight: Float,
  ): Image? {
    var snapshot: Image? = null
    Surface.makeRaster(ImageInfo.makeN32Premul(width, height)).use { surface ->
      surface.canvas.apply {
        scale(width / sourceWidth, height / sourceHeight)
        dom.render(this)
      }

      snapshot = surface.makeImageSnapshot()
    }

    return snapshot
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
