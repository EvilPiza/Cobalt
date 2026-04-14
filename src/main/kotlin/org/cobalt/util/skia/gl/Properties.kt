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

import java.util.*

/**
 * Container for cached OpenGL state used by the renderer.
 *
 * Each property holds the last known value for the corresponding GL state so
 * the library can avoid redundant GL calls when the state is already set.
 */
class Properties {

  /** The last active texture unit (GL_ACTIVE_TEXTURE). Stored as a single-element IntArray. */
  val lastActiveTexture = IntArray(1)

  /** The last linked/used program object (GL_CURRENT_PROGRAM). */
  val lastProgram = IntArray(1)

  /** The last bound texture id. */
  val lastTexture = IntArray(1)

  /** The last bound sampler id. */
  val lastSampler = IntArray(1)

  /** The last bound array buffer id (GL_ARRAY_BUFFER). */
  val lastArrayBuffer = IntArray(1)

  /** The last bound vertex array object id (GL_VERTEX_ARRAY). */
  val lastVertexArrayObject = IntArray(1)

  /** The last polygon rasterization mode(s) (e.g., GL_POLYGON_MODE). Two elements for front and back. */
  val lastPolygonMode = IntArray(2)

  /** The last viewport rectangle (x, y, width, height). */
  val lastViewport = IntArray(4)

  /** The last scissor box rectangle (x, y, width, height). */
  val lastScissorBox = IntArray(4)

  /** The last blend source factor for RGB (GL_BLEND_SRC_RGB). */
  val lastBlendSrcRgb = IntArray(1)

  /** The last blend destination factor for RGB (GL_BLEND_DST_RGB). */
  val lastBlendDstRgb = IntArray(1)

  /** The last blend source factor for alpha (GL_BLEND_SRC_ALPHA). */
  val lastBlendSrcAlpha = IntArray(1)

  /** The last blend destination factor for alpha (GL_BLEND_DST_ALPHA). */
  val lastBlendDstAlpha = IntArray(1)

  /** The last blend equation for RGB (GL_BLEND_EQUATION_RGB). */
  val lastBlendEquationRgb = IntArray(1)

  /** The last blend equation for alpha (GL_BLEND_EQUATION_ALPHA). */
  val lastBlendEquationAlpha = IntArray(1)

  /** The last bound pixel unpack buffer (GL_PIXEL_UNPACK_BUFFER). */
  val lastPixelUnpackBufferBinding = IntArray(1)

  /** The last pixel unpack alignment (GL_UNPACK_ALIGNMENT). */
  val lastUnpackAlignment = IntArray(1)

  /** The last pixel unpack row length (GL_UNPACK_ROW_LENGTH). */
  val lastUnpackRowLength = IntArray(1)

  /** The last pixel unpack skip pixels (GL_UNPACK_SKIP_PIXELS). */
  val lastUnpackSkipPixels = IntArray(1)

  /** The last pixel unpack skip rows (GL_UNPACK_SKIP_ROWS). */
  val lastUnpackSkipRows = IntArray(1)

  /** The last pack swap bytes flag (GL_PACK_SWAP_BYTES). */
  val lastPackSwapBytes = IntArray(1)

  /** The last pack LSB first flag (GL_PACK_LSB_FIRST). */
  val lastPackLsbFirst = IntArray(1)

  /** The last pack row length (GL_PACK_ROW_LENGTH). */
  val lastPackRowLength = IntArray(1)

  /** The last pack image height (GL_PACK_IMAGE_HEIGHT). */
  val lastPackImageHeight = IntArray(1)

  /** The last pack skip pixels (GL_PACK_SKIP_PIXELS). */
  val lastPackSkipPixels = IntArray(1)

  /** The last pack skip rows (GL_PACK_SKIP_ROWS). */
  val lastPackSkipRows = IntArray(1)

  /** The last pack skip images (GL_PACK_SKIP_IMAGES). */
  val lastPackSkipImages = IntArray(1)

  /** The last pack alignment (GL_PACK_ALIGNMENT). */
  val lastPackAlignment = IntArray(1)

  /** The last unpack swap bytes flag (GL_UNPACK_SWAP_BYTES). */
  val lastUnpackSwapBytes = IntArray(1)

  /** The last unpack LSB first flag (GL_UNPACK_LSB_FIRST). */
  val lastUnpackLsbFirst = IntArray(1)

  /** The last unpack image height (GL_UNPACK_IMAGE_HEIGHT). */
  val lastUnpackImageHeight = IntArray(1)

  /** The last unpack skip images (GL_UNPACK_SKIP_IMAGES). */
  val lastUnpackSkipImages = IntArray(1)

  // Internal bitset for boolean GL state flags.
  private val flags = BitSet(7)

  /** Whether blending was last enabled (GL_BLEND). */
  var lastEnableBlend
    get() = flags[0]
    set(value) {
      flags[0] = value
    }

  /** Whether face culling was last enabled (GL_CULL_FACE). */
  var lastEnableCullFace
    get() = flags[1]
    set(value) {
      flags[1] = value
    }

  /** Whether depth testing was last enabled (GL_DEPTH_TEST). */
  var lastEnableDepthTest
    get() = flags[2]
    set(value) {
      flags[2] = value
    }

  /** Whether stencil testing was last enabled (GL_STENCIL_TEST). */
  var lastEnableStencilTest
    get() = flags[3]
    set(value) {
      flags[3] = value
    }

  /** Whether scissor testing was last enabled (GL_SCISSOR_TEST). */
  var lastEnableScissorTest
    get() = flags[4]
    set(value) {
      flags[4] = value
    }

  /** Whether primitive restart was last enabled (GL_PRIMITIVE_RESTART). */
  var lastEnablePrimitiveRestart
    get() = flags[5]
    set(value) {
      flags[5] = value
    }

  /** The last depth mask value (whether depth writes were enabled). */
  var lastDepthMask
    get() = flags[6]
    set(value) {
      flags[6] = value
    }

}
