package org.cobalt.ui.notification

import kotlin.time.Duration
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
   * @param title short headline text shown prominently
   * @param description body text shown below the title
   * @param duration how long the notification remains visible
   */
  fun queue(title: String, description: String, duration: Duration) {
    val notification = Notification(
      title,
      description,
      duration
    )

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

    updateNotifications(windowScale)

    activeNotifications.forEach { notification ->
      SkiaTransforms.save()
      SkiaTransforms.scale(Vec2f(windowScale, windowScale))
      notification.renderComponent()
      SkiaTransforms.restore()
    }
  }

  private fun updateNotifications(windowScale: Float) {
    val screenHeight = WindowUtils.getHeight() / windowScale
    val currentTime = System.currentTimeMillis()

    activeNotifications.forEach { it.checkExpiry(currentTime) }
    activeNotifications.removeIf { it.isDone() }

    while (activeNotifications.size < MAX_ACTIVE_NOTIFICATIONS && notifQueue.isNotEmpty()) {
      val notif = notifQueue.removeAt(0)
      val targetY = computeTargetY(screenHeight, activeNotifications.size, notif.height)

      notif.targetY = targetY
      notif.previousY = targetY
      notif.start(currentTime)

      activeNotifications.add(notif)
    }

    activeNotifications.forEachIndexed { index, notif ->
      notif.moveTo(computeTargetY(screenHeight, index, notif.height))
    }
  }

  private fun computeTargetY(screenHeight: Float, index: Int, notifHeight: Float): Float {
    return screenHeight - (index + 1) * (notifHeight + NOTIFICATION_MARGIN) - NOTIFICATION_MARGIN
  }

  private const val MAX_ACTIVE_NOTIFICATIONS: Int = 3
  private const val NOTIFICATION_MARGIN: Float = 10f

}
