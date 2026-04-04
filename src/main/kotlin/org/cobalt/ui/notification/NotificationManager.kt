package org.cobalt.ui.notification

import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.util.skia.SkiaRenderer

object NotificationManager {

  private val notificationsList = mutableSetOf<Notification>()

  init {
    EventBus.register(this)
  }

  fun pushNotification(notification: Notification) {
    notificationsList.add(notification)
  }

  @SubscribeEvent
  fun onSkiaDraw(event: SkiaDrawEvent) {
    val windowScale = SkiaRenderer.getWindowScale()

    notificationsList
      .forEach { notification ->
        SkiaRenderer.save()

        val originX = notification.xPos
        val originY = notification.yPos

        SkiaRenderer.translate(originX, originY)
        SkiaRenderer.scale(windowScale, windowScale)
        SkiaRenderer.translate(-originX, -originY)

        notification.renderComponent()

        SkiaRenderer.restore()
      }
  }

}
