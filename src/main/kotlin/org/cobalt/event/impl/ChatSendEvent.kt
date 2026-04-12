package org.cobalt.event.impl

import org.cobalt.event.Event

/** Event fired when the player sends a chat message; cancellable. */
class ChatSendEvent(
  /** The raw chat message content that is being sent. */
  val message: String
) : Event.Cancellable()
