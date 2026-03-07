package org.cobalt.event

abstract class Event {

  abstract class Cancellable : Event() {

    private var cancelled = false

    fun isCancelled(): Boolean {
      return cancelled
    }

    fun setCancelled(cancelled: Boolean) {
      this.cancelled = cancelled
    }

  }

  enum class Priority {

    HIGHEST,
    HIGH,
    MEDIUM,
    LOW,
    LOWEST;

    fun weight(): Int {
      return ordinal
    }

  }

}
