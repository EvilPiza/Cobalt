package org.cobalt.event

/** Base event type used by the module event system. */
abstract class Event {

  /** Base class for cancellable events which can be prevented from propagating. */
  abstract class Cancellable : Event() {

    private var cancelled = false

    /** Return true when this event has been cancelled and should not be processed further. */
    fun isCancelled(): Boolean {
      return cancelled
    }

    /** Mark this event as cancelled or not. */
    fun setCancelled(cancelled: Boolean) {
      this.cancelled = cancelled
    }

  }

  /** Event delivery priority used by subscribers to control ordering. */
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

    /** Numeric weight corresponding to the priority's ordinal. */
    fun weight(): Int {
      return ordinal
    }

  }

}
