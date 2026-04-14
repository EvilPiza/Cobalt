package org.cobalt.util

import net.minecraft.network.protocol.game.ClientboundSetTimePacket
import org.cobalt.Cobalt.minecraft
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.PacketEvent
import org.cobalt.mixin.client.AbstractClientPlayerAccessor

object ServerUtils {
  private var lastTickTime = 0L

  var averageTps = 20f
    private set

  val currentPing
    get() = (minecraft.player as AbstractClientPlayerAccessor).clientPlayerInfo?.latency ?: 0

  init {
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onPacketReceive(event: PacketEvent.Receive) {
    if (event.packet is ClientboundSetTimePacket) {
      val now = System.currentTimeMillis()

      if (lastTickTime == 0L) {
        lastTickTime = now
        return
      }

      val delta = now - lastTickTime
      lastTickTime = now

      if (delta <= 0) return

      val tps = (20000.0 / delta).coerceIn(0.0, 20.0)
      averageTps = (averageTps * 0.95 + tps * 0.05).toFloat()
    }
  }

}
