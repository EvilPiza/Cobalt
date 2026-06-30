package org.cobalt.pathfinder.movement.impl.fly

import org.cobalt.pathfinder.calculate.PathMode
import org.cobalt.pathfinder.calculate.PathNode
import org.cobalt.pathfinder.movement.CalculationContext
import org.cobalt.pathfinder.movement.Movement
import org.cobalt.pathfinder.movement.MovementResult

class FlyDescendMovement : Movement() {

  override fun calculateCost(
    ctx: CalculationContext,
    currNode: PathNode,
    res: MovementResult,
  ) {
    res.set(currNode.x, currNode.y - 1, currNode.z)
    res.type = PathMode.FLY
    res.cost = 1.0
  }

  companion object {
    val DEFAULT = FlyDescendMovement()
  }

}
