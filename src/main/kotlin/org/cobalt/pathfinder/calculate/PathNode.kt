package org.cobalt.pathfinder.calculate

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import org.cobalt.pathfinder.goal.IGoal

data class PathNode(
  val x: Int,
  val y: Int,
  val z: Int,
  val goal: IGoal,
) {

  var costSoFar = 1e6
  val costToEnd = goal.heuristic(x, y, z)
  var totalCost = 1.0
  var heapPosition = -1
  var type = PathMode.WALK
  var parent: PathNode? = null

  val blockPos: BlockPos = BlockPos(x, y, z)
  val blockBelow: BlockPos = BlockPos(x, y - 1, z)
  val centerVec: Vec3 = Vec3(x + 0.5, y.toDouble(), z + 0.5)

  override fun equals(other: Any?): Boolean {
    val otherNode = other as PathNode

    return otherNode.x == x &&
      otherNode.y == y &&
      otherNode.z == z
  }

  override fun hashCode(): Int {
    return longHash(x, y, z).toInt()
  }

  companion object {
    fun longHash(x: Int, y: Int, z: Int): Long {
      var hash = 3241L
      hash = 3457689L * hash + x
      hash = 8734625L * hash + y
      hash = 2873465L * hash + z
      return hash
    }
  }

}
