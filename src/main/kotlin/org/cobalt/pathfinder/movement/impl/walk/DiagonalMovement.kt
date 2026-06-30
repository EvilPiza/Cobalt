package org.cobalt.pathfinder.movement.impl.walk

import org.cobalt.pathfinder.calculate.PathNode
import org.cobalt.pathfinder.movement.CalculationContext
import org.cobalt.pathfinder.movement.Movement
import org.cobalt.pathfinder.movement.MovementResult

class DiagonalMovement(
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
    val NORTH_EAST = DiagonalMovement(1, -1)
    val NORTH_WEST = DiagonalMovement(-1, -1)
    val SOUTH_EAST = DiagonalMovement(1, 1)
    val SOUTH_WEST = DiagonalMovement(-1, 1)
  }

}
