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

package org.cobalt.util.skia.gl

import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL45.*

private const val GL_VERSION_3_3 = 330
private const val GL_VERSION_3_1 = 310
private const val GL_VERSION_2_0 = 200
private const val GL_VERSION_1_2 = 120

private const val DEFAULT_PIXEL_UNPACK_BINDING = 0
private const val DEFAULT_SAMPLER_UNIT = 0

private const val DEFAULT_UNPACK_ALIGNMENT = 1
private const val DEFAULT_UNPACK_ROW_LENGTH = 0
private const val DEFAULT_UNPACK_SKIP_PIXELS = 0
private const val DEFAULT_UNPACK_SKIP_ROWS = 0
private const val VIEWPORT_X = 0
private const val VIEWPORT_Y = 1
private const val VIEWPORT_W = 2
private const val VIEWPORT_H = 3

private const val SCISSOR_X = 0
private const val SCISSOR_Y = 1
private const val SCISSOR_W = 2
private const val SCISSOR_H = 3

/**
 * Represents a snapshot of relevant OpenGL state that can be pushed and
 * restored. The snapshot reads multiple GL bindings and pixel store
 * parameters so rendering code can change GL state and then restore it to
 * the previous values.
 *
 * @param glVersion computed GL version (major * 100 + minor * 10) used to
 * determine which GL features are available when capturing/restoring state.
 */
class State(private val glVersion: Int) {

  private val props = Properties()

  /**
   * Capture the current GL state into this [State] instance.
   *
   * This method queries a wide set of GL bindings (textures, buffers,
   * vertex arrays), pixel store parameters and enabled/disabled flags and
   * stores them inside the internal [Properties] object. It also resets a
   * handful of pixel store parameters (unpack alignment/row/skip) to safe
   * defaults required by the renderer.
   *
   * @return this [State] instance for convenience.
   */
  fun push(): State {
    with(props) {
      glGetIntegerv(GL_ACTIVE_TEXTURE, lastActiveTexture)
      glActiveTexture(GL_TEXTURE0)
      glGetIntegerv(GL_CURRENT_PROGRAM, lastProgram)
      glGetIntegerv(GL_TEXTURE_BINDING_2D, lastTexture)

      if (glVersion >= GL_VERSION_3_3 || GL.getCapabilities().GL_ARB_sampler_objects) {
        glGetIntegerv(GL_SAMPLER_BINDING, lastSampler)
      }

      glGetIntegerv(GL_ARRAY_BUFFER_BINDING, lastArrayBuffer)
      glGetIntegerv(GL_VERTEX_ARRAY_BINDING, lastVertexArrayObject)

      if (glVersion >= GL_VERSION_2_0) {
        glGetIntegerv(GL_POLYGON_MODE, lastPolygonMode)
      }

      glGetIntegerv(GL_VIEWPORT, lastViewport)
      glGetIntegerv(GL_SCISSOR_BOX, lastScissorBox)
      glGetIntegerv(GL_BLEND_SRC_RGB, lastBlendSrcRgb)
      glGetIntegerv(GL_BLEND_DST_RGB, lastBlendDstRgb)
      glGetIntegerv(GL_BLEND_SRC_ALPHA, lastBlendSrcAlpha)
      glGetIntegerv(GL_BLEND_DST_ALPHA, lastBlendDstAlpha)
      glGetIntegerv(GL_BLEND_EQUATION_RGB, lastBlendEquationRgb)
      glGetIntegerv(GL_BLEND_EQUATION_ALPHA, lastBlendEquationAlpha)

      lastEnableBlend = glIsEnabled(GL_BLEND)
      lastEnableCullFace = glIsEnabled(GL_CULL_FACE)
      lastEnableDepthTest = glIsEnabled(GL_DEPTH_TEST)
      lastEnableStencilTest = glIsEnabled(GL_STENCIL_TEST)
      lastEnableScissorTest = glIsEnabled(GL_SCISSOR_TEST)

      if (glVersion >= GL_VERSION_3_1) {
        lastEnablePrimitiveRestart = glIsEnabled(GL_PRIMITIVE_RESTART)
      }

      lastDepthMask = glGetBoolean(GL_DEPTH_WRITEMASK)

      glGetIntegerv(GL_PIXEL_UNPACK_BUFFER_BINDING, lastPixelUnpackBufferBinding)
      glBindBuffer(GL_PIXEL_UNPACK_BUFFER, DEFAULT_PIXEL_UNPACK_BINDING)

      glGetIntegerv(GL_PACK_SWAP_BYTES, lastPackSwapBytes)
      glGetIntegerv(GL_PACK_LSB_FIRST, lastPackLsbFirst)
      glGetIntegerv(GL_PACK_ROW_LENGTH, lastPackRowLength)
      glGetIntegerv(GL_PACK_SKIP_PIXELS, lastPackSkipPixels)
      glGetIntegerv(GL_PACK_SKIP_ROWS, lastPackSkipRows)
      glGetIntegerv(GL_PACK_ALIGNMENT, lastPackAlignment)

      glGetIntegerv(GL_UNPACK_SWAP_BYTES, lastUnpackSwapBytes)
      glGetIntegerv(GL_UNPACK_LSB_FIRST, lastUnpackLsbFirst)
      glGetIntegerv(GL_UNPACK_ALIGNMENT, lastUnpackAlignment)
      glGetIntegerv(GL_UNPACK_ROW_LENGTH, lastUnpackRowLength)
      glGetIntegerv(GL_UNPACK_SKIP_PIXELS, lastUnpackSkipPixels)
      glGetIntegerv(GL_UNPACK_SKIP_ROWS, lastUnpackSkipRows)

      if (glVersion >= GL_VERSION_1_2) {
        glGetIntegerv(GL_PACK_IMAGE_HEIGHT, lastPackImageHeight)
        glGetIntegerv(GL_PACK_SKIP_IMAGES, lastPackSkipImages)
        glGetIntegerv(GL_UNPACK_IMAGE_HEIGHT, lastUnpackImageHeight)
        glGetIntegerv(GL_UNPACK_SKIP_IMAGES, lastUnpackSkipImages)
      }

      glPixelStorei(GL_UNPACK_ALIGNMENT, DEFAULT_UNPACK_ALIGNMENT)
      glPixelStorei(GL_UNPACK_ROW_LENGTH, DEFAULT_UNPACK_ROW_LENGTH)
      glPixelStorei(GL_UNPACK_SKIP_PIXELS, DEFAULT_UNPACK_SKIP_PIXELS)
      glPixelStorei(GL_UNPACK_SKIP_ROWS, DEFAULT_UNPACK_SKIP_ROWS)
    }

    return this
  }

  /**
   * Restore GL state previously captured by [push].
   *
   * The stored values are applied back to the GL context, including bound
   * program, textures, samplers, vertex arrays, pixel store parameters and
   * enabled/disabled capabilities. The method returns this [State]
   * instance for chaining if desired.
   *
   * @return this [State] instance after restoration.
   */
  fun pop(): State {
    with(props) {
      glUseProgram(lastProgram[0])
      glBindTexture(GL_TEXTURE_2D, lastTexture[0])

      if (glVersion >= GL_VERSION_3_3 || GL.getCapabilities().GL_ARB_sampler_objects) {
        glBindSampler(DEFAULT_SAMPLER_UNIT, lastSampler[0])
      }

      glActiveTexture(lastActiveTexture[0])
      glBindVertexArray(lastVertexArrayObject[0])
      glBindBuffer(GL_ARRAY_BUFFER, lastArrayBuffer[0])
      glBlendEquationSeparate(lastBlendEquationRgb[0], lastBlendEquationAlpha[0])
      glBlendFuncSeparate(
        lastBlendSrcRgb[0],
        lastBlendDstRgb[0],
        lastBlendSrcAlpha[0],
        lastBlendDstAlpha[0]
      )

      if (lastEnableBlend) glEnable(GL_BLEND)
      else glDisable(GL_BLEND)
      if (lastEnableCullFace) glEnable(GL_CULL_FACE)
      else glDisable(GL_CULL_FACE)
      if (lastEnableDepthTest) glEnable(GL_DEPTH_TEST)
      else glDisable(GL_DEPTH_TEST)
      if (lastEnableStencilTest) glEnable(GL_STENCIL_TEST)
      else glDisable(GL_STENCIL_TEST)
      if (lastEnableScissorTest) glEnable(GL_SCISSOR_TEST)
      else glDisable(GL_SCISSOR_TEST)

      if (glVersion >= GL_VERSION_3_1) {
        if (lastEnablePrimitiveRestart) glEnable(GL_PRIMITIVE_RESTART)
        else glDisable(GL_PRIMITIVE_RESTART)
      }

      if (glVersion >= GL_VERSION_2_0) {
        glPolygonMode(GL_FRONT_AND_BACK, lastPolygonMode[0])
      }

      glViewport(
        lastViewport[VIEWPORT_X],
        lastViewport[VIEWPORT_Y],
        lastViewport[VIEWPORT_W],
        lastViewport[VIEWPORT_H]
      )
      glScissor(
        lastScissorBox[SCISSOR_X],
        lastScissorBox[SCISSOR_Y],
        lastScissorBox[SCISSOR_W],
        lastScissorBox[SCISSOR_H]
      )

      glPixelStorei(GL_PACK_SWAP_BYTES, lastPackSwapBytes[0])
      glPixelStorei(GL_PACK_LSB_FIRST, lastPackLsbFirst[0])
      glPixelStorei(GL_PACK_ROW_LENGTH, lastPackRowLength[0])
      glPixelStorei(GL_PACK_SKIP_PIXELS, lastPackSkipPixels[0])
      glPixelStorei(GL_PACK_SKIP_ROWS, lastPackSkipRows[0])
      glPixelStorei(GL_PACK_ALIGNMENT, lastPackAlignment[0])

      glBindBuffer(GL_PIXEL_UNPACK_BUFFER, lastPixelUnpackBufferBinding[0])
      glPixelStorei(GL_UNPACK_SWAP_BYTES, lastUnpackSwapBytes[0])
      glPixelStorei(GL_UNPACK_LSB_FIRST, lastUnpackLsbFirst[0])
      glPixelStorei(GL_UNPACK_ALIGNMENT, lastUnpackAlignment[0])
      glPixelStorei(GL_UNPACK_ROW_LENGTH, lastUnpackRowLength[0])
      glPixelStorei(GL_UNPACK_SKIP_PIXELS, lastUnpackSkipPixels[0])
      glPixelStorei(GL_UNPACK_SKIP_ROWS, lastUnpackSkipRows[0])

      if (glVersion >= GL_VERSION_1_2) {
        glPixelStorei(GL_PACK_IMAGE_HEIGHT, lastPackImageHeight[0])
        glPixelStorei(GL_PACK_SKIP_IMAGES, lastPackSkipImages[0])
        glPixelStorei(GL_UNPACK_IMAGE_HEIGHT, lastUnpackImageHeight[0])
        glPixelStorei(GL_UNPACK_SKIP_IMAGES, lastUnpackSkipImages[0])
      }

      glDepthMask(lastDepthMask)
    }

    return this
  }

}
