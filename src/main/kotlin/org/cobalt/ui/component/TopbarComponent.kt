package org.cobalt.ui.component

import org.cobalt.ui.UIComponent
import org.cobalt.ui.page.Page
import org.cobalt.ui.page.PageManager
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaCorner
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaText
import org.cobalt.util.skia.TextStyle

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

  private val currentPage: Page?
    get() = PageManager.currentPageType.page

  init {
    addChild(searchBar)
  }

  override fun renderComponent() {
    val currentPage = currentPage ?: return

    SkiaShapes.drawRoundedRect(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      10f,
      theme.backgroundSecondary.rgb,
      listOf(SkiaCorner.TOP_RIGHT)
    )

    val textX = xPos + INNER_PADDING
    val textY = yPos + (height - CURRENT_PAGE_TITLE_FONT) / 2

    SkiaText.drawText(
      SkiaText.regularFont,
      currentPage.title,
      Vec2f(textX, textY),
      TextStyle(CURRENT_PAGE_TITLE_FONT, theme.textPrimary.rgb)
    )

    val searchBarX = xPos + width - SEARCHBAR_WIDTH - INNER_PADDING
    val searchBarY = yPos + (height - SEARCHBAR_HEIGHT) / 2

    searchBar
      .updateBounds(searchBarX, searchBarY)
      .renderComponent()
  }

  private const val SEARCHBAR_WIDTH = 250f
  private const val SEARCHBAR_HEIGHT = 40f
  private const val CURRENT_PAGE_TITLE_FONT = 19f
  private const val INNER_PADDING = 20f

}
