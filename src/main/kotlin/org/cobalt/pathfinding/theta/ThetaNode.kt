package org.cobalt.pathfinding.theta

import net.minecraft.core.BlockPos

class ThetaNode(
  val pos: BlockPos,
) {

  var g = Double.POSITIVE_INFINITY
  var h = 0.0

  var parent: ThetaNode? = null
  var closed = false

  val f: Double
    get() = g + h

}
