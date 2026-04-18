package org.cobalt.util.helper

import java.util.*
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.TickEvent

/** Schedule Runnable tasks to run after a number of client ticks.
 * Tasks are executed on TickEvent.End and are ordered by scheduled tick. */
object TickScheduler {

  private val taskQueue = PriorityQueue<ScheduledTask>(Comparator.comparingLong(ScheduledTask::executeTick))
  private var currentTick: Long = 0

  private data class ScheduledTask(val executeTick: Long, val action: Runnable)

  init {
    EventBus.register(this)
  }

  /** Schedule an action to execute after the given number of ticks. */
  @JvmStatic
  fun schedule(delayTicks: Long, action: Runnable) {
    taskQueue.offer(ScheduledTask(currentTick + delayTicks, action))
  }

  /** Internal event handler invoked at the end of each client tick to flush scheduled tasks. */
  @SubscribeEvent
  fun onClientTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.End) {
    currentTick++
    var task: ScheduledTask?

    while (taskQueue.peek().also { task = it } != null && currentTick >= task!!.executeTick) {
      taskQueue.poll().action.run()
    }
  }

}
