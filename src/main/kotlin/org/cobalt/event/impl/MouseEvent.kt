package org.cobalt.event.impl

import org.cobalt.event.Event

/**
 * Event fired when a mouse button changes state.
 *
 * @property button the mouse button involved in the event
 * @property action the action performed (press or release)
 */
class MouseEvent(val button: Button, val action: Action) : Event.Cancellable() {

  /**
   * Represents the physical mouse buttons.
   */
  enum class Button {

    /**
     * Left mouse button.
     */
    LEFT,

    /**
     * Right mouse button.
     */
    RIGHT,

    /**
     * Middle mouse button (scroll wheel click).
     */
    MIDDLE,

  }

  /**
   * Represents the state change of a mouse button.
   */
  enum class Action {

    /**
     * The button was pressed down.
     */
    PRESS,

    /**
     * The button was released.
     */
    RELEASE,

  }

}
