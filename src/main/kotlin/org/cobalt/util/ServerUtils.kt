package org.cobalt.util

import net.minecraft.network.protocol.game.ClientboundSetTimePacket
import org.cobalt.Cobalt.minecraft
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.PacketEvent

object ServerUtils {

  private var lastTickTime = -1L

  @JvmStatic
  var averageTps = 20.0
    private set

  @JvmStatic
  val currentPing
    get() = minecraft.player?.playerInfo?.latency ?: 0

  init {
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onPacketReceive(@Suppress("UnusedParameter") event: PacketEvent.Receive) {
    if (event.packet !is ClientboundSetTimePacket) {
      return
    }

    averageTps = (20000.0 / (System.currentTimeMillis() - lastTickTime + 1))
      .coerceIn(0.0, 20.0)

    lastTickTime = System.currentTimeMillis()
  }

}
