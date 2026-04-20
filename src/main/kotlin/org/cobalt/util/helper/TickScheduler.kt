package org.cobalt.util.helper

import java.util.*
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.TickEvent

/**
 * Utility for scheduling delayed tasks based on client tick updates.
 */
object TickScheduler {

  private val taskQueue = PriorityQueue<ScheduledTask>(Comparator.comparingLong(ScheduledTask::executeTick))
  private var currentTick: Long = 0

  private data class ScheduledTask(val executeTick: Long, val action: Runnable)

  init {
    EventBus.register(this)
  }

  /**
   * Schedules a task to be executed after a given number of client ticks.
   *
   * @param delayTicks number of ticks to wait before executing the task
   * @param action the task to execute after the delay
   */
  @JvmStatic
  fun schedule(delayTicks: Long, action: Runnable) {
    taskQueue.offer(ScheduledTask(currentTick + delayTicks, action))
  }

  @Suppress("UndocumentedPublicFunction")
  @SubscribeEvent
  fun onClientTick(@Suppress("UnusedParameter") event: TickEvent.End) {
    currentTick++
    var task: ScheduledTask?

    while (taskQueue.peek().also { task = it } != null && currentTick >= task!!.executeTick) {
      taskQueue.poll().action.run()
    }
  }

}
