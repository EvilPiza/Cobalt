package org.cobalt.event.impl

import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.DirectContext
import org.cobalt.event.Event
import org.cobalt.util.skia.WrappedBackendRenderTarget

/**
 * Custom event fired when Skia performs its draw step for the current frame.
 *
 * This is the callback point where custom rendering can be performed on the
 * Skia canvas before it is submitted.
 *
 * @property context the Skia direct rendering context
 * @property renderTarget the backend render target used for rendering
 * @property canvas the Skia canvas for drawing operations
 */
class SkiaDrawEvent(
  val context: DirectContext,
  val renderTarget: WrappedBackendRenderTarget,
  val canvas: Canvas,
) : Event()
