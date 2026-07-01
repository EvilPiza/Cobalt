package org.cobalt.pathfinder.movement

import org.cobalt.pathfinder.calculate.PathNode

// todo: implement costs in each movement type
abstract class Movement(
  val type: Type
) {

  abstract fun calculateCost(ctx: CalculationContext, currNode: PathNode, res: MovementResult)

  enum class Type {
    WALK, FLY
  }

}
