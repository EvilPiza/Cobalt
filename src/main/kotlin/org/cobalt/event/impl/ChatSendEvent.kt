package org.cobalt.event.impl

import org.cobalt.event.Event

class ChatSendEvent(val message: String) : Event.Cancellable()
