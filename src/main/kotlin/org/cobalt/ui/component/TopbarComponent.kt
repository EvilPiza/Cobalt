package org.cobalt.ui.component

import org.cobalt.ui.UIComponent
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaCorner
import org.cobalt.util.skia.SkiaShapes

object TopbarComponent : UIComponent() {

  override val width: Float
    get() = 700f

  override val height: Float
    get() = 70f

  private val searchBar = TextInputComponent(
    width = SEARCHBAR_WIDTH,
    height = SEARCHBAR_HEIGHT,
    placeholder = "Search"
  )

  init {
    addChild(searchBar)
  }

  override fun renderComponent() {
    SkiaShapes.drawRoundedRect(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      10f,
      theme.backgroundSecondary.rgb,
      listOf(SkiaCorner.TOP_RIGHT)
    )

    val searchBarX = xPos + width - SEARCHBAR_WIDTH - SEARCHBAR_PADDING
    val searchBarY = yPos + (height - SEARCHBAR_HEIGHT) / 2

    searchBar
      .updateBounds(searchBarX, searchBarY)
      .renderComponent()
  }

  private const val SEARCHBAR_WIDTH = 250f
  private const val SEARCHBAR_HEIGHT = 40f
  private const val SEARCHBAR_PADDING = 20f

}
