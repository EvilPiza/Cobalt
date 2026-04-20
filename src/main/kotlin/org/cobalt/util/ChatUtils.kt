package org.cobalt.util

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import org.cobalt.Cobalt
import org.cobalt.Cobalt.minecraft
import org.slf4j.LoggerFactory

/**
 * Utility for sending chat and system messages.
 */
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

  /**
   * Sends a system message to the local player with optional formatting.
   *
   * @param message the message content to display
   * @param type determines how the message is formatted (prefix, debug, or raw)
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

  /**
   * Converts a plain string into a Minecraft chat component.
   *
   * @param string the raw text to convert
   * @return a mutable chat component representing the input string
   */
  @JvmStatic
  fun stringToComponent(string: String): MutableComponent {
    return Component.literal(string)
  }

  /**
   * Sends a chat message as the player.
   *
   * @param message the message to send in chat
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

  /**
   * Sends a command as the player to the server.
   *
   * @param command the command string without the leading slash
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

/**
 * Type of message formatting used by [ChatUtils].
 *
 * Determines whether a prefix is applied or if the message is sent raw.
 */
enum class MessageType {

  /** Default message includes the mod prefix. */
  DEFAULT,

  /** Raw messages are sent without any prefix. */
  RAW,

  /**
   * Debug message includes the debug prefix.
   *
   * Additionally, duplicate consecutive messages are ignored to prevent spam.
   * If the same message is sent twice in a row, it will not be re-displayed.
   */
  DEBUG

}
