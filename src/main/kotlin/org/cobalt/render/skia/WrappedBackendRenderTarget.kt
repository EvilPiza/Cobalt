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

package org.cobalt.render.skia

import io.github.humbleui.skija.BackendRenderTarget
import io.github.humbleui.skija.impl.Stats
import org.jetbrains.annotations.Contract

/**
 * A thin wrapper around Skija's [BackendRenderTarget] that carries additional
 * information about the GL framebuffer used to back the render target.
 *
 * @property width framebuffer width in pixels
 * @property height framebuffer height in pixels
 * @property sampleCnt number of samples for multisampling
 * @property stencilBits number of stencil bits in the framebuffer
 * @property fbId OpenGL framebuffer id
 * @property fbFormat OpenGL framebuffer format
 * @param ptr native pointer passed to the Skija BackendRenderTarget base
 */
class WrappedBackendRenderTarget(
  val width: Int,
  val height: Int,
  val sampleCnt: Int,
  val stencilBits: Int,
  val fbId: Int,
  val fbFormat: Int,
  ptr: Long
) : BackendRenderTarget(ptr) {

  companion object {

    @Contract("_, _, _, _, _, _ -> new")
    /**
     * Create a new [WrappedBackendRenderTarget] backed by an OpenGL framebuffer.
     *
     * The native helper [_nMakeGL] is invoked to allocate the platform-specific
     * backend render target and its pointer is stored in the created wrapper.
     *
     * @param width framebuffer width in pixels
     * @param height framebuffer height in pixels
     * @param sampleCnt number of samples for multisampling
     * @param stencilBits number of stencil bits in the framebuffer
     * @param fbId OpenGL framebuffer id
     * @param fbFormat OpenGL framebuffer format
     */
    fun makeGL(
      width: Int, height: Int, sampleCnt: Int, stencilBits: Int, fbId: Int, fbFormat: Int
    ): WrappedBackendRenderTarget {
      Stats.onNativeCall()
      return WrappedBackendRenderTarget(
        width,
        height,
        sampleCnt,
        stencilBits,
        fbId,
        fbFormat,
        _nMakeGL(width, height, sampleCnt, stencilBits, fbId, fbFormat)
      )
    }

  }

}
