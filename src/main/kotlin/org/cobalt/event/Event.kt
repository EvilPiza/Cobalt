package org.cobalt.event

/**
 * Base class for all custom Cobalt events.
 */
abstract class Event {

  /**
   * Base class for events that can be canceled.
   */
  abstract class Cancellable : Event() {

    private var cancelled = false

    /**
     * Returns whether this event has been canceled.
     *
     * @return true if the event is canceled, false otherwise
     */
    fun isCancelled(): Boolean {
      return cancelled
    }

    /**
     * Sets the canceled state of this event.
     *
     * @param cancelled whether the event should be canceled
     */
    fun setCancelled(cancelled: Boolean) {
      this.cancelled = cancelled
    }

  }

  /**
   * Priority levels used to determine event listener execution order.
   */
  enum class Priority {

    /** Highest delivery priority; handlers with this priority run before others. */
    HIGHEST,

    /** High delivery priority; runs after HIGHEST but before MEDIUM. */
    HIGH,

    /** Default delivery priority for handlers. */
    MEDIUM,

    /** Low delivery priority; runs after MEDIUM. */
    LOW,

    /** Lowest delivery priority; runs last. */
    LOWEST;

    /**
     * Numeric weight used for sorting priorities.
     */
    fun weight(): Int = ordinal

  }

}
