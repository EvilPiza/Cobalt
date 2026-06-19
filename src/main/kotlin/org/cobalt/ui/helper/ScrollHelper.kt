package org.cobalt.ui.helper

class ScrollHelper(private val scrollSpeed: Float = 10f) {

  var scrollOffset: Float = 0f
    private set

  private var maxScroll: Float = 0f

  fun updateMaxScroll(contentHeight: Float, viewportHeight: Float) {
    maxScroll = maxOf(0f, contentHeight - viewportHeight)
    scrollOffset = scrollOffset.coerceIn(0f, maxScroll)
  }

  fun scroll(amount: Double) {
    scrollOffset = (scrollOffset - amount.toFloat() * scrollSpeed)
      .coerceIn(0f, maxScroll)
  }

  fun reset() {
    scrollOffset = 0f
  }

  val canScroll: Boolean
    get() = maxScroll > 0f

}
