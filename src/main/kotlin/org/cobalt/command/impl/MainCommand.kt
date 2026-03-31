package org.cobalt.command.impl

import net.minecraft.client.Minecraft
import org.cobalt.command.Command
import org.cobalt.command.annotation.DefaultHandler
import org.cobalt.ui.screen.ConfigScreen
import org.cobalt.util.ChatUtils
import org.cobalt.util.helper.TickScheduler

internal object MainCommand : Command(
  name = "cobalt",
  aliases = listOf("cb")
) {

  private val minecraft: Minecraft
    = Minecraft.getInstance()

  @DefaultHandler
  fun main() {
    TickScheduler.schedule(1) {
      minecraft.setScreen(ConfigScreen)
    }
  }

}
