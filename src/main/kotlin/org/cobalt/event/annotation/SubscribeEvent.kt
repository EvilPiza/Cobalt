package org.cobalt.event.annotation

import org.cobalt.event.Event

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
/** Annotation to mark methods as event subscribers.
 *
 * @param ignoreCancelled if true the subscriber will still receive events that were cancelled
 * @param priority delivery priority for ordering subscribers
 * @param once when true the subscriber will be removed after the first invocation
 */
annotation class SubscribeEvent(
  val ignoreCancelled: Boolean = false,
  val priority: Event.Priority = Event.Priority.MEDIUM,
  val once: Boolean = false
)
