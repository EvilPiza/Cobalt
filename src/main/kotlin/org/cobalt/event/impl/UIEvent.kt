package org.cobalt.event.impl

import org.cobalt.event.Event

abstract class UIEvent : Event() {
  class NanoVG() : UIEvent()
}
