package org.cobalt.render.skia

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
 * Represents a lazily-loaded image resource. Supports both raster images and
 * SVG documents. Raster images are loaded as deferred Skia [Image] objects,
 * while SVGs are parsed into an [SVGDOM] and can be rasterized on demand.
 *
 * @property radius optional corner radius to apply when rendering the image
 * @property colorMask optional ARGB color mask applied when drawing
 */
class SkiaImage(identifier: String, val radius: Float? = null, val colorMask: Int? = null) {

  /** True when the identifier points to an SVG resource (case-insensitive). */
  val isSvg = identifier.endsWith(".svg", ignoreCase = true)

  /**
   * Deferred Skia [Image] used for raster formats (png/jpg/etc.). This will be
   * null for SVG resources.
   */
  val image: Image?

  /**
   * Parsed [SVGDOM] for SVG resources. Null for raster images.
   */
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
   * Return a raster [Image] sized to the requested [width]/[height].
   *
   * For raster inputs this returns the deferred image (no resizing). For SVG
   * inputs this will render the SVG to a raster surface, cache the generated
   * snapshot and return it. Subsequent calls with the same dimensions will
   * return the cached snapshot.
   *
   * @param width desired pixel width of the rasterized image
   * @param height desired pixel height of the rasterized image
   * @return a Skia [Image] at the requested dimensions, or null if the image
   *         could not be loaded or rendered
   */
  fun getOrGenerateRaster(width: Int, height: Int): Image? {
    if (!isSvg) return image
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

  /**
   * Release any native resources (Skia images and DOMs) held by this
   * instance. After calling this method the instance should not be used to
   * produce images.
   */
  fun delete() {
    image?.close()
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
