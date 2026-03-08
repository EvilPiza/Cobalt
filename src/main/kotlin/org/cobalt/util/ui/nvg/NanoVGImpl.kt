/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2023-2025, odtheking
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Portions of this file are derived from OdinFabric
 * Copyright (c) odtheking
 * Licensed under BSD-3-Clause
 *
 * Modifications and additions:
 * Licensed under GPL-3.0
 */

package org.cobalt.util.ui.nvg

import com.mojang.blaze3d.opengl.GlDevice
import com.mojang.blaze3d.opengl.GlStateManager
import com.mojang.blaze3d.opengl.GlTexture
import com.mojang.blaze3d.systems.RenderSystem
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.min
import net.minecraft.client.Minecraft
import org.cobalt.util.ColorUtils.alpha
import org.cobalt.util.ColorUtils.blue
import org.cobalt.util.ColorUtils.green
import org.cobalt.util.ColorUtils.red
import org.cobalt.util.ui.Renderer
import org.cobalt.util.ui.style.Font
import org.cobalt.util.ui.style.Gradient
import org.cobalt.util.ui.style.Image
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NVGPaint
import org.lwjgl.nanovg.NanoSVG.*
import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL3.*
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL33C
import org.lwjgl.stb.STBImage.stbi_load_from_memory
import org.lwjgl.system.MemoryUtil.memAlloc
import org.lwjgl.system.MemoryUtil.memFree

internal object NanoVGImpl : Renderer {

  private val mc: Minecraft =
    Minecraft.getInstance()

  private val nvgPaint = NVGPaint.malloc()
  private val nvgColor = NVGColor.malloc()
  private val nvgColor2 = NVGColor.malloc()

  private val fontMap = HashMap<Font, NVGFont>()
  private val fontBounds = FloatArray(4)

  private val images = HashMap<Image, NVGImage>()

  private var scissor: Scissor? = null
  private var drawing: Boolean = false
  private var vg = -1L

  @JvmField
  var prevActiveTexture = -1

  @JvmField
  var prevBoundTexture = -1

  @JvmField
  var prevProgram = -1

  override fun init() {
    vg = nvgCreate(NVG_ANTIALIAS or NVG_STENCIL_STROKES)
    require(vg != -1L) { "Failed to initialize NanoVG" }
  }

  override fun render(screenWidth: Float, screenHeight: Float, block: () -> Unit) {
    beginFrame(screenWidth, screenHeight)
    block()
    endFrame()
  }

  private fun beginFrame(screenWidth: Float, screenHeight: Float) {
    check(!drawing) { "[NVGRenderer] Already drawing, but called beginFrame" }

    prevActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE)
    prevProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM)

    val framebuffer = mc.mainRenderTarget
    val glFramebuffer = (framebuffer.colorTexture as GlTexture).getFbo(
      (RenderSystem.getDevice() as GlDevice).directStateAccess(),
      null
    )

    GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, glFramebuffer)
    GlStateManager._viewport(0, 0, framebuffer.width, framebuffer.height)
    GlStateManager._activeTexture(GL30.GL_TEXTURE0)

    nvgBeginFrame(vg, screenWidth, screenHeight, 1f)
    nvgTextAlign(vg, NVG_ALIGN_LEFT or NVG_ALIGN_TOP)
    drawing = true
  }

  private fun endFrame() {
    check(drawing) { "[NVGRenderer] Not drawing, but called endFrame" }

    nvgEndFrame(vg)
    GlStateManager._disableCull()
    GlStateManager._disableDepthTest()
    GlStateManager._enableBlend()
    GlStateManager._blendFuncSeparate(770, 771, 1, 0)
    GlStateManager._glUseProgram(0)

    if (prevProgram != -1) GlStateManager._glUseProgram(prevProgram)

    if (prevActiveTexture != -1) {
      GlStateManager._activeTexture(prevActiveTexture)
      if (prevBoundTexture != -1) GlStateManager._bindTexture(prevBoundTexture)
    }

    GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
    drawing = false
  }

  override fun push() {
    nvgSave(vg)
  }

  override fun pop() {
    nvgRestore(vg)
  }

  override fun scale(x: Float, y: Float) {
    nvgScale(vg, x, y)
  }

  override fun translate(x: Float, y: Float) {
    nvgTranslate(vg, x, y)
  }

  override fun rotate(amount: Float) {
    nvgRotate(vg, amount)
  }

  override fun globalAlpha(amount: Float) {
    nvgGlobalAlpha(vg, amount.coerceIn(0f, 1f))
  }

  override fun pushScissor(x: Float, y: Float, width: Float, height: Float) {
    scissor = Scissor(scissor, x, y, width + x, height + y)
    scissor?.applyScissor()
  }

  override fun popScissor() {
    nvgResetScissor(vg)
    scissor = scissor?.previous
    scissor?.applyScissor()
  }

  override fun text(font: Font, text: String, x: Float, y: Float, size: Float, color: Int) {
    nvgFontSize(vg, size)
    nvgFontFaceId(vg, getFontID(font))
    color(color)
    nvgFillColor(vg, nvgColor)
    nvgText(vg, x, y + .5f, text)
  }

  override fun textBounds(font: Font, text: String, size: Float): Pair<Float, Float> {
    nvgFontSize(vg, size)
    nvgFontFaceId(vg, getFontID(font))
    nvgTextBounds(vg, 0f, 0f, text, fontBounds)

    val width = fontBounds[2] - fontBounds[0]
    val height = fontBounds[3] - fontBounds[1]

    return width to height
  }

  override fun wrappedText(
    font: Font,
    text: String,
    x: Float,
    y: Float,
    wrappingWidth: Float,
    size: Float,
    color: Int,
    lineHeight: Float,
  ) {
    nvgFontSize(vg, size)
    nvgFontFaceId(vg, getFontID(font))
    nvgTextLineHeight(vg, lineHeight)
    color(color)
    nvgFillColor(vg, nvgColor)
    nvgTextBox(vg, x, y, wrappingWidth, text)
  }

  override fun wrappedTextBounds(
    font: Font,
    text: String,
    wrappingWidth: Float,
    size: Float,
    lineHeight: Float,
  ): FloatArray {
    val bounds = FloatArray(4)

    nvgFontSize(vg, size)
    nvgFontFaceId(vg, getFontID(font))
    nvgTextLineHeight(vg, lineHeight)
    nvgTextBoxBounds(vg, 0f, 0f, wrappingWidth, text, bounds)

    return bounds
  }

  override fun line(x1: Float, y1: Float, x2: Float, y2: Float, thickness: Float, color: Int) {
    nvgBeginPath(vg)
    nvgMoveTo(vg, x1, y1)
    nvgLineTo(vg, x2, y2)
    nvgStrokeWidth(vg, thickness)
    color(color)
    nvgStrokeColor(vg, nvgColor)
    nvgStroke(vg)
  }

  override fun circle(x: Float, y: Float, radius: Float, color: Int) {
    nvgBeginPath(vg)
    nvgCircle(vg, x, y, radius)
    color(color)
    nvgFillColor(vg, nvgColor)
    nvgFill(vg)
  }

  override fun rect(x: Float, y: Float, width: Float, height: Float, color: Int, radius: Float?) {
    nvgBeginPath(vg)

    if (radius == null) {
      nvgRect(vg, x, y, width, height + .5f)
    } else {
      nvgRoundedRect(vg, x, y, width, height + .5f, radius)
    }

    color(color)
    nvgFillColor(vg, nvgColor)
    nvgFill(vg)
  }

  override fun gradientRect(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    color1: Int,
    color2: Int,
    gradient: Gradient,
    radius: Float?,
  ) {
    nvgBeginPath(vg)

    if (radius == null) {
      nvgRect(vg, x, y, width, height)
    } else {
      nvgRoundedRect(vg, x, y, width, height, radius)
    }

    gradient(color1, color2, x, y, width, height, gradient)
    nvgFillPaint(vg, nvgPaint)
    nvgFill(vg)
  }

  override fun halfRoundedRect(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    color: Int,
    radius: Float,
    roundTop: Boolean,
  ) {
    nvgBeginPath(vg)

    if (roundTop) {
      nvgMoveTo(vg, x, y + height)
      nvgLineTo(vg, x + width, y + height)
      nvgLineTo(vg, x + width, y + radius)
      nvgArcTo(vg, x + width, y, x + width - radius, y, radius)
      nvgLineTo(vg, x + radius, y)
      nvgArcTo(vg, x, y, x, y + radius, radius)
      nvgLineTo(vg, x, y + height)
    } else {
      nvgMoveTo(vg, x, y)
      nvgLineTo(vg, x + width, y)
      nvgLineTo(vg, x + width, y + height - radius)
      nvgArcTo(vg, x + width, y + height, x + width - radius, y + height, radius)
      nvgLineTo(vg, x + radius, y + height)
      nvgArcTo(vg, x, y + height, x, y + height - radius, radius)
      nvgLineTo(vg, x, y)
    }

    nvgClosePath(vg)
    color(color)
    nvgFillColor(vg, nvgColor)
    nvgFill(vg)
  }

  override fun hollowRect(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    thickness: Float,
    color: Int,
    radius: Float?,
  ) {
    nvgBeginPath(vg)

    if (radius == null) {
      nvgRect(vg, x, y, width, height)
    } else {
      nvgRoundedRect(vg, x, y, width, height, radius)
    }

    nvgStrokeWidth(vg, thickness)
    nvgPathWinding(vg, NVG_HOLE)
    color(color)
    nvgStrokeColor(vg, nvgColor)
    nvgStroke(vg)
  }

  override fun hollowGradientRect(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    thickness: Float,
    color1: Int,
    color2: Int,
    gradient: Gradient,
    radius: Float?,
  ) {
    nvgBeginPath(vg)

    if (radius == null) {
      nvgRect(vg, x, y, width, height)
    } else {
      nvgRoundedRect(vg, x, y, width, height, radius)
    }

    nvgStrokeWidth(vg, thickness)
    gradient(color1, color2, x, y, width, height, gradient)
    nvgStrokePaint(vg, nvgPaint)
    nvgStroke(vg)
  }

  override fun createImage(resourcePath: String): Image {
    val image = images.keys.find { it.identifier == resourcePath } ?: Image(resourcePath)
    if (image.isSVG) images.getOrPut(image) { NVGImage(0, loadSVG(image)) }.count++
    else images.getOrPut(image) { NVGImage(0, loadImage(image)) }.count++
    return image
  }

  override fun image(image: Image, x: Float, y: Float, width: Float, height: Float, radius: Float?, colorMask: Int?) {
    nvgImagePattern(vg, x, y, width, height, 0f, getImage(image), 1f, nvgPaint)

    if (colorMask != null) {
      nvgRGBA(
        colorMask.red.toByte(),
        colorMask.green.toByte(),
        colorMask.blue.toByte(),
        colorMask.alpha.toByte(),
        nvgPaint.innerColor()
      )
    }

    nvgBeginPath(vg)

    if (radius == null) {
      nvgRect(vg, x, y, width, height + .5f)
    } else {
      nvgRoundedRect(vg, x, y, width, height + .5f, radius)
    }

    nvgFillPaint(vg, nvgPaint)
    nvgFill(vg)
  }

  override fun deleteImage(image: Image) {
    val nvgImage = images[image] ?: return
    nvgImage.count--

    if (nvgImage.count == 0) {
      nvgDeleteImage(vg, nvgImage.nvg)
      images.remove(image)
    }
  }

  override fun color(color: Int) {
    nvgRGBA(
      color.red.toByte(),
      color.green.toByte(),
      color.blue.toByte(),
      color.alpha.toByte(),
      nvgColor
    )
  }

  override fun color(color1: Int, color2: Int) {
    nvgRGBA(
      color1.red.toByte(),
      color1.green.toByte(),
      color1.blue.toByte(),
      color1.alpha.toByte(),
      nvgColor
    )

    nvgRGBA(
      color2.red.toByte(),
      color2.green.toByte(),
      color2.blue.toByte(),
      color2.alpha.toByte(),
      nvgColor2
    )
  }

  override fun gradient(
    color1: Int,
    color2: Int,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    direction: Gradient,
  ) {
    color(color1, color2)

    when (direction) {
      Gradient.TOP_TO_BOTTOM -> nvgLinearGradient(vg, x, y, x, y + height, nvgColor, nvgColor2, nvgPaint)
      Gradient.LEFT_TO_RIGHT -> nvgLinearGradient(vg, x, y, x + width, y, nvgColor, nvgColor2, nvgPaint)
    }
  }

  private fun getImage(image: Image): Int {
    return images[image]?.nvg ?: throw IllegalStateException("Image (${image.identifier}) doesn't exist")
  }

  private fun loadImage(image: Image): Int {
    val width = IntArray(1)
    val height = IntArray(1)
    val channels = IntArray(1)
    val buffer = stbi_load_from_memory(
      image.buffer(), width, height, channels, 4
    ) ?: throw NullPointerException("Failed to load image: ${image.identifier}")

    return nvgCreateImageRGBA(vg, width[0], height[0], 0, buffer)
  }

  private fun loadSVG(image: Image): Int {
    val vec = image.stream.use { it.bufferedReader().readText() }
    val svg = nsvgParse(vec, "px", 96f) ?: throw IllegalStateException("Failed to parse ${image.identifier}")

    val width = svg.width().toInt()
    val height = svg.height().toInt()
    val buffer = memAlloc(width * height * 4)

    try {
      val rasterizer = nsvgCreateRasterizer()
      nsvgRasterize(rasterizer, svg, 0f, 0f, 1f, buffer, width, height, width * 4)

      val nvgImage = nvgCreateImageRGBA(vg, width, height, 0, buffer)
      nsvgDeleteRasterizer(rasterizer)

      return nvgImage
    } finally {
      nsvgDelete(svg)
      memFree(buffer)
    }
  }

  private fun getFontID(font: Font): Int {
    return fontMap.getOrPut(font) {
      val buffer = font.buffer()
      NVGFont(nvgCreateFontMem(vg, font.name, buffer, false), buffer)
    }.id
  }

  private class Scissor(val previous: Scissor?, val x: Float, val y: Float, val maxX: Float, val maxY: Float) {
    fun applyScissor() {
      if (previous == null) {
        nvgScissor(vg, x, y, maxX - x, maxY - y)
      } else {
        val x = max(x, previous.x)
        val y = max(y, previous.y)
        val width = max(0f, (min(maxX, previous.maxX) - x))
        val height = max(0f, (min(maxY, previous.maxY) - y))
        nvgScissor(vg, x, y, width, height)
      }
    }
  }

  private data class NVGImage(var count: Int, val nvg: Int)
  private data class NVGFont(val id: Int, val buffer: ByteBuffer)

}
