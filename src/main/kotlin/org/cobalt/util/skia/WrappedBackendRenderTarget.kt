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

import io.github.humbleui.skija.BackendRenderTarget
import io.github.humbleui.skija.BackendRenderTarget._nMakeGL
import io.github.humbleui.skija.impl.Stats
import org.jetbrains.annotations.Contract

/**
 * [BackendRenderTarget] wrapper that stores the OpenGL framebuffer metadata
 * used to create and track the native render-target pointer.
 *
 * @property width framebuffer width in pixels
 * @property height framebuffer height in pixels
 * @property sampleCnt number of samples used for multisampling
 * @property stencilBits number of stencil bits in the framebuffer
 * @property fbId OpenGL framebuffer object id
 * @property fbFormat OpenGL framebuffer format enum value
 * @param ptr native backend render-target pointer owned by [BackendRenderTarget]
 */
class WrappedBackendRenderTarget(
  val width: Int,
  val height: Int,
  val sampleCnt: Int,
  val stencilBits: Int,
  val fbId: Int,
  val fbFormat: Int,
  ptr: Long,
) : BackendRenderTarget(ptr) {

  companion object {

    /**
     * Create a new [WrappedBackendRenderTarget] backed by an OpenGL framebuffer.
     *
     * The native helper [_nMakeGL] allocates the underlying backend render target.
     * The resulting pointer is then passed to [BackendRenderTarget] through the
     * [WrappedBackendRenderTarget] constructor.
     *
     * @param width framebuffer width in pixels
     * @param height framebuffer height in pixels
     * @param sampleCnt number of samples for multisampling
     * @param stencilBits number of stencil bits in the framebuffer
     * @param fbId OpenGL framebuffer id
     * @param fbFormat OpenGL framebuffer format
     * @return a new [WrappedBackendRenderTarget] instance with native pointer state
     */
    @Contract("_, _, _, _, _, _ -> new")
    internal fun makeGL(
      width: Int, height: Int, sampleCnt: Int, stencilBits: Int, fbId: Int, fbFormat: Int,
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
