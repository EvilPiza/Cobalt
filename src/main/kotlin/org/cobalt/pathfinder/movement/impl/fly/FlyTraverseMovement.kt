package org.cobalt.pathfinder.movement.impl.fly

import org.cobalt.pathfinder.calculate.PathMode
import org.cobalt.pathfinder.calculate.PathNode
import org.cobalt.pathfinder.movement.CalculationContext
import org.cobalt.pathfinder.movement.Movement
import org.cobalt.pathfinder.movement.MovementResult

class FlyTraverseMovement(
  val dx: Int,
  val dz: Int,
) : Movement() {

  override fun calculateCost(
    ctx: CalculationContext,
    currNode: PathNode,
    res: MovementResult,
  ) {
    res.set(currNode.x + dx, currNode.y, currNode.z + dz)
    res.type = PathMode.FLY
    res.cost = 1.0
  }

  companion object {
    val NORTH = FlyTraverseMovement(0, -1)
    val SOUTH = FlyTraverseMovement(0, 1)
    val EAST = FlyTraverseMovement(1, 0)
    val WEST = FlyTraverseMovement(-1, 0)
  }

}
