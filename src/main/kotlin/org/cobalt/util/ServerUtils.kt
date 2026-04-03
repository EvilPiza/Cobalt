package org.cobalt.util

import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.network.protocol.game.ClientboundSetTimePacket
import org.cobalt.Cobalt.minecraft
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.PacketEvent
import org.cobalt.mixin.client.AbstractClientPlayerAccessor

object ServerUtils {

  private var lastTickTime = System.currentTimeMillis()

  var averageTps = 20f
    private set

  var averagePing = 0
    private set

  init {
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onPacketReceive(event: PacketEvent.Receive) {
    when (event.packet) {
      is ClientboundSetTimePacket -> {
        val now = System.currentTimeMillis()
        val delta = now - lastTickTime
        lastTickTime = now
        val tps = (20000f / delta).coerceIn(0f, 20f)
        averageTps = averageTps * 0.95f + tps * 0.05f
      }

      is ClientboundPlayerInfoUpdatePacket -> {
        val player = minecraft.player ?: return
        averagePing = (player as AbstractClientPlayerAccessor).clientPlayerInfo?.latency ?: averagePing
      }
    }
  }

}
