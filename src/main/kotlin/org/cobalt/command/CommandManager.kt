package org.cobalt.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.ChatFormatting
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import org.cobalt.Cobalt.minecraft
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.ChatSendEvent
import org.cobalt.util.ChatUtils

object CommandManager {

  /**
   * Command dispatcher used for registering and executing custom commands.
   */
  @JvmStatic
  internal val dispatcher = CommandDispatcher<ClientSuggestionProvider>()

  /**
   * Prefix for custom commands.
   */
  internal const val PREFIX: Char = '.'

  init {
    EventBus.register(this)
  }

  /**
   * Registers a command with the dispatcher.
   *
   * @param command the command instance to register
   */
  @JvmStatic
  fun register(command: Command) {
    command.build().forEach { dispatcher.register(it) }
  }

  @Suppress("UndocumentedPublicFunction")
  @SubscribeEvent
  fun handleCommandExecution(@Suppress("UnusedParameter") event: ChatSendEvent) {
    val content = event.message

    if (!content.startsWith(PREFIX)) {
      return
    }

    val player = minecraft.player ?: return
    val commandLine = content.removePrefix(PREFIX.toString()).trim()

    if (commandLine.isEmpty()) {
      return
    }

    try {
      dispatcher.execute(commandLine, player.connection.suggestionsProvider)
    } catch (exception: CommandSyntaxException) {
      ChatUtils.sendSystemMessage("${ChatFormatting.RED}${exception.message}")
    }

    event.setCancelled(true)
  }

}
