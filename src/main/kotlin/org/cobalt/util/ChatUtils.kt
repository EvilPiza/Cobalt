package org.cobalt.util

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import org.cobalt.Cobalt.mc

enum class MessageType {
  COBALT,
  DEBUG,
  RAW
}

object ChatUtils {
  @JvmField
  val CobaltPrefix = Component.literal("")
    .append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY))
    .append(ColorUtils.buildTextGradient("Cobalt", 0x4CADD0, 0xB2F9FF))
    .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY))

  @JvmField
  val CobaltDebugPrefix = Component.literal("")
    .append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY))
    .append(ColorUtils.buildTextGradient("Cobalt Debug", 0x369876, 0x71FF9E))
    .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY))

  @JvmStatic
  fun message(message: String, type: MessageType = MessageType.COBALT) {
    val mcplayer = mc.player ?: run {
      println("[Cobalt] Attempted to send message ($message) but mc.player is null")
      return
    }

    val component = when (type) {
      MessageType.RAW -> {
        stringToComponent(message)
      }

      MessageType.DEBUG -> {
        CobaltDebugPrefix.copy()
          .append(stringToComponent(message))
      }

      MessageType.COBALT -> {
        CobaltPrefix.copy()
          .append(stringToComponent(message))
      }
    }

    mcplayer.sendSystemMessage(component)
  }

  @JvmStatic
  fun stringToComponent(string: String): MutableComponent {
    return Component.literal(string)
  }

  @JvmStatic
  fun say(message: String) {
    if (mc.player == null) return println("[Cobalt] Attempted to send message ($message), but mc.player is null")
    mc.player!!.connection.sendChat(message)
  }

  @JvmStatic
  fun sendCommand(command: String) {
    if (mc.player == null) return println("[Cobalt] Attempted to send message ($command), but mc.player is null")
    mc.player!!.connection.sendCommand(command)
  }
}
