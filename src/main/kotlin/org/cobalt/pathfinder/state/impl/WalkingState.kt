package org.cobalt.pathfinder.state.impl

import org.cobalt.pathfinder.PathExecutor
import org.cobalt.pathfinder.state.ExecutorState
import org.cobalt.ui.theme.ThemeManager
import org.cobalt.util.PlayerUtils
import org.cobalt.util.WorldRenderUtils

class WalkingState : ExecutorState {

  override fun onTick() {
    val path = PathExecutor.path ?: run {
      PathExecutor.stop()
      return
    }

    val index = PathExecutor.pathIndex
    val node = path.nodes[index]

    val playerPos = PlayerUtils.position
    val nodePos = node.blockPos

    if (playerPos.distSqr(nodePos) < 4) { // todo: change later
      if (index + 1 >= path.nodes.size) {
        PathExecutor.stop()
        return
      }

      PathExecutor.pathIndex++
      return
    }

    // todo: handle movement + rotations
  }

  override fun onRender() {
    val theme = ThemeManager.activeTheme
    val nodes = PathExecutor.path?.nodes ?: return

    for (index in nodes.indices) {
      val node = nodes[index]

      WorldRenderUtils.drawBlockPos(
        node.blockBelow.atY(node.y - 1),
        color = theme.accentPrimary
      )

      if (index > 0) {
        val prev = nodes[index - 1]

        WorldRenderUtils.drawLine(
          prev.centerVec, node.centerVec,
          theme.accentSecondary
        )
      }
    }
  }

}
