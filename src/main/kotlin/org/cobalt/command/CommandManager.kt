package org.cobalt.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.ChatFormatting
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import org.cobalt.Cobalt.minecraft
import org.cobalt.command.impl.MainCommand
import org.cobalt.util.ChatUtils

object CommandManager {

  const val PREFIX: Char = '.'

  @JvmStatic
  internal val dispatcher = CommandDispatcher<ClientSuggestionProvider>()

  private val commands = mutableListOf<Command>()

  @JvmStatic
  internal fun registerCommands() {
    val builtIn = arrayOf(
      MainCommand
    )

    builtIn.forEach { command ->
      registerCommand(command)
    }

    registerSlashCommands()
  }

  @JvmStatic
  fun registerCommand(command: Command) {
    val ids = (listOf(command.name) + command.aliases)
      .map { it.trim().lowercase() }
      .filter { it.isNotEmpty() }

    require(ids.isNotEmpty()) {
      "Command must have a non-empty name or alias"
    }

    require(ids.distinct().size == ids.size) {
      "Command '${command.name}' contains duplicate aliases"
    }

    val registeredIds = commands
      .flatMap { listOf(it.name) + it.aliases }
      .mapTo(mutableSetOf()) { it.trim().lowercase() }

    val duplicate = ids.firstOrNull { it in registeredIds }
    require(duplicate == null) {
      "Command id '$duplicate' is already registered"
    }

    command.build<ClientSuggestionProvider>().forEach { dispatcher.register(it) }
    commands.add(command)
  }

  @JvmStatic
  internal fun handleCommandExecution(content: String): Boolean {
    if (!content.startsWith(PREFIX)) {
      return false
    }

    val player = minecraft.player ?: return false
    val commandLine = content.removePrefix(PREFIX.toString()).trim()

    if (commandLine.isEmpty()) {
      return false
    }

    try {
      dispatcher.execute(commandLine, player.connection.suggestionsProvider)
      minecraft.gui.hud.chat.commandHistory.addCommand(content)
    } catch (exception: CommandSyntaxException) {
      ChatUtils.sendSystemMessage("${ChatFormatting.RED}${exception.message}")
    }

    return true
  }

  private fun registerSlashCommands() {
    ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
      registerSlashCommandNodes(dispatcher)
    }
  }

  private fun registerSlashCommandNodes(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
    commands.forEach { command ->
      command.build<FabricClientCommandSource>().forEach { dispatcher.register(it) }
    }
  }

}
