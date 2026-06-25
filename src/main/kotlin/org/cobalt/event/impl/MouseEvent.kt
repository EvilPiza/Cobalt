package org.cobalt.event.impl

import org.cobalt.event.Event
import org.cobalt.util.MouseAction
import org.cobalt.util.MouseButton

class MouseEvent(
  val button: MouseButton,
  val action: MouseAction,
) : Event.Cancellable()
