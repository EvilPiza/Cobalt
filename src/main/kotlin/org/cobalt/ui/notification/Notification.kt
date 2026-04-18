package org.cobalt.ui.notification

import kotlin.time.Duration
import org.cobalt.ui.UIComponent

/** Simple on-screen notification displayed for a given duration.
 *
 * @property title short headline text shown prominently
 * @property description body text shown below the title
 * @property duration how long the notification should be visible
 */
data class Notification(
  /** Short headline text shown prominently. */
  val title: String,

  /** Body text shown below the title. */
  val description: String,

  /** How long the notification should be visible. */
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

  /**
   * Render the notification UI.
   *
   * Implementations should draw the notification background, title and
   * description within the component bounds. This method is called every
   * frame while the notification is visible.
   */
  override fun renderComponent() { return }

}

/** Types of notifications used to indicate severity or purpose. */
enum class NotificationType {
  /** Indicates a successful operation. */
  SUCCESS,

  /** Indicates a warning or non-critical issue. */
  WARNING,

  /** Indicates an error or critical problem. */
  ERROR,

  /** Informational message without success/error semantics. */
  INFO
}
