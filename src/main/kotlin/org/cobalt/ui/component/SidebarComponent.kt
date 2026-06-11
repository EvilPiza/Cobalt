package org.cobalt.ui.component

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import org.cobalt.Cobalt.minecraft
import org.cobalt.ui.UIComponent
import org.cobalt.ui.component.button.SidebarButton
import org.cobalt.ui.page.PageType
import org.cobalt.util.skia.Skia
import org.cobalt.util.skia.helper.SkiaCorner

object SidebarComponent : UIComponent(
  width = 250f,
  height = 600f
) {

  private val buttons = mutableListOf<SidebarButton>()

  fun preload() {
    for (page in PageType.entries) {
      val button = SidebarButton(page)
      this.addChild(button)
      buttons.add(button)
    }

    playerFace
  }

  override fun renderComponent() {
    Skia.roundedRect(
      xPos, yPos, width, height,
      10f, theme.backgroundSecondary,
      SkiaCorner.LEFT_SIDE
    )

    val titleTextWidth = Skia.textWidth(Skia.boldFont, TITLE_TEXT, TITLE_FONT_SIZE)
    val titleTextX = xPos + (width - titleTextWidth) / 2
    val titleTextY = yPos + TITLE_PADDING

    Skia.text(
      Skia.boldFont, TITLE_TEXT,
      titleTextX, titleTextY,
      TITLE_FONT_SIZE, theme.textPrimary
    )

    val buttonX = xPos + (width - SidebarButton.WIDTH) / 2f
    var buttonY = yPos + TITLE_FONT_SIZE + (TITLE_PADDING * 2)

    buttons.forEach { button ->
      button
        .updateBounds(buttonX, buttonY)
        .renderComponent()

      buttonY += SidebarButton.HEIGHT + BUTTONS_SPACING
    }

    val boxX = xPos + USER_INFO_OUTER_PADDING
    val boxY = yPos + height - (USER_INFO_HEIGHT + USER_INFO_OUTER_PADDING)

    Skia.roundedRect(
      boxX, boxY, USER_INFO_WIDTH, USER_INFO_HEIGHT,
      USER_INFO_CORNER_RADIUS, theme.backgroundPrimary,
    )

    Skia.roundedOutline(
      boxX, boxY, USER_INFO_WIDTH, USER_INFO_HEIGHT,
      1f, USER_INFO_CORNER_RADIUS, theme.border
    )

    val playerFaceX = boxX + USER_INFO_INNER_PADDING
    val playerFaceY = boxY + (USER_INFO_HEIGHT - PLAYER_FACE_SIDE_LENGTH) / 2

    Skia.image(
      playerFace,
      playerFaceX, playerFaceY,
      PLAYER_FACE_SIDE_LENGTH, PLAYER_FACE_SIDE_LENGTH,
      PLAYER_FACE_SIDE_LENGTH / 2
    )

    val textX = boxX + PLAYER_FACE_SIDE_LENGTH + (USER_INFO_INNER_PADDING * 2)
    val textY = boxY + USER_INFO_INNER_PADDING

    Skia.text(
      Skia.regularFont, minecraft.gameProfile.name,
      textX, textY, USER_INFO_TEXT_SIZE, theme.textPrimary
    )

    val currentTime = ZonedDateTime.now()
    val formatter = DateTimeFormatter
      .ofLocalizedTime(FormatStyle.SHORT)
      .withLocale(Locale.getDefault())

    Skia.text(
      Skia.regularFont,
      currentTime.format(formatter),
      textX, textY + USER_INFO_TEXT_SIZE + 2f,
      USER_INFO_TEXT_SIZE, theme.textSecondary,
    )
  }

  private val playerFace = try {
    Skia.createImage("https://mc-heads.net/avatar/${minecraft.user.profileId}/100/face.png")
  } catch (_: Exception) {
    Skia.createImage("/assets/cobalt/textures/steve.png")
  }

  private const val TITLE_TEXT = "cobalt"
  private const val TITLE_FONT_SIZE = 28f
  private const val TITLE_PADDING = 50f

  private const val BUTTONS_SPACING = 5f

  private const val USER_INFO_OUTER_PADDING = 15f
  private const val USER_INFO_INNER_PADDING = 12f
  private const val USER_INFO_CORNER_RADIUS = 5f
  private const val USER_INFO_TEXT_SIZE = 12.5f
  private const val USER_INFO_HEIGHT = 55f
  private const val PLAYER_FACE_SIDE_LENGTH = USER_INFO_HEIGHT - (USER_INFO_INNER_PADDING * 2)
  private val USER_INFO_WIDTH = width - (USER_INFO_OUTER_PADDING * 2)

}
