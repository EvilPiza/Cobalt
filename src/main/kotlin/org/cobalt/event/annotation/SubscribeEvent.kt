package org.cobalt.event.annotation

import org.cobalt.event.Event

/**
 * Marks a function as an event subscriber.
 *
 * @param ignoreCancelled whether to receive canceled events
 * @param priority subscriber execution priority
 * @param once if true, subscriber is removed after first call
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SubscribeEvent(
  val ignoreCancelled: Boolean = false,
  val priority: Event.Priority = Event.Priority.MEDIUM,
  val once: Boolean = false,
)
