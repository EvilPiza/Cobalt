package org.cobalt.pathfinder.calculate

import net.minecraft.core.BlockPos
import org.cobalt.Cobalt.minecraft
import org.cobalt.pathfinder.calculate.openset.BinaryHeapOpenSet
import org.cobalt.pathfinder.goal.IGoal
import org.cobalt.pathfinder.helper.BlockStateAccessor
import org.cobalt.pathfinder.movement.CalculationContext

class AStarPathfinder(
  startX: Int,
  startY: Int,
  startZ: Int,
  goal: IGoal,
) : Pathfinder(startX, startY, startZ, goal) {

  override fun findPath(): List<BlockPos>? {
    val ctx = CalculationContext()
    val openSet = BinaryHeapOpenSet()
    val visited = HashMap<Long, PathNode>()

    val startNode = PathNode(startX, startY, startZ, goal).also {
      it.costSoFar = 0.0
      it.totalCost = it.costToEnd
    }

    openSet.add(startNode)
    visited[startNode.key()] = startNode

    while (!openSet.isEmpty()) {
      val current = openSet.poll()

      if (goal.isAtGoal(current.x, current.y, current.z)) {
        return reconstruct(current)
      }


    }

    return null
  }

  fun PathNode.key(): Long {
    return (x.toLong() and 0x3FFFFFF) shl 38 or
      (z.toLong() and 0x3FFFFFF) shl 12 or
      (y.toLong() and 0xFFF)
  }

  private fun reconstruct(end: PathNode): List<BlockPos> {
    val path = ArrayDeque<BlockPos>()
    var node: PathNode? = end

    while (node != null) {
      path.addFirst(BlockPos(node.x, node.y, node.z))
      node = node.parent
    }

    return path
  }

}
