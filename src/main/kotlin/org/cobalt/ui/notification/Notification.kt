package org.cobalt.ui.notification

import kotlin.time.Duration
import org.cobalt.ui.UIComponent

/**
 * On-screen notification UI element.
 *
 * @property title short headline text shown prominently
 * @property description body text shown below the title
 * @property duration how long the notification remains visible
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

  /**
   * Renders the notification contents.
   */
  override fun renderComponent() {
    // TODO: draw the actual notification here..
  }

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
