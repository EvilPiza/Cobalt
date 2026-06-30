package org.cobalt.pathfinder

import org.cobalt.Cobalt.minecraft
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.TickEvent
import org.cobalt.event.impl.WorldRenderEvent
import org.cobalt.pathfinder.calculate.Path
import org.cobalt.pathfinder.state.ExecutorState
import org.cobalt.pathfinder.state.impl.CalculatingState
import org.cobalt.util.ChatUtils
import org.cobalt.util.MessageType

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

    this.config = config
    this.running = true

    changeState(CalculatingState())
  }

  fun stop() {
    state?.exit()
    running = false

    state = null
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
    state?.onRender()
  }

}
