package org.cobalt.util.helper

class Clock {

  var isScheduled: Boolean = false
    private set

  private var endTime: Long = 0

  fun schedule(milliseconds: Long) {
    this.endTime = System.currentTimeMillis() + milliseconds
    this.isScheduled = true
  }

  fun schedule(milliseconds: Double) {
    this.endTime = (System.currentTimeMillis() + milliseconds.toLong())
    this.isScheduled = true
  }

  fun passed(): Boolean {
    return System.currentTimeMillis() >= endTime
  }

  fun reset() {
    this.isScheduled = false
    endTime = 0
  }

}
