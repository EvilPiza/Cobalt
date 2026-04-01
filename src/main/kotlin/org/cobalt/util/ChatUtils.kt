package org.cobalt.util

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import org.cobalt.Cobalt
import org.cobalt.Cobalt.minecraft
import org.slf4j.LoggerFactory

object ChatUtils {

  private val defaultPrefix = Component.literal("")
    .append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY))
    .append(ColorUtils.buildTextGradient(Cobalt.MOD_NAME, 0x4CADD0, 0xB2F9FF))
    .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY))

  private val debugPrefix = Component.literal("")
    .append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY))
    .append(ColorUtils.buildTextGradient("${Cobalt.MOD_NAME} Debug", 0x369876, 0x71FF9E))
    .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY))

  private val logger = LoggerFactory.getLogger(this::class.java)

  @JvmStatic
  fun sendMessage(message: String, type: MessageType = MessageType.DEFAULT) {
    val player = minecraft.player

    if (player == null) {
      logger.error("Attempted to send message ($message) but mc.player is null")
      return
    }

    val component = when (type) {
      MessageType.DEFAULT -> defaultPrefix.copy().append(stringToComponent(message))
      MessageType.DEBUG -> debugPrefix.copy().append(stringToComponent(message))
      MessageType.RAW -> stringToComponent(message)
    }

    player.sendSystemMessage(component)
  }

  @JvmStatic
  fun stringToComponent(string: String): MutableComponent {
    return Component.literal(string)
  }

  @JvmStatic
  fun sendChatMessage(message: String) {
    val player = minecraft.player

    if (player == null) {
      logger.error("Attempted to send message ($message) but mc.player is null")
      return
    }

    player.connection.sendChat(message)
  }

  @JvmStatic
  fun sendCommand(command: String) {
    val player = minecraft.player

    if (player == null) {
      logger.error("Attempted to send command ($command) but mc.player is null")
      return
    }

    player.connection.sendCommand(command)
  }

}

enum class MessageType {
  DEFAULT,
  DEBUG,
  RAW
}
