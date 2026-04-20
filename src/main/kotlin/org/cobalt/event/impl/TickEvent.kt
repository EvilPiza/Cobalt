package org.cobalt.event.impl

import org.cobalt.event.Event

/**
 * Base event for client tick lifecycle events.
 */
abstract class TickEvent : Event() {

  /**
   * Custom event fired at the start of the client tick.
   */
  class Start : TickEvent()

  /**
   * Custom event fired at the end of the client tick.
   */
  class End : TickEvent()
}
