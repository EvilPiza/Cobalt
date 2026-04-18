package org.cobalt.command

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.ChatFormatting
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import org.cobalt.Cobalt.minecraft
import org.cobalt.util.ChatUtils
import com.mojang.brigadier.exceptions.CommandSyntaxException

/** Central command dispatcher and helpers for registering and executing chat commands. */
object CommandManager {

  /** Brigadier dispatcher used to register command trees. */
  @JvmStatic
  val dispatcher = CommandDispatcher<ClientSuggestionProvider>()

  /** Character prefix used to identify chat commands. */
  @JvmStatic
  val prefix: Char = '.'

  /** Register a top-level command into the Brigadier dispatcher. */
  @JvmStatic
  fun register(command: Command) {
    command.build().forEach { dispatcher.register(it) }
  }

  /** Execute a command line string as if entered by the player; logs and notifies on failure. */
  @JvmStatic
  fun handleCommandExecution(content: String) {
    val player = minecraft.player ?: return
    val commandLine = content.removePrefix(prefix.toString()).trim()

    if (commandLine.isEmpty()) { return }

    try {
      dispatcher.execute(commandLine, player.connection.suggestionsProvider)
    } catch (exception: CommandSyntaxException) {
      ChatUtils.sendSystemMessage("${ChatFormatting.RED}${exception.message}")
    }
  }
}
