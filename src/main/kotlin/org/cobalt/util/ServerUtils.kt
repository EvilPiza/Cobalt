package org.cobalt.util

import net.minecraft.network.protocol.game.ClientboundSetTimePacket
import org.cobalt.Cobalt.minecraft
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.PacketEvent
import org.cobalt.mixin.client.AbstractClientPlayerAccessor

/**
 * Utility object for retrieving and tracking server-side metrics such as
 * average TPS (ticks per second) and the current player ping.
 *
 * The object listens for incoming packets and updates an internal smoothed
 * TPS estimate whenever the server time packet is received.
 */
object ServerUtils {
  private var lastTickTime = 0L

  /**
   * Smoothed average server ticks per second (TPS).
   *
   * This value is updated when a `ClientboundSetTimePacket` is received and is
   * smoothed over time to avoid rapid fluctuations. The setter is private; the
   * value should be read-only from callers.
   */
  var averageTps = 20f
    private set

  /**
   * The current player's network latency (ping) in milliseconds.
   *
   * Obtained from the Minecraft client player info via an accessor mixin.
   * Returns 0 when no player info is available.
   */
  val currentPing
    get() = (minecraft.player as AbstractClientPlayerAccessor).clientPlayerInfo?.latency ?: 0

  init {
    EventBus.register(this)
  }

  /**
   * Event handler for incoming packets. When a `ClientboundSetTimePacket` is
   * received we use its arrival timestamp to estimate the server tick time and
   * update [averageTps] with a small smoothing factor.
   */
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
