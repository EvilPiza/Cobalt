package org.cobalt.ui.notification

import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.render.skia.SkiaRenderer
import org.cobalt.math.SimpleVec3

/**
 * Manager responsible for displaying on-screen notifications.
 *
 * Notifications pushed to this manager will be rendered each frame via the
 * Skia renderer. The manager registers itself on the global event bus in the
 * initializer so it receives draw callbacks.
 */
object NotificationManager {

  // Internal storage for active notifications.
  private val notificationsList = mutableSetOf<Notification>()

  init {
    EventBus.register(this)
  }

  /**
   * Enqueue a notification for rendering.
   *
   * The notification will be retained until it decides to remove itself or
   * the manager is cleared. Notifications are rendered in an unspecified
   * iteration order.
   *
   * @param notification the notification to display
   */
  fun pushNotification(notification: Notification) {
    notificationsList.add(notification)
  }

  /**
   * Event handler invoked during the Skia draw pass.
   *
   * This method iterates over all active notifications and renders each one
   * using the `SkiaRenderer`. Notifications are drawn with the appropriate
   * window scale and the renderer state is saved/restored around each
   * notification draw call.
   */
  @SubscribeEvent
  fun onSkiaDraw(@Suppress("UnusedParameter") event: SkiaDrawEvent) {
    val windowScale = SkiaRenderer.getWindowScale()

    notificationsList
      .forEach { notification ->
        SkiaRenderer.save()

        val originX = notification.xPos
        val originY = notification.yPos

        SkiaRenderer.translate(SimpleVec3(originX, originY))
        SkiaRenderer.scale(SimpleVec3(windowScale, windowScale))
        SkiaRenderer.translate(SimpleVec3(-originX, -originY))

        notification.renderComponent()

        SkiaRenderer.restore()
      }
  }

}


