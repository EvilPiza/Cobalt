package org.cobalt.pathfinder.state.impl

import org.cobalt.Cobalt.minecraft
import org.cobalt.pathfinder.PathExecutor
import org.cobalt.pathfinder.state.ExecutorState
import org.cobalt.util.KeybindUtils
import org.cobalt.util.PlayerUtils

class StartFlyState : ExecutorState {

  private var flyStage = 0

  override fun enter() {
    flyStage = 0
  }

  override fun onTick() {
    if (handleFlyStart()) {
      return
    }

    PathExecutor.changeState(PathingState())
  }

  private fun handleFlyStart(): Boolean {
    val keyJump = minecraft.options.keyJump

    if (PlayerUtils.isFlying) {
      flyStage = 0
      return false
    }

    if (PlayerUtils.player!!.onGround()) {
      flyStage = 0
    }

    KeybindUtils.stopMovement(keyJump)

    when (flyStage) {
      0 -> {
        KeybindUtils.setKeyState(keyJump, true)
        flyStage = 1
      }

      1 -> {
        KeybindUtils.setKeyState(keyJump, false)
        flyStage = 2
      }

      2 -> {
        KeybindUtils.setKeyState(keyJump, true)
        flyStage = 3
      }

      3 -> {
        KeybindUtils.setKeyState(keyJump, false)
      }
    }

    return true
  }

}
