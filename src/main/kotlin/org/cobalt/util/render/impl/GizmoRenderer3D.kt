package org.cobalt.util.render.impl

import java.awt.Color
import kotlin.math.max
import kotlin.math.min
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.gizmos.GizmoStyle
import net.minecraft.gizmos.Gizmos
import net.minecraft.util.ARGB
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.cobalt.event.impl.RenderContext
import org.cobalt.util.FrustumUtils
import org.cobalt.util.render.Render3D

internal object GizmoRenderer3D : Render3D {

  private val minecraft: Minecraft
    get() = Minecraft.getInstance()

  override fun drawBlockPos(
    context: RenderContext,
    pos: BlockPos,
    color: Color,
    esp: Boolean,
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

  override fun drawBox(
    context: RenderContext,
    box: AABB,
    color: Color,
    esp: Boolean,
  ) {
    if (color.alpha == 0) {
      return
    }

    if (!FrustumUtils.isVisible(context.frustum, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)) {
      return
    }

    val strokeColor = ARGB.color(color.alpha, color.red, color.green, color.blue)
    val fillColor = ARGB.color(125, color.red, color.green, color.blue)

    val style = GizmoStyle.strokeAndFill(strokeColor, 1.0f, fillColor)
    val props = Gizmos.cuboid(box, style)

    if (esp) {
      props.setAlwaysOnTop()
    }
  }

  override fun drawTracer(
    context: RenderContext,
    to: Vec3,
    color: Color,
    esp: Boolean,
    thickness: Float,
  ) {
    val camera = minecraft.gameRenderer.mainCamera
    val cameraPos = camera.position()
    val from = cameraPos.add(Vec3.directionFromRotation(camera.xRot(), camera.yRot()))

    drawLine(context, from, to, color, esp, thickness)
  }

  override fun drawLine(
    context: RenderContext,
    start: Vec3,
    end: Vec3,
    color: Color,
    esp: Boolean,
    thickness: Float,
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

    val argbColor = ARGB.color(color.alpha, color.red, color.green, color.blue)
    val props = Gizmos.line(start, end, argbColor, thickness)

    if (esp) {
      props.setAlwaysOnTop()
    }
  }


}
