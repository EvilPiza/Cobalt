package org.cobalt.command.impl

import java.awt.Color
import net.minecraft.world.phys.Vec3
import org.cobalt.Cobalt.minecraft
import org.cobalt.command.Command
import org.cobalt.command.annotation.DefaultHandler
import org.cobalt.command.annotation.SubCommand
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.WorldRenderEvent
import org.cobalt.pathfinder.calculate.AStarPathfinder
import org.cobalt.pathfinder.calculate.Path
import org.cobalt.pathfinder.calculate.PathMode
import org.cobalt.pathfinder.goal.GoalBlock
import org.cobalt.pathfinder.movement.CalculationContext
import org.cobalt.pathfinder.movement.MovementHelper
import org.cobalt.ui.screen.ConfigScreen
import org.cobalt.ui.screen.HudEditorScreen
import org.cobalt.util.ChatUtils
import org.cobalt.util.PlayerUtils
import org.cobalt.util.WorldRenderUtils
import org.cobalt.util.helper.TickScheduler

object MainCommand : Command(name = "cobalt", aliases = listOf("cb")) {

  private const val DELAY_TICKS = 1L

  @DefaultHandler
  fun main() {
    TickScheduler.schedule(DELAY_TICKS) {
      minecraft.gui.setScreen(ConfigScreen)
    }
  }

  @SubCommand
  fun hud() {
    TickScheduler.schedule(DELAY_TICKS) {
      minecraft.gui.setScreen(HudEditorScreen)
    }
  }

  private var path: Path? = null

  init {
    EventBus.register(this)
  }

  @SubCommand
  fun walk(x: Int, y: Int, z: Int) {
    pathfind(PathMode.WALK, x, y, z)
  }

  @SubCommand
  fun fly(x: Int, y: Int, z: Int) {
    pathfind(PathMode.FLY, x, y, z)
  }

  @SubCommand
  fun test(pass: Boolean) {
    val ctx = CalculationContext()
    val playerPos = PlayerUtils.position

    val pos = if (pass) {
      playerPos
    } else {
      playerPos.atY(playerPos.y - 1)
    }

    val bool = if (pass) {
      MovementHelper.canPassThrough(ctx, pos.x, pos.y, pos.z)
    } else {
      MovementHelper.canStandOn(ctx, pos.x, pos.y, pos.z)
    }

    ChatUtils.sendSystemMessage("$bool")
  }

  private fun pathfind(mode: PathMode, x: Int, y: Int, z: Int) {
    val playerPos = PlayerUtils.position

    val pathfinder = AStarPathfinder(
      playerPos.x, playerPos.y, playerPos.z,
      GoalBlock(x, y, z), mode.movements
    )

    path = pathfinder.findPath()

    if (path != null) {
      ChatUtils.sendSystemMessage(
        "Path found with ${path!!.nodes.size} nodes in ${path!!.timeElapsed.inWholeMilliseconds} ms"
      )
    } else {
      ChatUtils.sendSystemMessage("No path found")
    }
  }

  @SubscribeEvent
  fun onRender(ignored: WorldRenderEvent) {
    path?.nodes?.let { path ->
      for (index in 1 until path.size) {
        val prev = path[index - 1]
        val current = path[index]

        WorldRenderUtils.drawLine(
          Vec3(prev.x + 0.5, prev.y + 0.5, prev.z + 0.5),
          Vec3(current.x + 0.5, current.y + 0.5, current.z + 0.5),
          Color.WHITE, true
        )
      }
    }
  }

}
