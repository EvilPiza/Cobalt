package org.cobalt.util

import java.awt.Color
import kotlin.math.max
import kotlin.math.min
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.cobalt.Cobalt
import org.cobalt.util.helper.CustomRenderTypes

object RenderUtils {

  @JvmStatic
  fun drawBlockPos(
    context: LevelRenderContext,
    pos: BlockPos,
    color: Color,
    esp: Boolean = false,
    lineWidth: Float = 1f,
  ) {
    drawBox(
      context,
      AABB(
        pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(),
        pos.x + 1.0, pos.y + 1.0, pos.z + 1.0,
      ),
      color, esp, lineWidth,
    )
  }

  fun drawEntityOutline(
    context: LevelRenderContext,
    entity: Entity,
    color: Color,
    esp: Boolean = false,
    lineWidth: Float = 1f,
  ) {
    val partialTicks = Cobalt.minecraft.deltaTracker.getGameTimeDeltaPartialTick(true)
    drawBox(
      context,
      entity.boundingBox.move(
        entity.xOld + (entity.x - entity.xOld) * partialTicks - entity.x,
        entity.yOld + (entity.y - entity.yOld) * partialTicks - entity.y,
        entity.zOld + (entity.z - entity.zOld) * partialTicks - entity.z,
      ),
      color, esp, lineWidth,
    )
  }

  @JvmStatic
  fun drawTracer(
    context: LevelRenderContext,
    to: Vec3,
    color: Color,
    esp: Boolean = true,
    lineWidth: Float = 1f,
  ) {
    val camera = Cobalt.minecraft.gameRenderer.mainCamera

    drawLine(
      context,
      camera
        .position()
        .add(Vec3.directionFromRotation(camera.xRot(), camera.yRot())),
      to,
      color,
      esp,
      lineWidth
    )
  }

  @JvmStatic
  fun drawBox(
    context: LevelRenderContext,
    box: AABB,
    color: Color,
    esp: Boolean = false,
    lineWidth: Float = 1f,
  ) {
    if (color.alpha == 0) {
      return
    }

    if (
      !FrustumUtils.isVisible(
        context.levelState().cameraRenderState.cullFrustum,
        box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ,
      )
    ) {
      return
    }

    val cameraPos = Cobalt.minecraft.gameRenderer.mainCamera.position()
    val corners = arrayOf(
      Vec3(box.minX, box.minY, box.minZ), Vec3(box.maxX, box.minY, box.minZ),
      Vec3(box.maxX, box.minY, box.maxZ), Vec3(box.minX, box.minY, box.maxZ),
      Vec3(box.minX, box.maxY, box.minZ), Vec3(box.maxX, box.maxY, box.minZ),
      Vec3(box.maxX, box.maxY, box.maxZ), Vec3(box.minX, box.maxY, box.maxZ),
    )

    val bufferSource = context.bufferSource()
    val matrix = context.poseStack().last().pose()
    val fillColor = Color(color.red, color.green, color.blue, 100)
    val quadBuffer = bufferSource.getBuffer(CustomRenderTypes.getQuads(esp))

    for (index in BOX_QUADS) {
      val corner = corners[index]
      quadBuffer.addVertex(
        matrix,
        (corner.x - cameraPos.x).toFloat(),
        (corner.y - cameraPos.y).toFloat(),
        (corner.z - cameraPos.z).toFloat(),
      ).setColor(fillColor.red, fillColor.green, fillColor.blue, fillColor.alpha)
    }

    bufferSource.endBatch(CustomRenderTypes.getQuads(esp))

    val poseEntry = context.poseStack().last()
    val lineBuffer = bufferSource.getBuffer(CustomRenderTypes.getLines(esp))

    for (i in BOX_LINES.indices step 2) {
      val lineStart = corners[BOX_LINES[i]]
      val lineEnd = corners[BOX_LINES[i + 1]]
      val lineNormal = lineEnd.subtract(lineStart).normalize()

      for (vertex in listOf(lineStart, lineEnd)) {
        lineBuffer.addVertex(
          matrix,
          (vertex.x - cameraPos.x).toFloat(),
          (vertex.y - cameraPos.y).toFloat(),
          (vertex.z - cameraPos.z).toFloat(),
        )
          .setLineWidth(lineWidth)
          .setColor(color.red, color.green, color.blue, color.alpha)
          .setNormal(poseEntry, lineNormal.x.toFloat(), lineNormal.y.toFloat(), lineNormal.z.toFloat())
      }
    }

    bufferSource.endBatch(CustomRenderTypes.getLines(esp))
  }

  @JvmStatic
  fun drawLine(
    context: LevelRenderContext,
    from: Vec3,
    to: Vec3,
    color: Color,
    esp: Boolean = false,
    lineWidth: Float = 1f,
  ) {
    if (color.alpha == 0) {
      return
    }

    if (
      !FrustumUtils.isVisible(
        context.levelState().cameraRenderState.cullFrustum,
        min(from.x, to.x), min(from.y, to.y), min(from.z, to.z),
        max(from.x, to.x), max(from.y, to.y), max(from.z, to.z),
      )
    ) {
      return
    }

    val bufferSource = context.bufferSource()
    val poseEntry = context.poseStack().last()
    val cameraPos = Cobalt.minecraft.gameRenderer.mainCamera.position()
    val lineNormal = to.subtract(from).normalize()
    val lineBuffer = bufferSource.getBuffer(CustomRenderTypes.getLines(esp))

    for (vertex in listOf(from, to)) {
      lineBuffer.addVertex(
        poseEntry.pose(),
        (vertex.x - cameraPos.x).toFloat(),
        (vertex.y - cameraPos.y).toFloat(),
        (vertex.z - cameraPos.z).toFloat(),
      )
        .setLineWidth(lineWidth)
        .setColor(color.red, color.green, color.blue, color.alpha)
        .setNormal(poseEntry, lineNormal.x.toFloat(), lineNormal.y.toFloat(), lineNormal.z.toFloat())
    }

    bufferSource.endBatch(CustomRenderTypes.getLines(esp))
  }

  private val BOX_QUADS = intArrayOf(
    0, 1, 2, 3,
    4, 7, 6, 5,
    0, 4, 5, 1,
    1, 5, 6, 2,
    2, 6, 7, 3,
    3, 7, 4, 0,
  )

  private val BOX_LINES = intArrayOf(
    0, 1, 1, 2, 2, 3, 3, 0,
    4, 5, 5, 6, 6, 7, 7, 4,
    0, 4, 1, 5, 2, 6, 3, 7,
  )

}
