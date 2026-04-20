package org.cobalt.event.impl

import org.cobalt.event.Event

/**
 * Custom event fired when the player sends a chat message.
 *
 * This event extends [Event.Cancellable] and can be canceled to prevent
 * the message from being sent.
 *
 * @property message the message being sent
 */
class ChatSendEvent(val message: String) : Event.Cancellable()
