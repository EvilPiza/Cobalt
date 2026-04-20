package org.cobalt.ui

/**
 * Base class for all drawable UI components.
 *
 * @property xPos X position of the component
 * @property yPos Y position of the component
 * @property width component width
 * @property height component height
 */
abstract class UIComponent(
  var xPos: Float,
  var yPos: Float,
  open val width: Float = 0.0f,
  open val height: Float = 0.0f,
) {

  /**
   * Render the component's contents.
   */
  abstract fun renderComponent()

  /**
   * Updates the component position.
   *
   * @param xPos new X coordinate in screen space
   * @param yPos new Y coordinate in screen space
   * @return this component for chaining
   */
  fun updateBounds(xPos: Float, yPos: Float): UIComponent {
    this.xPos = xPos
    this.yPos = yPos
    return this
  }

}
