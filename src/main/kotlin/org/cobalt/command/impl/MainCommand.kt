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
import org.cobalt.pathfinder.PathConfig
import org.cobalt.pathfinder.PathExecutor
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

  @SubCommand
  fun goTo(x: Int, y: Int, z: Int, fly: Boolean = false) {
    val config = PathConfig(
      goal = GoalBlock(x, y, z),
      mode = if (fly) PathMode.FLY else PathMode.WALK
    )

    PathExecutor.goTo(config)
  }

}
