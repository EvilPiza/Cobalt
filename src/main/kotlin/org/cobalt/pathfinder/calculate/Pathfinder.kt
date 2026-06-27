package org.cobalt.pathfinder.calculate

import net.minecraft.core.BlockPos
import org.cobalt.pathfinder.goal.IGoal

abstract class Pathfinder(
  val startX: Int,
  val startY: Int,
  val startZ: Int,
  val goal: IGoal
) {

  abstract fun findPath(): List<BlockPos>?

}
