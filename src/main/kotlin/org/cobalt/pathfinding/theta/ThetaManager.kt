package org.cobalt.pathfinding.theta

import net.minecraft.core.BlockPos
import org.cobalt.Cobalt.minecraft

object ThetaManager {

  var lastPath: ThetaPath? = null
    private set

  fun pathfind(goal: BlockPos): ThetaPath? {

    val level = minecraft.level ?: return null
    val player = minecraft.player ?: return null

    val pathfinder = ThetaPathfinder(level)

    val result = pathfinder.findPath(
      player.blockPosition(),
      goal,
    )

    lastPath = result

    ThetaPathRenderer.setPath(result)

    return result
  }

  fun applyExternalPath(path: ThetaPath) {
    lastPath = path
    ThetaPathRenderer.setPath(path)
  }

  fun clear() {
    lastPath = null
    ThetaPathRenderer.clear()
  }

  fun toggleRender() {
    ThetaPathRenderer.toggle()
  }
}
