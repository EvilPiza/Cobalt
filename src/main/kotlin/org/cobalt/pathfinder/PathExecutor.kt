package org.cobalt.pathfinder

import net.minecraft.ChatFormatting
import org.cobalt.Cobalt.minecraft
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.TickEvent
import org.cobalt.event.impl.WorldRenderEvent
import org.cobalt.pathfinder.calculate.Path
import org.cobalt.pathfinder.state.ExecutorState
import org.cobalt.pathfinder.state.impl.CalculatingState
import org.cobalt.ui.theme.ThemeManager
import org.cobalt.util.ChatUtils
import org.cobalt.util.KeybindUtils
import org.cobalt.util.MessageType
import org.cobalt.util.PlayerUtils
import org.cobalt.util.WorldRenderUtils

object PathExecutor {

  var state: ExecutorState? = null
  var config: PathConfig? = null

  var path: Path? = null
  var pathIndex: Int = 0

  var running = false

  init {
    EventBus.register(this)
  }

  fun goTo(config: PathConfig) {
    stop()

    if (config.useFlyMovement && !PlayerUtils.canFly) {
      ChatUtils.sendSystemMessage("${ChatFormatting.RED} Invalid path config, since player cannot fly!")
      return
    }

    this.config = config
    this.running = true

    changeState(CalculatingState())
  }

  fun stop() {
    state?.exit()
    state = null

    KeybindUtils.stopMovement()

    running = false
    config = null

    path = null
    pathIndex = 0
  }

  fun changeState(newState: ExecutorState) {
    state?.exit()
    state = newState
    state?.enter()

    ChatUtils.sendSystemMessage(
      "Entering ${newState.javaClass.simpleName} Executor State",
      MessageType.DEBUG
    )
  }

  @SubscribeEvent
  fun onTick(ignored: TickEvent.Start) {
    if (minecraft.level == null || minecraft.player == null) {
      stop()
      return
    }

    if (minecraft.gui.screen() != null) {
      return
    }

    state?.onTick()
  }

  @SubscribeEvent
  fun onRender(ignored: WorldRenderEvent) {
    if (state is CalculatingState) {
      return
    }

    val theme = ThemeManager.activeTheme
    val nodes = path?.keyNodes ?: return

    val targetNode = nodes[pathIndex].blockPos
    val playerPos = PlayerUtils.position

    WorldRenderUtils.drawBlockPos(
      playerPos,
      color = theme.success
    )

    WorldRenderUtils.drawBlockPos(
      targetNode,
      color = theme.error
    )

    for (index in nodes.indices) {
      val node = nodes[index]

      if (node.blockPos !in listOf(targetNode, playerPos)) {
        WorldRenderUtils.drawBlockPos(
          node.blockPos,
          color = theme.accentPrimary
        )
      }

      if (index > 0) {
        val prev = nodes[index - 1]

        WorldRenderUtils.drawLine(
          prev.centerVec, node.centerVec,
          theme.accentSecondary
        )
      }
    }
  }

}
