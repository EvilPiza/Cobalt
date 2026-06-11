package org.cobalt.command.impl

import kotlin.time.Duration.Companion.seconds
import org.cobalt.Cobalt.minecraft
import org.cobalt.command.Command
import org.cobalt.command.annotation.DefaultHandler
import org.cobalt.command.annotation.SubCommand
import org.cobalt.ui.notification.NotificationManager
import org.cobalt.ui.screen.ConfigScreen
import org.cobalt.ui.screen.HudEditorScreen
import org.cobalt.util.helper.TickScheduler
import net.minecraft.core.BlockPos
import org.cobalt.pathfinding.theta.ThetaAsyncWorker
import org.cobalt.pathfinding.theta.ThetaManager
import org.cobalt.util.MouseUtils

internal object MainCommand : Command(name = "cobalt", aliases = listOf("cb")) {

  private const val DELAY_TICKS = 1L

  @DefaultHandler
  fun main() {
    TickScheduler.schedule(DELAY_TICKS) {
      minecraft.setScreen(ConfigScreen)
    }
  }

  @SubCommand
  fun hud() {
    TickScheduler.schedule(DELAY_TICKS) {
      minecraft.setScreen(HudEditorScreen)
    }
  }

  @SubCommand
  fun notification(title: String, description: String, duration: Int) {
    NotificationManager.queue(title, description, duration.seconds)
  }

  @SubCommand
  fun theta(x: Int, y: Int, z: Int) {
    val result = ThetaManager.pathfind(
      BlockPos(x, y, z)
    )

    if (result == null) {
      NotificationManager.queue(
        "Theta*",
        "Failed to create path",
        5.seconds,
      )

      return
    }

    val metrics = result.metrics

    NotificationManager.queue(
      "Theta* Success",
      "Nodes=${metrics.expandedNodes} LOS=${metrics.lineOfSightChecks} Length=${metrics.pathLength} Time=${"%.2f".format(metrics.elapsedMs)}ms",
      8.seconds,
    )
  }

  @SubCommand
  fun thetaclear() {

    ThetaManager.clear()

    NotificationManager.queue(
      "Theta*",
      "Cleared rendered path",
      5.seconds,
    )
  }

  @SubCommand
  fun pathasync(x: Int, y: Int, z: Int) {

    val goal = BlockPos(x, y, z)

    ThetaAsyncWorker.requestPath(goal) { result ->

      if (result == null) {
        NotificationManager.queue(
          "Theta*",
          "Path failed",
          3.seconds
        )
        return@requestPath
      }

      ThetaManager.applyExternalPath(result)

      NotificationManager.queue(
        "Theta* Async",
        "Nodes=${result.metrics.expandedNodes} Time=${"%.2f".format(result.metrics.elapsedMs)}ms",
        6.seconds
      )
    }
  }
  //Somehow fucks everything, idk
  @SubCommand
  fun repath(x: Int, y: Int, z: Int) {

    ThetaAsyncWorker.requestPath(BlockPos(x, y, z)) { result ->
      if (result != null) {
        ThetaManager.applyExternalPath(result)
      }
    }
  }

}
