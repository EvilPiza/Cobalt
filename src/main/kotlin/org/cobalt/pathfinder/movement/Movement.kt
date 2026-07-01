package org.cobalt.pathfinder.movement

import org.cobalt.pathfinder.calculate.PathNode

abstract class Movement(
  val type: Type
) {

  abstract fun calculateCost(ctx: CalculationContext, currNode: PathNode, res: MovementResult)

  enum class Type {
    WALK, FLY, ETHERWARP, AOTV
  }

}
