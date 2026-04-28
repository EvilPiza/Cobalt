package org.cobalt.event.impl

import net.minecraft.network.protocol.Packet
import org.cobalt.event.Event

abstract class PacketEvent(
  val packet: Packet<*>,
) : Event.Cancellable() {

  class Send(packet: Packet<*>) : PacketEvent(packet)
  class Receive(packet: Packet<*>) : PacketEvent(packet)

}
