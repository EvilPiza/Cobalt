package org.cobalt.ui.notification

import kotlin.time.Duration
import org.cobalt.ui.UIComponent

data class Notification(
  val title: String,
  val description: String,
  val duration: Duration
) : UIComponent(
  xPos = 0f,
  yPos = 0f,
  width = 100f,
  height = 100f
) {

  override fun renderComponent() {

  }


}

enum class NotificationType {
  SUCCESS,
  WARNING,
  ERROR,
  INFO
}
