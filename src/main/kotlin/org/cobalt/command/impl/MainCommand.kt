package org.cobalt.command.impl

import org.cobalt.Cobalt.minecraft
import org.cobalt.command.Command
import org.cobalt.command.annotation.DefaultHandler
import org.cobalt.command.annotation.SubCommand
import org.cobalt.ui.screen.ConfigScreen
import org.cobalt.ui.screen.HudEditorScreen
import org.cobalt.util.helper.TickScheduler
import org.cobalt.util.rotation.DefaultRotations
import org.cobalt.util.rotation.RotationManager

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
  fun rotate(yaw: Double, pitch: Double) {
    RotationManager.setActiveRotation(
      DefaultRotations,
      yaw = yaw,
      pitch = pitch
    )
  }

}
