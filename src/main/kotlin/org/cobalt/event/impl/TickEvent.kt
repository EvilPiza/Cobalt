package org.cobalt.event.impl

import org.cobalt.event.Event

/** Events that represent client tick boundaries. */
abstract class TickEvent : Event() {

  /** Event fired at the start of the client tick. */
  class Start : TickEvent()

  /** Event fired at the end of the client tick. */
  class End : TickEvent()

}
