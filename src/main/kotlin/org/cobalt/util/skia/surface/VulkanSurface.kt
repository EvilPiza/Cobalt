package org.cobalt.util.skia.surface

import io.github.humbleui.skija.Canvas

class VulkanSurface : SkiaSurface {

  override fun render(
    width: Int,
    height: Int,
    rawWidth: Float,
    rawHeight: Float,
    dpr: Float,
    colorTexId: Int,
    clear: Boolean,
    draw: (Canvas) -> Unit,
  ) {
    TODO("Not yet implemented")
  }

  override fun close() {
    TODO("Not yet implemented")
  }

}
