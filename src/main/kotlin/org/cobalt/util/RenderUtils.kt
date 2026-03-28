package org.cobalt.util

import java.awt.Color
import kotlin.math.max
import kotlin.math.min
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.cobalt.util.helper.Layers

object RenderUtils {

  private val minecraft: Minecraft =
    Minecraft.getInstance()

  fun drawBlockPos(
      context: LevelRenderContext,
      pos: BlockPos,
      color: Color,
      esp: Boolean = false,
      lineWidth: Float = 1f,
  ) {
    val box = AABB(
        pos.x.toDouble(),
        pos.y.toDouble(),
        pos.z.toDouble(),
        pos.x + 1.0,
        pos.y + 1.0,
        pos.z + 1.0
    )

    drawBox(context, box, color, esp, lineWidth)
  }

  fun drawEntityOutline(
      context: LevelRenderContext,
      entity: Entity,
      color: Color,
      esp: Boolean = false,
      lineWidth: Float = 1f,
  ) {
    val partialTicks = minecraft.deltaTracker.getGameTimeDeltaPartialTick(true)

    val interpolatedX = entity.xOld + (entity.x - entity.xOld) * partialTicks
    val interpolatedY = entity.yOld + (entity.y - entity.yOld) * partialTicks
    val interpolatedZ = entity.zOld + (entity.z - entity.zOld) * partialTicks

    val dx = interpolatedX - entity.x
    val dy = interpolatedY - entity.y
    val dz = interpolatedZ - entity.z

    drawBox(context, entity.boundingBox.move(dx, dy, dz), color, esp, lineWidth)
  }

  fun drawTracer(
      context: LevelRenderContext,
      to: Vec3,
      color: Color,
      esp: Boolean = true,
      lineWidth: Float = 1f,
  ) {
    val camera = minecraft.gameRenderer.mainCamera
    val cameraPos = camera.position()
    val from = cameraPos.add(Vec3.directionFromRotation(camera.xRot(), camera.yRot()))

    drawLine(context, from, to, color, esp, lineWidth)
  }

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

    val frustum = context.levelState().cameraRenderState.cullFrustum

    if (!FrustumUtils.isVisible(frustum, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)) {
      return
    }

    val poseStack = context.poseStack()
    val bufferSource = context.bufferSource()
    val cameraPos = minecraft.gameRenderer.mainCamera.position()
    val matrix = poseStack.last().pose()
    val poseEntry = poseStack.last()

    val fillColor = Color(color.red, color.green, color.blue, 100)
    val corners = arrayOf(
        Vec3(box.minX, box.minY, box.minZ), Vec3(box.maxX, box.minY, box.minZ),
        Vec3(box.maxX, box.minY, box.maxZ), Vec3(box.minX, box.minY, box.maxZ),
        Vec3(box.minX, box.maxY, box.minZ), Vec3(box.maxX, box.maxY, box.minZ),
        Vec3(box.maxX, box.maxY, box.maxZ), Vec3(box.minX, box.maxY, box.maxZ),
    )

    val quadBuffer = bufferSource.getBuffer(Layers.getQuads(esp))

    for (index in BOX_QUADS) {
      val corner = corners[index]
      quadBuffer.addVertex(
        matrix,
        (corner.x - cameraPos.x).toFloat(),
        (corner.y - cameraPos.y).toFloat(),
        (corner.z - cameraPos.z).toFloat(),
      ).setColor(fillColor.red, fillColor.green, fillColor.blue, fillColor.alpha)
    }

    bufferSource.endBatch(Layers.getQuads(esp))

    val lineBuffer = bufferSource.getBuffer(Layers.getLines(esp))

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

    bufferSource.endBatch(Layers.getLines(esp))
  }

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

    val frustum = context.levelState().cameraRenderState.cullFrustum

    if (
      !FrustumUtils.isVisible(
        frustum,
          min(from.x, to.x), min(from.y, to.y), min(from.z, to.z),
          max(from.x, to.x), max(from.y, to.y), max(from.z, to.z),
      )
    ) {
      return
    }

    val poseStack = context.poseStack()
    val bufferSource = context.bufferSource()
    val cameraPos = minecraft.gameRenderer.mainCamera.position()
    val poseEntry = poseStack.last()
    val matrix = poseEntry.pose()
    val lineBuffer = bufferSource.getBuffer(Layers.getLines(esp))
    val lineNormal = to.subtract(from).normalize()

    for (vertex in listOf(from, to)) {
      lineBuffer
        .addVertex(
          matrix,
          (vertex.x - cameraPos.x).toFloat(),
          (vertex.y - cameraPos.y).toFloat(),
          (vertex.z - cameraPos.z).toFloat()
        )
        .setLineWidth(lineWidth)
        .setColor(color.red, color.green, color.blue, color.alpha)
        .setNormal(poseEntry, lineNormal.x.toFloat(), lineNormal.y.toFloat(), lineNormal.z.toFloat())
    }

    bufferSource.endBatch(Layers.getLines(esp))
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
