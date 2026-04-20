package org.cobalt.ui.notification

import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.math.Vec2f
import org.cobalt.util.WindowUtils
import org.cobalt.util.skia.SkiaTransforms

/**
 * Central manager for Cobalt notifications.
 */
object NotificationManager {

  private val notificationsList = mutableSetOf<Notification>()

  init {
    EventBus.register(this)
  }

  /**
   * Adds a notification to be rendered.
   *
   * @param notification the notification instance to display
   */
  fun pushNotification(notification: Notification) {
    notificationsList.add(notification)
  }

  @Suppress("UndocumentedPublicFunction")
  @SubscribeEvent
  fun onSkiaDraw(@Suppress("UnusedParameter") event: SkiaDrawEvent) {
    val windowScale = WindowUtils.getWindowScale()

    notificationsList
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


