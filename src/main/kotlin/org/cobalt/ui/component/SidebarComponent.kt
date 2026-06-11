package org.cobalt.ui.component

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import net.minecraft.client.Minecraft
import org.cobalt.Cobalt.minecraft
import org.cobalt.ui.UIComponent
import org.cobalt.ui.component.button.SidebarButton
import org.cobalt.ui.page.PageType
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaCorner
import org.cobalt.util.skia.SkiaImages
import org.cobalt.util.skia.SkiaOutlines
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaText
import org.cobalt.util.skia.TextStyle

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
    SkiaShapes.drawRoundedRect(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      10f,
      theme.backgroundSecondary.rgb,
      listOf(SkiaCorner.LEFT)
    )

    val titleTextWidth = SkiaText.getTextWidth(SkiaText.boldFont, TITLE_TEXT, TITLE_FONT_SIZE)
    val titleTextX = xPos + (width - titleTextWidth) / 2
    val titleTextY = yPos + TITLE_PADDING

    SkiaText.drawText(
      SkiaText.boldFont, TITLE_TEXT,
      Vec2f(titleTextX, titleTextY),
      TextStyle(TITLE_FONT_SIZE, theme.textPrimary.rgb)
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

    SkiaShapes.drawRoundedRect(
      Vec2f(boxX, boxY),
      Dimensions(USER_INFO_WIDTH, USER_INFO_HEIGHT),
      USER_INFO_CORNER_RADIUS,
      theme.backgroundPrimary.rgb,
    )

    SkiaOutlines.drawRoundedOutline(
      Vec2f(boxX, boxY),
      Dimensions(USER_INFO_WIDTH, USER_INFO_HEIGHT),
      USER_INFO_CORNER_RADIUS,
      theme.border.rgb
    )

    val playerFaceX = boxX + USER_INFO_INNER_PADDING
    val playerFaceY = boxY + (USER_INFO_HEIGHT - PLAYER_FACE_SIDE_LENGTH) / 2

    SkiaImages.drawImage(
      playerFace,
      Vec2f(playerFaceX, playerFaceY),
      Dimensions(PLAYER_FACE_SIDE_LENGTH, PLAYER_FACE_SIDE_LENGTH)
    )

    val textX = boxX + PLAYER_FACE_SIDE_LENGTH + (USER_INFO_INNER_PADDING * 2)
    val textY = boxY + USER_INFO_INNER_PADDING

    SkiaText.drawText(
      SkiaText.regularFont, minecraft.gameProfile.name,
      Vec2f(textX, textY),
      TextStyle(USER_INFO_TEXT_SIZE, theme.textPrimary.rgb),
    )

    val currentTime = ZonedDateTime.now()
    val formatter = DateTimeFormatter
      .ofLocalizedTime(FormatStyle.SHORT)
      .withLocale(Locale.getDefault())

    SkiaText.drawText(
      SkiaText.regularFont,
      currentTime.format(formatter),
      Vec2f(textX, textY + USER_INFO_TEXT_SIZE + 2f),
      TextStyle(USER_INFO_TEXT_SIZE, theme.textSecondary.rgb),
    )
  }

  private val playerFace = try {
    SkiaImages.loadImage(
      identifier = "https://mc-heads.net/avatar/${Minecraft.getInstance().user.profileId}/100/face.png",
      radius = PLAYER_FACE_SIDE_LENGTH / 2
    )
  } catch (_: Exception) {
    SkiaImages.loadImage(
      identifier = "/assets/cobalt/textures/steve.png",
      radius = PLAYER_FACE_SIDE_LENGTH / 2
    )
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
  private val USER_INFO_WIDTH = width - (USER_INFO_OUTER_PADDING * 2)
  private const val PLAYER_FACE_SIDE_LENGTH = USER_INFO_HEIGHT - (USER_INFO_INNER_PADDING * 2)

}
