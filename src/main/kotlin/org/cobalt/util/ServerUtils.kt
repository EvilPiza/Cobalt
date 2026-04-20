package org.cobalt.util

import net.minecraft.network.protocol.game.ClientboundSetTimePacket
import org.cobalt.Cobalt.minecraft
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.PacketEvent
import org.cobalt.mixin.client.AbstractClientPlayerAccessor

private const val DEFAULT_TPS = 20f
private const val TICKS_PER_SECOND = 20.0
private const val MS_PER_SECOND = 1000.0
private const val TPS_SMOOTHING = 0.05f

object ServerUtils {

  private var lastTickTime = -1L

  /**
   * Average of the server's ticks per second (TPS).
   */
  var averageTps = DEFAULT_TPS
    private set

  /**
   * Current network latency to the server in milliseconds.
   *
   * @return player's ping value, or 0 if unavailable
   */
  val currentPing
    get() = (minecraft.player as AbstractClientPlayerAccessor)
      .clientPlayerInfo?.latency ?: 0

  init {
    EventBus.register(this)
  }

  @Suppress("UndocumentedPublicFunction")
  @SubscribeEvent
  fun onPacketReceive(@Suppress("UnusedParameter") event: PacketEvent.Receive) {
    if (event.packet !is ClientboundSetTimePacket) return

    val now = System.currentTimeMillis()

    val last = lastTickTime
    lastTickTime = now

    if (last == -1L) return

    val delta = now - last
    if (delta <= 0) return

    val tps = (MS_PER_SECOND * TICKS_PER_SECOND / delta)
      .coerceAtMost(TICKS_PER_SECOND)
      .toFloat()

    averageTps =
      averageTps * (1 - TPS_SMOOTHING) +
        tps * TPS_SMOOTHING
  }

}
