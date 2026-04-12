package org.cobalt.event.impl

import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.DirectContext
import org.cobalt.event.Event
import org.cobalt.util.skia.WrappedBackendRenderTarget

/** Event fired when Skia drawing is performed for a render pass. */
class SkiaDrawEvent(
  /** The Skia DirectContext used for GPU operations. */
  val context: DirectContext,
  /** The wrapped backend render target representing the surface being rendered to. */
  val renderTarget: WrappedBackendRenderTarget,
  /** The Skia Canvas used to issue draw commands. */
  val canvas: Canvas
) : Event()
