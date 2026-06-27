package org.cobalt.pathfinder.calculate

import org.cobalt.pathfinder.goal.IGoal

data class PathNode(
  val x: Int,
  val y: Int,
  val z: Int,
  val goal: IGoal,
) : Comparable<PathNode> {

  var costSoFar = Double.POSITIVE_INFINITY
  val costToEnd = goal.heuristic(x, y, z)
  var totalCost = 1.0
  var heapPosition = -1

  var parent: PathNode? = null

  override fun compareTo(other: PathNode): Int =
    totalCost.compareTo(other.totalCost)

}
