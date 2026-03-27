package org.cobalt.util.render

import java.awt.Color
import kotlin.math.max
import kotlin.math.min
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.cobalt.util.FrustumUtils

object Render3D {

  private val minecraft: Minecraft = Minecraft.getInstance()

  fun drawBlockPos(
    context: LevelRenderContext,
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

  fun drawEntityOutline(
    context: LevelRenderContext,
    entity: Entity,
    color: Color,
    esp: Boolean = false,
  ) {
    val partialTicks = minecraft.deltaTracker.getGameTimeDeltaPartialTick(true)

    val interpolatedX = entity.xOld + (entity.x - entity.xOld) * partialTicks
    val interpolatedY = entity.yOld + (entity.y - entity.yOld) * partialTicks
    val interpolatedZ = entity.zOld + (entity.z - entity.zOld) * partialTicks

    val dx = interpolatedX - entity.x
    val dy = interpolatedY - entity.y
    val dz = interpolatedZ - entity.z

    drawBox(context, entity.boundingBox.move(dx, dy, dz), color, esp)
  }

  fun drawTracer(
    context: LevelRenderContext,
    to: Vec3,
    color: Color,
    esp: Boolean = true,
    thickness: Float = 1f,
  ) {
    val camera = minecraft.gameRenderer.mainCamera
    val cameraPos = camera.position()
    val from = cameraPos.add(Vec3.directionFromRotation(camera.xRot(), camera.yRot()))

    drawLine(context, from, to, color, esp, thickness)
  }

  fun drawBox(
    context: LevelRenderContext,
    box: AABB,
    color: Color,
    esp: Boolean = false,
  ) {
    if (color.alpha == 0) return

    val frustum = context.levelState().cameraRenderState.cullFrustum

    if (!FrustumUtils.isVisible(frustum, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)) {
      return
    }

    val poseStack = context.poseStack()
    val bufferSource = context.bufferSource()
    val camera = context.gameRenderer().mainCamera

    // TODO: Finish
  }

  fun drawLine(
    context: LevelRenderContext,
    from: Vec3,
    to: Vec3,
    color: Color,
    esp: Boolean = false,
    thickness: Float = 1f,
  ) {
    if (color.alpha == 0) return

    val frustum = context.levelState().cameraRenderState.cullFrustum

    if (
      !FrustumUtils.isVisible(
        frustum,
        min(from.x, to.x),
        min(from.y, to.y),
        min(from.z, to.z),
        max(from.x, to.x),
        max(from.y, to.y),
        max(from.z, to.z)
      )
    ) {
      return
    }

    val poseStack = context.poseStack()
    val bufferSource = context.bufferSource()
    val camera = context.gameRenderer().mainCamera

    // TODO: Finish
  }

}
