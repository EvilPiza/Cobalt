package org.cobalt.pathfinder.movement

import org.cobalt.pathfinder.calculate.PathNode

// TODO: Implement costs in each movement type (to improve path quality)
abstract class Movement(
  val type: Type,
) {

  abstract fun calculateCost(ctx: CalculationContext, currNode: PathNode, res: MovementResult)

  enum class Type {
    WALK, FLY
  }

}
