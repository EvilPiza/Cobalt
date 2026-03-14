package org.cobalt.command.impl

import org.cobalt.command.Command
import org.cobalt.command.annotation.DefaultHandler
import org.cobalt.ui.screen.ConfigScreen

internal object MainCommand : Command(
  name = "cobalt",
  aliases = listOf("cb")
) {

  @DefaultHandler
  fun main() {
    ConfigScreen.openScreen()
  }

}
