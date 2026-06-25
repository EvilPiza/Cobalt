package org.cobalt.util.skia.surface

import io.github.humbleui.skija.Canvas

interface SkiaSurface {

  fun render(
    width: Int,
    height: Int,
    rawWidth: Float,
    rawHeight: Float,
    dpr: Float,
    colorTexId: Int,
    clear: Boolean,
    draw: (Canvas) -> Unit
  )

  fun close()

}
