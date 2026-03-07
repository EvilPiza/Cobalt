package org.cobalt.command.impl

import org.cobalt.command.Command
import org.cobalt.command.annotation.DefaultHandler

object MainCommand : Command(
  name = "cobalt",
  aliases = listOf("cb")
) {

  @DefaultHandler
  fun main() {
    println("Hello World!")
  }

}
