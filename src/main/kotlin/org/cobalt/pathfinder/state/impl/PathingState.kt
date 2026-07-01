package org.cobalt.pathfinder.state.impl

import net.minecraft.client.KeyMapping
import org.cobalt.Cobalt.minecraft
import org.cobalt.dsl.centerVec
import org.cobalt.pathfinder.PathExecutor
import org.cobalt.pathfinder.movement.Movement
import org.cobalt.pathfinder.movement.MovementHelper
import org.cobalt.pathfinder.state.ExecutorState
import org.cobalt.util.KeybindUtils
import org.cobalt.util.PlayerUtils

// todo: fix movement & rotations
class PathingState : ExecutorState {

  private val path = PathExecutor.path
  private val config = PathExecutor.config

  override fun exit() {
    KeybindUtils.stopMovement()
  }

  override fun onTick() {
    if (path == null || config == null) {
      PathExecutor.stop()
      return
    }

    val index = PathExecutor.pathIndex
    val node = path.nodes[index]

    val (yaw, _) = PlayerUtils.rotation
    val playerPos = PlayerUtils.position
    val nodePos = node.blockPos
    val isFlyNode = node.type == Movement.Type.FLY

    if (
      isFlyNode &&
      config.useFlyMovement &&
      PlayerUtils.canFly &&
      !PlayerUtils.isFlying
    ) {
      PathExecutor.changeState(StartFlyState())
      return
    }

    val distSq = playerPos.centerVec().distanceToSqr(nodePos.centerVec())

    if (distSq < 0.3 * 0.3) {
      if (index + 1 >= path.nodes.size) {
        PathExecutor.stop()
        return
      }

      PathExecutor.pathIndex++
      return
    }

    val rotation = MovementHelper.getRotation(playerPos.centerVec(), nodePos.centerVec())
    val sameXZ = nodePos.x == playerPos.x && nodePos.z == playerPos.z

    val keys = mutableListOf<KeyMapping>()
    val neededKeys = MovementHelper.getNeededKeys(yaw, rotation.yaw)

    if (config.shouldSprint && !isFlyNode) {
      keys.add(minecraft.options.keySprint)
    }

    if (!sameXZ) {
      keys.addAll(neededKeys)
    }

    if (isFlyNode && sameXZ) {
      val diffY = nodePos.y - playerPos.y

      when {
        diffY > 0 -> {
          keys.add(minecraft.options.keyJump)
        }

        diffY < 0 -> {
          keys.add(minecraft.options.keyShift)
        }
      }
    }

    KeybindUtils.holdThese(*keys.toTypedArray())
  }

}
