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

  internal const val PREFIX: Char = '.'

  @JvmStatic
  internal val dispatcher = CommandDispatcher<ClientSuggestionProvider>()

  init {
    EventBus.register(this)
  }

  @JvmStatic
  fun register(command: Command) {
    command.build().forEach { dispatcher.register(it) }
  }

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
      minecraft.commandHistory().addCommand(content)
    } catch (exception: CommandSyntaxException) {
      ChatUtils.sendSystemMessage("${ChatFormatting.RED}${exception.message}")
    }

    event.setCancelled(true)
  }

}
