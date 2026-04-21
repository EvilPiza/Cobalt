package org.cobalt.ui.notification

import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.util.Vec2f
import org.cobalt.util.WindowUtils
import org.cobalt.util.skia.SkiaTransforms

object NotificationManager {

  private val notifQueue = mutableListOf<Notification>()
  private val activeNotifications = mutableListOf<Notification>()

  init {
    EventBus.register(this)
  }

  /**
   * Queue a notification to be rendered.
   *
   * @param notification the [Notification] instance to be displayed
   */
  fun queue(notification: Notification) {
    notifQueue.add(notification)
  }


  /**
   * Clears all notifications.
   */
  fun clear() {
    notifQueue.clear()
    activeNotifications.clear()
  }

  @Suppress("UndocumentedPublicFunction")
  @SubscribeEvent
  fun onSkiaDraw(@Suppress("UnusedParameter") event: SkiaDrawEvent) {
    val windowScale = WindowUtils.getWindowScale()

    activeNotifications
      .forEach { notification ->
        SkiaTransforms.save()

        val originX = notification.xPos
        val originY = notification.yPos

        SkiaTransforms.translate(Vec2f(originX, originY))
        SkiaTransforms.scale(Vec2f(windowScale, windowScale))
        SkiaTransforms.translate(Vec2f(-originX, -originY))

        notification.renderComponent()

        SkiaTransforms.restore()
      }
  }

}


