package org.cobalt.event.impl

import net.minecraft.network.protocol.Packet
import org.cobalt.event.Event

/** Base event for network packet send/receive operations; cancellable. */
abstract class PacketEvent(
  /** The network packet associated with the event. */
  val packet: Packet<*>,
) : Event.Cancellable() {

  /** Event fired when a packet is sent from the client. */
  class Send(packet: Packet<*>) : PacketEvent(packet)

  /** Event fired when a packet is received by the client. */
  class Receive(packet: Packet<*>) : PacketEvent(packet)

}
