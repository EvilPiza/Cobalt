package org.cobalt.event.impl

import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.DirectContext
import org.cobalt.event.Event
import org.cobalt.util.skia.WrappedBackendRenderTarget

class SkiaDrawEvent(
  val context: DirectContext,
  val renderTarget: WrappedBackendRenderTarget,
  val canvas: Canvas
) : Event()
