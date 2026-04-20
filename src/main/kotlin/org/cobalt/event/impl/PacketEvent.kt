package org.cobalt.event.impl

import net.minecraft.network.protocol.Packet
import org.cobalt.event.Event

/**
 * Base type for all packet-related events.
 *
 * @property packet the packet being sent or received
 */
abstract class PacketEvent(
  val packet: Packet<*>,
) : Event.Cancellable() {

  /**
   * Custom event fired when a packet is sent to the server.
   *
   * This event is cancellable via [Event.Cancellable], which prevents the
   * packet from being sent.
   *
   * @property packet the packet being sent
   */
  class Send(packet: Packet<*>) : PacketEvent(packet)

  /**
   * Custom event fired when a packet is received from the server.
   *
   * This event is cancellable via [Event.Cancellable], which prevents the
   * packet from being processed.
   *
   * @property packet the packet being received
   */
  class Receive(packet: Packet<*>) : PacketEvent(packet)

}
