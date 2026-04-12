package org.cobalt.ui

/** Base UI component with position and size used by HUD/editor screens. */
abstract class UIComponent(
  /** Screen-space X coordinate. */
  var xPos: Float,
  /** Screen-space Y coordinate. */
  var yPos: Float,
  /** Component width in pixels. */
  open val width: Float = 0.0f,
  /** Component height in pixels. */
  open val height: Float = 0.0f,
) {

  /** Render the component's contents onto the current canvas/context. */
  abstract fun renderComponent()

  /** Update the component's screen-space position and return this instance. */
  fun updateBounds(xPos: Float, yPos: Float): UIComponent {
    this.xPos = xPos
    this.yPos = yPos
    return this
  }

}
