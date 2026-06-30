package org.cobalt.pathfinder.movement.impl.walk

import org.cobalt.pathfinder.calculate.PathNode
import org.cobalt.pathfinder.movement.CalculationContext
import org.cobalt.pathfinder.movement.Movement
import org.cobalt.pathfinder.movement.MovementResult

class TraverseMovement(
  val dx: Int,
  val dz: Int,
) : Movement() {

  override fun calculateCost(
    ctx: CalculationContext,
    currNode: PathNode,
    res: MovementResult,
  ) {
    res.set(currNode.x + dx, currNode.y, currNode.z + dz)
    res.cost = 1.0
  }

  companion object {
    val NORTH = TraverseMovement(0, -1)
    val SOUTH = TraverseMovement(0, 1)
    val EAST = TraverseMovement(1, 0)
    val WEST = TraverseMovement(-1, 0)
  }

}
