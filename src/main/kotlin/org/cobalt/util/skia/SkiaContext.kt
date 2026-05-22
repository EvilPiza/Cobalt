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

import io.github.humbleui.skija.*
import org.cobalt.event.EventBus
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.util.skia.gl.States
import org.lwjgl.opengl.GL11

private const val DEFAULT_SAMPLES = 0
private const val DEFAULT_STENCIL_BITS = 8
private const val FRAMEBUFFER_ID = 0
private const val CLEAR_R = 0f
private const val CLEAR_G = 0f
private const val CLEAR_B = 0f
private const val CLEAR_A_TRANSPARENT = 0f

internal object SkiaContext {

  private var context: DirectContext? = null
  private var renderTarget: BackendRenderTarget? = null

  var surface: Surface? = null

  val canvas: Canvas?
    get() = surface?.canvas

  init {
    EventBus.register(this)
  }

  internal fun initSkia(width: Int, height: Int) {
    if (context == null) {
      context = DirectContext.makeGL()
    }

    surface?.close()
    renderTarget?.close()

    renderTarget = BackendRenderTarget.makeGL(
      width,
      height,
      DEFAULT_SAMPLES,
      DEFAULT_STENCIL_BITS,
      FRAMEBUFFER_ID,
      FramebufferFormat.GR_GL_RGBA8
    )

    surface = Surface.wrapBackendRenderTarget(
      requireNotNull(context),
      requireNotNull(renderTarget),
      SurfaceOrigin.BOTTOM_LEFT,
      ColorType.RGBA_8888,
      ColorSpace.getSRGB(),
      null
    )
  }

  internal fun draw() {
    val context = context ?: return
    val surface = surface ?: return

    States.push()
    GL11.glDisable(GL11.GL_CULL_FACE)
    GL11.glClearColor(CLEAR_R, CLEAR_G, CLEAR_B, CLEAR_A_TRANSPARENT)

    context.resetGLAll()

    val canvas = canvas
    val renderTarget = renderTarget

    if (canvas != null && renderTarget != null) {
      EventBus.post(SkiaDrawEvent(context, renderTarget, canvas))
    }

    context.flushAndSubmit(surface)

    States.pop()
  }

}
