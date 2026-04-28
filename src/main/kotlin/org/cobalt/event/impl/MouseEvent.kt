package org.cobalt.event.impl

import org.cobalt.event.Event

class MouseEvent(val button: Button, val action: Action) : Event.Cancellable() {

  enum class Button {
    LEFT, RIGHT, MIDDLE;
  }

  enum class Action {
    PRESS, RELEASE;
  }

}
