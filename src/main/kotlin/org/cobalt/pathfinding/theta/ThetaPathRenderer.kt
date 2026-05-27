package org.cobalt.pathfinding.theta

import java.awt.Color
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import org.cobalt.util.RenderUtils
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext

object ThetaPathRenderer {

  @Volatile
  private var path: List<BlockPos> = emptyList()

  @Volatile
  var enabled: Boolean = true

  @Volatile
  var color: Color = Color.GREEN

  @Volatile
  var lineWidth: Float = 3f

  fun render(context: LevelRenderContext) {

    if (!enabled) return
    val localPath = path

    if (localPath.size < 2) return

    for (i in 0 until localPath.size - 1) {

      val a = localPath[i]
      val b = localPath[i + 1]

      RenderUtils.drawLine(
        context,
        toVec3(a),
        toVec3(b),
        color,
        true,
        lineWidth
      )
    }
  }


  fun setPath(thetaPath: ThetaPath) {
    path = thetaPath.points
  }

  fun setPath(points: List<BlockPos>) {
    path = points
  }

  fun clear() {
    path = emptyList()
  }

  fun toggle() {
    enabled = !enabled
  }

  private fun toVec3(pos: BlockPos): Vec3 {
    return Vec3(
      pos.x + 0.5,
      pos.y + 0.1,
      pos.z + 0.5
    )
  }
}
