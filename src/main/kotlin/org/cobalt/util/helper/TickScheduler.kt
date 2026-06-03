package org.cobalt.util.helper

import java.util.PriorityQueue
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.TickEvent

object TickScheduler {

  private val taskQueue = PriorityQueue<ScheduledTask>(Comparator.comparingLong(ScheduledTask::executeTick))
  private var currentTick: Long = 0

  init {
    EventBus.register(this)
  }

  @JvmStatic
  fun schedule(delayTicks: Long, action: Runnable) {
    taskQueue.offer(ScheduledTask(currentTick + delayTicks, action))
  }

  @SubscribeEvent
  fun onClientTick(ignored: TickEvent.End) {
    currentTick++
    var task: ScheduledTask?

    while (taskQueue.peek().also { task = it } != null && currentTick >= task!!.executeTick) {
      taskQueue.poll().action.run()
    }
  }

  private data class ScheduledTask(val executeTick: Long, val action: Runnable)

}
