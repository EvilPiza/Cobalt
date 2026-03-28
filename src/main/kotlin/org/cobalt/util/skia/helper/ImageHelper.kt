package org.cobalt.util.skia.helper

import com.mojang.blaze3d.opengl.GlConst
import io.github.humbleui.skija.ColorType
import io.github.humbleui.skija.DirectContext
import io.github.humbleui.skija.Image
import io.github.humbleui.skija.SurfaceOrigin
import org.lwjgl.opengl.GL11

object ImageHelper {

  private val textures = mutableMapOf<Int, Image>()

  fun get(
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
      create(context, textureId, width, height, origin, hasAlpha)
    }.apply {
      if (this.width != width || this.height != height) {
        textures[textureId] = create(context, textureId, width, height, origin, hasAlpha)
      }
    }
  }

  private fun create(
    context: DirectContext,
    textureId: Int,
    width: Int,
    height: Int,
    origin: SurfaceOrigin,
    hasAlpha: Boolean,
  ) = Image.adoptGLTextureFrom(
    context,
    textureId,
    GL11.GL_TEXTURE_2D,
    width,
    height,
    GL11.GL_RGBA8,
    origin,
    if (hasAlpha) ColorType.RGBA_8888 else ColorType.RGB_888X
  )

}
