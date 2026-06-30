package org.cobalt.pathfinder.calculate

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import kotlin.time.Duration.Companion.milliseconds
import org.cobalt.pathfinder.calculate.openset.BinaryHeapOpenSet
import org.cobalt.pathfinder.goal.IGoal
import org.cobalt.pathfinder.movement.CalculationContext
import org.cobalt.pathfinder.movement.Movement
import org.cobalt.pathfinder.movement.MovementResult

class AStarPathfinder(
  val startX: Int,
  val startY: Int,
  val startZ: Int,
  val goal: IGoal,
  val movements: Array<out Movement>,
) {

  private val closedSet = Long2ObjectOpenHashMap<PathNode>()
  private var startTime = 0L

  fun findPath(): Path? {
    val ctx = CalculationContext()
    val openSet = BinaryHeapOpenSet()
    val res = MovementResult()

    val startNode = PathNode(
      startX, startY, startZ, goal
    ).also {
      it.costSoFar = 0.0
      it.totalCost = it.costToEnd
    }

    openSet.add(startNode)

    startTime = System.currentTimeMillis()

    while (!openSet.isEmpty()) {
      val currentNode = openSet.poll()

      if (goal.isAtGoal(currentNode.x, currentNode.y, currentNode.z)) {
        return reconstruct(currentNode)
      }

      for (move in movements) {
        res.reset()
        move.calculateCost(ctx, currentNode, res)

        if (res.cost >= ctx.infCost) {
          continue
        }

        val neighborCostSoFar = currentNode.costSoFar + res.cost
        val neighborNode = getNode(
          res.x, res.y, res.z,
          PathNode.longHash(res.x, res.y, res.z)
        )

        if (neighborCostSoFar < neighborNode.costSoFar) {
          neighborNode.parent = currentNode
          neighborNode.costSoFar = neighborCostSoFar
          neighborNode.totalCost = neighborCostSoFar + neighborNode.costToEnd
          neighborNode.type = res.type

          if (neighborNode.heapPosition == -1) {
            openSet.add(neighborNode)
          } else {
            openSet.relocate(neighborNode)
          }
        }
      }
    }

    return null
  }

  fun getNode(x: Int, y: Int, z: Int, hash: Long): PathNode {
    var node: PathNode? = closedSet.get(hash)

    if (node == null) {
      node = PathNode(x, y, z, goal)
      closedSet.put(hash, node)
    }

    return node
  }

  private fun reconstruct(endNode: PathNode): Path {
    val path = mutableListOf<PathNode>()
    var node: PathNode? = endNode

    while (node != null) {
      path.addFirst(node)
      node = node.parent
    }

    return Path(
      nodes = path,
      timeElapsed = (System.currentTimeMillis() - startTime).milliseconds,
      goal = goal
    )
  }

}
