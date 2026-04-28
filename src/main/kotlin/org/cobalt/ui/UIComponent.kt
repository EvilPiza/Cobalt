package org.cobalt.ui

abstract class UIComponent(
  var xPos: Float,
  var yPos: Float,
  open val width: Float = 0.0f,
  open val height: Float = 0.0f,
) {

  abstract fun renderComponent()

  fun updateBounds(xPos: Float, yPos: Float): UIComponent {
    this.xPos = xPos
    this.yPos = yPos
    return this
  }

}
