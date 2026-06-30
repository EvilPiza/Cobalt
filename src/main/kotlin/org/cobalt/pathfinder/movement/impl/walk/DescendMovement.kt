package org.cobalt.pathfinder.movement.impl.walk

import org.cobalt.pathfinder.calculate.PathNode
import org.cobalt.pathfinder.movement.CalculationContext
import org.cobalt.pathfinder.movement.Movement
import org.cobalt.pathfinder.movement.MovementResult

class DescendMovement(
  val dx: Int,
  val dz: Int,
) : Movement() {

  override fun calculateCost(
    ctx: CalculationContext,
    currNode: PathNode,
    res: MovementResult,
  ) {
    res.set(currNode.x + dx, currNode.y - 1, currNode.z + dz)
    res.cost = 1.0
  }

  companion object {
    val NORTH = DescendMovement(0, -1)
    val SOUTH = DescendMovement(0, 1)
    val EAST = DescendMovement(1, 0)
    val WEST = DescendMovement(-1, 0)
  }

}
