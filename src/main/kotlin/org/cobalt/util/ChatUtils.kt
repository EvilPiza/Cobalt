package org.cobalt.util

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import org.cobalt.Cobalt
import org.cobalt.Cobalt.minecraft
import org.slf4j.LoggerFactory

  /** Utilities for sending chat/system messages and building message components. */
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

  /** Send a system chat message to the player using the configured message prefix.
   *
   * @param message the plain text message to send
   * @param type the MessageType that controls prefixing/formatting
   */
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

  /** Convert a plain string into a MutableComponent. */
  @JvmStatic
  fun stringToComponent(string: String): MutableComponent {
    return Component.literal(string)
  }

  /** Send a raw chat message as though typed by the player. */
  @JvmStatic
  fun sendChatMessage(message: String) {
    val player = minecraft.player

    if (player == null) {
      logger.error("Attempted to send message ($message) but mc.player is null")
      return
    }

    player.connection.sendChat(message)
  }

  /** Send a client-side command string to the server. */
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

/** Type of message to send via ChatUtils. */
enum class MessageType {
  /** Default message includes the mod prefix. */
  DEFAULT,
  /** Debug messages include the debug prefix. */
  DEBUG,
  /** Raw messages are sent without any prefix. */
  RAW
}
