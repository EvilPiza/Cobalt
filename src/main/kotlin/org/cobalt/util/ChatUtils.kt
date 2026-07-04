package org.cobalt.util

import net.minecraft.network.chat.MutableComponent
import org.cobalt.Cobalt
import org.cobalt.Cobalt.minecraft
import org.cobalt.Cobalt.runOnClientThread
import org.cobalt.module.impl.misc.Debug
import org.cobalt.util.helper.ChatFormatter
import org.slf4j.LoggerFactory

object ChatUtils {

  private val logger = LoggerFactory.getLogger(this::class.java)

  private val defaultPrefix =
    "<dark_gray>[</dark_gray><gradient:#4CADD0:#B2F9FF>${Cobalt.MOD_NAME}</gradient><dark_gray>] </dark_gray><reset>"
  private val debugPrefix =
    "<dark_gray>[</dark_gray><gradient:#369876:#71FF9E>${Cobalt.MOD_NAME} Debug</gradient><dark_gray>] </dark_gray><reset>"

  private var lastDebugMessage: String? = null

  @JvmStatic
  fun sendSystemMessage(message: String, type: MessageType = MessageType.DEFAULT) {
    val component = when (type) {
      MessageType.DEFAULT -> stringToComponent(defaultPrefix + message)
      MessageType.RAW -> stringToComponent(message)
      MessageType.DEBUG -> {
        if (!Debug.enabled || lastDebugMessage == message) {
          return
        }

        lastDebugMessage = message
        stringToComponent(debugPrefix + message)
      }
    }

    addToChat(component)
  }

  @JvmStatic
  fun stringToComponent(string: String): MutableComponent {
    return ChatFormatter.parse(string)
  }

  @JvmStatic
  fun sendPlayerMessage(message: String) {
    runOnClientThread {
      val player = minecraft.player

      if (player == null) {
        logger.error("Attempted to send message as player ($message) but mc.player is null")
        return@runOnClientThread
      }

      player.connection.sendChat(message)
    }
  }

  @JvmStatic
  fun sendCommand(command: String) {
    runOnClientThread {
      val player = minecraft.player

      if (player == null) {
        logger.error("Attempted to send command ($command) but mc.player is null")
        return@runOnClientThread
      }

      player.connection.sendCommand(command)
    }
  }

  private fun addToChat(component: MutableComponent) {
    runOnClientThread {
      val player = minecraft.player

      if (player == null) {
        logger.error("Attempted to send system message but mc.player is null")
        return@runOnClientThread
      }

      player.sendSystemMessage(component)
    }
  }

}

enum class MessageType {
  DEFAULT,
  RAW,
  DEBUG
}
