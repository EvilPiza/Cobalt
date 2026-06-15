package org.cobalt.util.skia

import com.mojang.blaze3d.opengl.GlStateManager
import com.mojang.blaze3d.opengl.GlTexture
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import io.github.humbleui.skija.BackendRenderTarget
import io.github.humbleui.skija.ColorSpace
import io.github.humbleui.skija.ColorType
import io.github.humbleui.skija.DirectContext
import io.github.humbleui.skija.Surface
import io.github.humbleui.skija.SurfaceOrigin
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState
import org.cobalt.Cobalt.minecraft
import org.cobalt.util.skia.gl.States
import org.joml.Matrix3x2f
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL30C

class SkiaPIP(buffer: MultiBufferSource.BufferSource) : PictureInPictureRenderer<SkiaPIP.SkiaRenderState>(buffer) {

  private var renderTarget: BackendRenderTarget? = null
  private var context: DirectContext? = null
  private var surface: Surface? = null

  private var fbo = 0
  private var depthStencil = 0
  private var attachedWidth = 0
  private var attachedHeight = 0
  private var lastTextureId = 0

  override fun getTranslateY(height: Int, guiScale: Int) = height / 2f
  override fun getRenderStateClass() = SkiaRenderState::class.java
  override fun getTextureLabel(): String = "Skia"

  override fun renderToTexture(state: SkiaRenderState, poseStack: PoseStack) {
    val colorView = RenderSystem.outputColorTextureOverride ?: return
    val width = colorView.getWidth(0).takeIf { it > 0 } ?: return
    val height = colorView.getHeight(0).takeIf { it > 0 } ?: return
    val colorTexId = (colorView.texture() as? GlTexture)?.glId() ?: return
    val previousFbo = GL11C.glGetInteger(GL30C.GL_FRAMEBUFFER_BINDING)

    States.push()

    bindTarget(colorTexId, width, height)
    GlStateManager._viewport(0, 0, width, height)

    val directContext = context ?: DirectContext.makeGL().also { context = it }
    directContext.resetGLAll()

    val skijaSurface = surfaceFor(width, height, colorTexId)
    skijaSurface.canvas.clear(0)

    Skia.beginFrame(skijaSurface.canvas)
    Skia.push()
    Skia.transform(state.poseMatrix)

    state.runnable.run()

    Skia.pop()
    Skia.endFrame()

    directContext.flushAndSubmit(skijaSurface, true)

    GlStateManager._glBindFramebuffer(GL30C.GL_FRAMEBUFFER, previousFbo)
    States.pop()
  }

  private fun bindTarget(colorTexId: Int, width: Int, height: Int) {
    if (fbo == 0) {
      fbo = GlStateManager.glGenFramebuffers()
    }

    GlStateManager._glBindFramebuffer(GL30C.GL_FRAMEBUFFER, fbo)
    GlStateManager._glFramebufferTexture2D(
      GL30C.GL_FRAMEBUFFER, GL30C.GL_COLOR_ATTACHMENT0,
      GL11C.GL_TEXTURE_2D, colorTexId, 0
    )

    if (depthStencil == 0 || attachedWidth != width || attachedHeight != height) {
      if (depthStencil != 0) {
        GL30C.glDeleteRenderbuffers(depthStencil)
      }

      depthStencil = GL30C.glGenRenderbuffers()

      GL30C.glBindRenderbuffer(GL30C.GL_RENDERBUFFER, depthStencil)
      GL30C.glRenderbufferStorage(GL30C.GL_RENDERBUFFER, GL30C.GL_DEPTH24_STENCIL8, width, height)
      GL30C.glBindRenderbuffer(GL30C.GL_RENDERBUFFER, 0)
      GL30C.glFramebufferRenderbuffer(
        GL30C.GL_FRAMEBUFFER, GL30C.GL_DEPTH_STENCIL_ATTACHMENT,
        GL30C.GL_RENDERBUFFER, depthStencil
      )

      attachedWidth = width
      attachedHeight = height
    }
  }

  private fun surfaceFor(width: Int, height: Int, textureId: Int): Surface {
    val existing = surface

    if (existing != null && existing.width == width && existing.height == height && lastTextureId == textureId) {
      return existing
    }

    surface?.close()
    renderTarget?.close()

    val directContext = context ?: DirectContext.makeGL().also { context = it }
    val target = BackendRenderTarget.makeGL(width, height, 0, 8, fbo, GL30C.GL_RGBA8)
    val created = Surface.wrapBackendRenderTarget(
      directContext,
      target,
      SurfaceOrigin.BOTTOM_LEFT,
      ColorType.RGBA_8888,
      ColorSpace.getSRGB()
    )

    renderTarget = target
    surface = created
    lastTextureId = textureId

    return created
  }

  override fun close() {
    if (fbo != 0) {
      GlStateManager._glDeleteFramebuffers(fbo)
      fbo = 0
    }

    if (depthStencil != 0) {
      GL30C.glDeleteRenderbuffers(depthStencil)
      depthStencil = 0
    }

    super.close()
  }

  data class SkiaRenderState(
    private val width: Int,
    private val height: Int,
    val poseMatrix: Matrix3x2f,
    private val scissor: ScreenRectangle?,
    private val bounds: ScreenRectangle,
    val runnable: Runnable,
  ) : PictureInPictureRenderState {
    override fun x0() = 0
    override fun y0() = 0
    override fun x1() = width
    override fun y1() = height
    override fun scissorArea() = scissor
    override fun bounds() = bounds
    override fun scale() = 1f
    override fun pose() = poseMatrix
  }

  companion object {

    @JvmStatic
    fun drawSkia(graphics: GuiGraphicsExtractor, runnable: Runnable) {
      val window = minecraft.window
      val width = window.width
      val height = window.height

      val scissor = graphics.scissorStack.peek()
      val pose = Matrix3x2f(graphics.pose())

      val screenRect = ScreenRectangle(0, 0, width, height).transformMaxBounds(pose)

      if (screenRect.width <= 0 || screenRect.height <= 0) {
        return
      }

      val bounds = scissor?.intersection(screenRect) ?: screenRect

      if (bounds.width <= 0 || bounds.height <= 0) {
        return
      }

      val state = SkiaRenderState(
        width, height,
        pose, scissor, bounds, runnable
      )

      graphics.guiRenderState.addPicturesInPictureState(state)
    }

  }

}
