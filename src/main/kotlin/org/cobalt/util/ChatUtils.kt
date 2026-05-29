package org.cobalt.util

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import org.cobalt.Cobalt
import org.cobalt.Cobalt.minecraft
import org.slf4j.LoggerFactory

object ChatUtils {

  private val logger = LoggerFactory.getLogger(this::class.java)

  private val defaultPrefix = Component.literal("")
    .append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY))
    .append(ColorUtils.buildTextGradient(Cobalt.MOD_NAME, 0x4CADD0, 0xB2F9FF))
    .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY))

  private val debugPrefix = Component.literal("")
    .append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY))
    .append(ColorUtils.buildTextGradient("${Cobalt.MOD_NAME} Debug", 0x369876, 0x71FF9E))
    .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY))

  private var lastDebugMessage: String? = null
  /*
  * Adds a message to chat with either the [Cobalt], [Cobalt Debug], or no prefix
  *
  * @param message The string to appear in chat
  * @param type Which message type to send, must be MessageType (.DEFAULT, .RAW or .DEBUG)
   */
  @JvmStatic
  fun sendSystemMessage(message: String, type: MessageType = MessageType.DEFAULT) {
    val player = minecraft.player

    if (player == null) {
      logger.error("Attempted to send system message ($message) but mc.player is null")
      return
    }

    val component = when (type) {
      MessageType.DEFAULT -> defaultPrefix.copy().append(stringToComponent(message))
      MessageType.RAW -> stringToComponent(message)
      MessageType.DEBUG -> {
        if (lastDebugMessage == message) {
          return
        }

        lastDebugMessage = message
        debugPrefix.copy().append(stringToComponent(message))
      }
    }

    player.sendSystemMessage(component)
  }
  /*
  * Converts a string to MC Component
  *
  * @param string The string to convert to a Component
  * @returns MutableComponent the output
   */
  @JvmStatic
  fun stringToComponent(string: String): MutableComponent {
    return Component.literal(string)
  }
  /*
  * Sends message to minecraft chat (to the server)
  * @param message The string you want to send
   */
  @JvmStatic
  fun sendPlayerMessage(message: String) {
    val player = minecraft.player

    if (player == null) {
      logger.error("Attempted to send message as player ($message) but mc.player is null")
      return
    }

    player.connection.sendChat(message)
  }
  /*
  * Send command to the server
  *
  * @param command The command you want to send as a string
   */
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
  RAW,
  DEBUG
}
