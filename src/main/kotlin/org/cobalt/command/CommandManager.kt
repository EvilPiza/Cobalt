package org.cobalt.command

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.ChatFormatting
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import org.cobalt.Cobalt.minecraft
import org.cobalt.util.ChatUtils
import org.slf4j.LoggerFactory


object CommandManager {

  private val logger = LoggerFactory.getLogger(this::class.java)

  @JvmStatic
  val dispatcher = CommandDispatcher<ClientSuggestionProvider>()

  @JvmStatic
  val prefix: Char = '.'

  @JvmStatic
  fun register(command: Command) {
    dispatcher.register(command.build())
  }

  @JvmStatic
  fun handleCommandExecution(content: String) {
    val player = minecraft.player ?: return
    val commandLine = content.removePrefix(prefix.toString()).trim()

    if (commandLine.isEmpty()) {
      return
    }

    try {
      dispatcher.execute(commandLine, player.connection.suggestionsProvider)
    } catch (exception: Exception) {
      logger.error("Error while executing command: $commandLine", exception)
      ChatUtils.sendMessage("${ChatFormatting.RED}Something went wrong when executing the command")
    }
  }

}
