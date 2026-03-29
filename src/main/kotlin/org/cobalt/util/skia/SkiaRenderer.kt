package org.cobalt.util.skia

import com.mojang.blaze3d.opengl.GlConst
import io.github.humbleui.skija.*
import java.io.IOException
import org.lwjgl.opengl.GL11

object SkiaRenderer {

  private val fonts = mutableMapOf<String, Font>()
  private val typefaces = mutableMapOf<String, Typeface>()
  private val textures = mutableMapOf<Int, Image>()

  fun loadFont(resourcePath: String, size: Float = 16f) = fonts.computeIfAbsent("$resourcePath:$size") {
    Font(
      loadTypeface(resourcePath), size
    ).apply {
      isSubpixel = false
      hinting = FontHinting.NORMAL
      edging = FontEdging.ANTI_ALIAS
    }
  }

  private fun loadTypeface(resourcePath: String) = typefaces.computeIfAbsent(resourcePath) {
    val bytes = javaClass.classLoader
      ?.getResourceAsStream(resourcePath)
      ?.use { it.readAllBytes() }
      ?: throw IOException("Font resource not found: $resourcePath")

    val font = FontMgr.getDefault().makeFromData(Data.makeFromBytes(bytes))
      ?: throw IllegalArgumentException("Invalid font data: $resourcePath")

    font
  }

  fun createImage(
    context: DirectContext,
    textureId: Int,
    width: Int,
    height: Int,
    hasAlpha: Boolean = true,
    origin: SurfaceOrigin = SurfaceOrigin.BOTTOM_LEFT,
  ): Image {
    require(width > 0 && height > 0) { "Width and height must be positive" }

    GL11.glBindTexture(GlConst.GL_TEXTURE_2D, textureId)

    return textures.getOrPut(textureId) {
      createTexture(context, textureId, width, height, origin, hasAlpha)
    }.apply {
      if (this.width != width || this.height != height) {
        textures[textureId] = createTexture(context, textureId, width, height, origin, hasAlpha)
      }
    }
  }

  private fun createTexture(
    context: DirectContext,
    textureId: Int,
    width: Int,
    height: Int,
    origin: SurfaceOrigin,
    hasAlpha: Boolean,
  ): Image = Image.adoptGLTextureFrom(
    context,
    textureId,
    GL11.GL_TEXTURE_2D,
    width,
    height,
    GL11.GL_RGBA8,
    origin,
    if (hasAlpha) ColorType.RGBA_8888 else ColorType.RGB_888X,
  )

}
