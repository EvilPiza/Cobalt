package org.cobalt.util.skia

import com.mojang.blaze3d.opengl.GlTexture
import org.cobalt.Cobalt.minecraft
import org.cobalt.util.skia.surface.GlSurface
import org.cobalt.util.skia.surface.SkiaSurface

internal object SkiaCompositor {

  @JvmStatic
  val glSurface: SkiaSurface = GlSurface()

  @JvmStatic
  fun composite() {
    if (!Skia.hasBatch()) {
      return
    }

    val window = minecraft.window
    val mainRenderTarget = minecraft.gameRenderer.mainRenderTarget()

    val width = mainRenderTarget.width.takeIf { it > 0 } ?: return
    val height = mainRenderTarget.height.takeIf { it > 0 } ?: return
    val colorTexId = (mainRenderTarget.colorTexture as? GlTexture)?.glId() ?: return

    val rawWidth = width.toFloat()
    val rawHeight = height.toFloat()
    val guiWidth = window.guiScaledWidth.toFloat().coerceAtLeast(1f)
    val dpr = (rawWidth / guiWidth).takeIf { it.isFinite() && it > 0f } ?: 1f

    glSurface.render(width, height, rawWidth, rawHeight, dpr, colorTexId, clear = false) {
      Skia.flush()
    }
  }

}
