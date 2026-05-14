package org.cobalt.event.impl

import io.github.humbleui.skija.BackendRenderTarget
import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.DirectContext
import org.cobalt.event.Event

class SkiaDrawEvent(
  val context: DirectContext,
  val renderTarget: BackendRenderTarget,
  val canvas: Canvas,
) : Event()
