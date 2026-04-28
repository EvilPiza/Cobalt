package org.cobalt.event.impl

import org.cobalt.event.Event

abstract class TickEvent : Event() {

  class Start : TickEvent()
  class End : TickEvent()

}
