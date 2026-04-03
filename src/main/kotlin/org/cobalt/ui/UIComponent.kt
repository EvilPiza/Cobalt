package org.cobalt.ui

abstract class UIComponent(
  var x: Float,
  var y: Float,
  open val width: Float = 0.0f,
  open val height: Float = 0.0f,
) {

  abstract fun render()

  fun updateBounds(x: Float, y: Float): UIComponent {
    this.x = x
    this.y = y
    return this
  }

}
