/*
 * This file is part of https://github.com/Lyzev/Skija.
 *
 * Copyright (c) 2025. Lyzev
 *
 * Skija is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at your option) any later version.
 *
 * Skija is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Skija. If not, see <https://www.gnu.org/licenses/>.
 */

package org.cobalt.util.skia

import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.ColorSpace
import io.github.humbleui.skija.ColorType
import io.github.humbleui.skija.DirectContext
import io.github.humbleui.skija.FramebufferFormat
import io.github.humbleui.skija.Surface
import io.github.humbleui.skija.SurfaceOrigin
import org.cobalt.event.EventBus
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.util.skia.gl.States
import org.lwjgl.opengl.GL11

private const val DEFAULT_SAMPLES = 0
private const val DEFAULT_STENCIL_BITS = 8
private const val DEFAULT_PREFER_SAMPLES = 0
private const val CLEAR_R = 0f
private const val CLEAR_G = 0f
private const val CLEAR_B = 0f
private const val CLEAR_A_TRANSPARENT = 0f

internal object SkiaContext {

  private var context: DirectContext? = null
  private var renderTarget: WrappedBackendRenderTarget? = null
  private var surface: Surface? = null

  var canvas: Canvas? = null
    private set

  init {
    EventBus.register(this)
  }

  internal fun initSkia(width: Int, height: Int) {
    ensureContext()
    recreateRenderTarget(width, height)

    canvas = surface?.canvas
  }

  private fun ensureContext() {
    if (context == null) context = DirectContext.makeGL()
  }

  private fun recreateRenderTarget(width: Int, height: Int) {
    surface?.close()
    renderTarget?.close()

    renderTarget = WrappedBackendRenderTarget.makeGL(
      width,
      height,
      DEFAULT_SAMPLES,
      DEFAULT_STENCIL_BITS,
      DEFAULT_PREFER_SAMPLES,
      FramebufferFormat.GR_GL_RGBA8
    )

    surface = Surface.wrapBackendRenderTarget(
      requireNotNull(context),
      requireNotNull(renderTarget),
      SurfaceOrigin.BOTTOM_LEFT,
      ColorType.RGBA_8888,
      ColorSpace.getSRGB()
    )
  }

  internal fun draw() {
    val ctx = context ?: return
    val srf = surface ?: return

    States.push()
    GL11.glDisable(GL11.GL_CULL_FACE)
    GL11.glClearColor(CLEAR_R, CLEAR_G, CLEAR_B, CLEAR_A_TRANSPARENT)

    ctx.resetGLAll()

    val cvs = canvas
    val rt = renderTarget
    if (cvs != null && rt != null) {
      EventBus.post(SkiaDrawEvent(ctx, rt, cvs))
    }

    ctx.flushAndSubmit(srf)

    States.pop()
  }

}
