package org.cobalt.util

import com.mojang.blaze3d.systems.RenderSystem
import java.awt.Color
import kotlin.math.max
import kotlin.math.min
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.ShapeRenderer
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.cobalt.event.impl.RenderContext
import org.cobalt.util.render.RenderLayers
import org.joml.Vector3f

object RenderUtils {

  private val mc: Minecraft =
    Minecraft.getInstance()

  @JvmStatic
  fun drawBlockPos(
    context: RenderContext,
    pos: BlockPos,
    color: Color,
    esp: Boolean = false,
  ) {
    val box = AABB(
      pos.x.toDouble(),
      pos.y.toDouble(),
      pos.z.toDouble(),
      pos.x + 1.0,
      pos.y + 1.0,
      pos.z + 1.0
    )

    drawBox(context, box, color, esp)
  }

  @JvmStatic
  fun drawTracer(
    context: RenderContext,
    to: Vec3,
    color: Color,
    esp: Boolean = true,
    thickness: Float = 1.5f,
  ) {
    context.camera.let {
      val cameraPos = it.position
      val from = cameraPos.add(Vec3.directionFromRotation(it.xRot, it.yRot))

      drawLine(context, from, to, color, esp, thickness)
    }
  }

  @JvmStatic
  fun drawBox(context: RenderContext, box: AABB, color: Color, esp: Boolean = false) {
    if (color.alpha == 0) {
      return
    }

    if (!FrustumUtils.isVisible(context.frustum, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)) {
      return
    }

    val matrix = context.matrixStack ?: return
    val bufferSource = context.consumers as? MultiBufferSource.BufferSource ?: return

    val r = color.red / 255f
    val g = color.green / 255f
    val b = color.blue / 255f

    val fillLayer = if (esp) RenderLayers.TRIANGLE_STRIP_ESP else RenderLayers.TRIANGLE_STRIP
    val lineLayer = if (esp) RenderLayers.LINE_LIST_ESP else RenderLayers.LINE_LIST

    matrix.pushPose()
    with(context.camera.position) { matrix.translate(-x, -y, -z) }

    ShapeRenderer.addChainedFilledBoxVertices(
      matrix,
      bufferSource.getBuffer(fillLayer),
      box.minX, box.minY, box.minZ,
      box.maxX, box.maxY, box.maxZ,
      r, g, b, 150 / 255F
    )

    ShapeRenderer.renderLineBox(
      matrix.last(),
      bufferSource.getBuffer(lineLayer),
      box.minX, box.minY, box.minZ,
      box.maxX, box.maxY, box.maxZ,
      r, g, b, 1f
    )

    matrix.popPose()
    bufferSource.endBatch(fillLayer)
    bufferSource.endBatch(lineLayer)
  }

  @JvmStatic
  fun drawLine(
    context: RenderContext,
    start: Vec3,
    end: Vec3,
    color: Color,
    esp: Boolean = false,
    thickness: Float = 1f,
  ) {
    if (color.alpha == 0) {
      return
    }

    if (
      !FrustumUtils.isVisible(
        context.frustum,
        min(start.x, end.x),
        min(start.y, end.y),
        min(start.z, end.z),
        max(start.x, end.x),
        max(start.y, end.y),
        max(start.z, end.z)
      )
    ) {
      return
    }

    val matrix = context.matrixStack ?: return
    val bufferSource = context.consumers as? MultiBufferSource.BufferSource ?: return
    val layer = if (esp) RenderLayers.LINE_LIST_ESP else RenderLayers.LINE_LIST
    RenderSystem.lineWidth(thickness)

    matrix.pushPose()
    with(context.camera.position) { matrix.translate(-x, -y, -z) }

    val startOffset = Vector3f(start.x.toFloat(), start.y.toFloat(), start.z.toFloat())
    val direction = end.subtract(start)

    ShapeRenderer.renderVector(
      matrix,
      bufferSource.getBuffer(layer),
      startOffset,
      direction,
      color.rgb
    )

    matrix.popPose()
    bufferSource.endBatch(layer)
  }

}
