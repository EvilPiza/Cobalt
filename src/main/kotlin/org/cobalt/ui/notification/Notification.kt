package org.cobalt.ui.notification

import kotlin.time.Duration
import org.cobalt.ui.UIComponent

/** Simple on-screen notification displayed for a given duration.
 *
 * @param title short headline text shown prominently
 * @param description body text shown below the title
 * @param duration how long the notification should be visible
 */
data class Notification(
  val title: String,
  val description: String,
  val duration: Duration
) : UIComponent(
  xPos = DEFAULT_X,
  yPos = DEFAULT_Y,
  width = DEFAULT_WIDTH,
  height = DEFAULT_HEIGHT
) {

  companion object {
    private const val DEFAULT_X: Float = 0f
    private const val DEFAULT_Y: Float = 0f
    private const val DEFAULT_WIDTH: Float = 100f
    private const val DEFAULT_HEIGHT: Float = 100f
  }

  /** Render the notification UI. */
  override fun renderComponent() {

  }


}

enum class NotificationType {
  SUCCESS,
  WARNING,
  ERROR,
  INFO
}
