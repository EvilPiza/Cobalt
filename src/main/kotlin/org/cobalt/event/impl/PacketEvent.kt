package org.cobalt.event.impl

import net.minecraft.network.protocol.Packet
import org.cobalt.event.Event

abstract class PacketEvent(
  private val packet: Packet<*>
) : Event.Cancellable() {

  class Send(packet: Packet<*>) : PacketEvent(packet)
  class Receive(packet: Packet<*>) : PacketEvent(packet)

  fun getPacket(): Packet<*> {
    return packet
  }

}
