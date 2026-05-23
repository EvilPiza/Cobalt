package org.cobalt.event.impl

import org.cobalt.event.Event

class MouseEvent(
  val button: MouseButton,
  val action: MouseAction
) : Event.Cancellable()

enum class MouseButton {
  LEFT, RIGHT, MIDDLE;
}

enum class MouseAction {
  PRESS, RELEASE;
}
