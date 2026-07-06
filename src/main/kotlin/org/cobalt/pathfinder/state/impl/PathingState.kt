package org.cobalt.pathfinder.state.impl

import org.cobalt.dsl.centerVec
import org.cobalt.pathfinder.PathExecutor
import org.cobalt.pathfinder.helper.PlayerInput
import org.cobalt.pathfinder.movement.Movement
import org.cobalt.pathfinder.movement.MovementHelper
import org.cobalt.pathfinder.state.ExecutorState
import org.cobalt.util.PlayerUtils

/**
 * TODO: Fix Movement & Rotations
 * - Find points alone path to rotate to and make a custom aiming system for pathfinding
 * - Handle unstuck (where you just need to move left or right to realign to center of path line)
 * - Handle jumps and descending blocks
 */
class PathingState : ExecutorState() {

  private val path = PathExecutor.path
  private val config = PathExecutor.config

  override fun exit() {
    input.stopMovement()
  }

  override fun onTick() {
    if (path == null || config == null) {
      PathExecutor.stop()
      return
    }

    val index = PathExecutor.pathIndex
    val node = path.keyNodes[index]

    val (yaw, _) = PlayerUtils.rotation
    val playerPos = PlayerUtils.position
    val nodePos = node.blockPos
    val isFlyNode = node.type == Movement.Type.FLY

    val distSq = playerPos.centerVec().distanceToSqr(nodePos.centerVec())

    // TODO: Update this logic to handle overshooting at high speeds or jumping over target (if user has jump boost)
    if (distSq < 0.3 * 0.3) {
      if (index + 1 >= path.keyNodes.size) {
        PathExecutor.stop()
        return
      }

      PathExecutor.pathIndex++
      return
    }

    if (
      isFlyNode &&
      config.useFlyMovement &&
      PlayerUtils.canFly &&
      !PlayerUtils.isFlying
    ) {
      PathExecutor.changeState(StartFlyState())
      return
    }

    val rotation = MovementHelper.getRotation(playerPos.centerVec(), nodePos.centerVec())
    val sameXZ = nodePos.x == playerPos.x && nodePos.z == playerPos.z

    val playerInput = PlayerInput()
    val neededKeys = MovementHelper.getNeededKeys(yaw, rotation.yaw)

    if (!sameXZ) {
      playerInput.apply(neededKeys)
    }

    if (config.shouldSprint && !isFlyNode) {
      playerInput.sprint = true
    }

    // TODO: add jump logic for walking
    // playerInput.jump = !PlayerUtils.isFlying && false

    if (isFlyNode && sameXZ) {
      val diffY = nodePos.y - playerPos.y

      when {
        diffY > 0 -> playerInput.jump = true
        diffY < 0 -> playerInput.sneak = true
      }
    }

    input.applyInput(playerInput)
  }

  override fun onRender() {
    // TODO: call rotation update here
  }

}
