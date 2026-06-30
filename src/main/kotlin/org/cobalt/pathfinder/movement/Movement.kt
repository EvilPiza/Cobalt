package org.cobalt.pathfinder.movement

import org.cobalt.pathfinder.calculate.PathNode

abstract class Movement {

  abstract fun calculateCost(ctx: CalculationContext, currNode: PathNode, res: MovementResult)

}
