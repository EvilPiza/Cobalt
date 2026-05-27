package org.cobalt.pathfinding.theta

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import kotlin.math.max

object ThetaMovementRules {

  private const val WIDTH = 0.6
  private const val HEIGHT = 1.8

  fun canTraverse(from: BlockPos, to: BlockPos, level: Level): Boolean {
    if (!hasGround(to, level)) return false
    if (!hasSpace(to, level)) return false
    if (!clearLine(from, to, level)) return false

    return true
  }

  fun shouldJump(from: BlockPos, to: BlockPos, level: Level): Boolean {
    val dy = to.y - from.y

    return dy in 1..2 && !hasSpace(to.above(), level)
  }

  private fun hasGround(pos: BlockPos, level: Level): Boolean {
    return !level.getBlockState(pos.below()).isAir
  }

  private fun hasSpace(pos: BlockPos, level: Level): Boolean {
    val box = AABB(
      pos.x + 0.5 - WIDTH / 2,
      pos.y.toDouble(),
      pos.z + 0.5 - WIDTH / 2,
      pos.x + 0.5 + WIDTH / 2,
      pos.y + HEIGHT,
      pos.z + 0.5 + WIDTH / 2,
    )

    return level.noCollision(box)
  }

  private fun clearLine(from: BlockPos, to: BlockPos, level: Level): Boolean {
    val steps = 6

    for (i in 0..steps) {

      val t = i.toDouble() / steps

      val x = from.x + (to.x - from.x) * t
      val y = from.y + (to.y - from.y) * t
      val z = from.z + (to.z - from.z) * t

      if (!hasSpace(BlockPos(x.toInt(), y.toInt(), z.toInt()), level)) {
        return false
      }
    }

    return true
  }

}
