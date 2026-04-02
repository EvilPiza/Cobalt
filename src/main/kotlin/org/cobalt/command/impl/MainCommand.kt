package org.cobalt.command.impl

import org.cobalt.Cobalt.minecraft
import org.cobalt.command.Command
import org.cobalt.command.annotation.DefaultHandler
import org.cobalt.command.annotation.SubCommand
import org.cobalt.ui.screen.ConfigScreen
import org.cobalt.util.helper.TickScheduler
import org.cobalt.util.rotation.DefaultRotations
import org.cobalt.util.rotation.RotationManager

internal object MainCommand : Command(name = "cobalt") {

  @DefaultHandler
  fun main() {
    TickScheduler.schedule(1) {
      minecraft.setScreen(ConfigScreen)
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
