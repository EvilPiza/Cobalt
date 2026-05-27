package org.cobalt.ui.component

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import net.minecraft.client.Minecraft
import org.cobalt.Cobalt
import org.cobalt.Cobalt.minecraft
import org.cobalt.ui.UIComponent
import org.cobalt.ui.component.button.SidebarButton
import org.cobalt.ui.page.Page
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaImages
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaSide
import org.cobalt.util.skia.SkiaText
import org.cobalt.util.skia.TextStyle

private const val SIDEBAR_WIDTH = 250f
private const val SIDEBAR_HEIGHT = 600f

object SidebarComponent : UIComponent(
  width = SIDEBAR_WIDTH,
  height = SIDEBAR_HEIGHT
) {

  private val buttons = mutableListOf<SidebarButton>()

  fun preload() {
    for (page in Page.entries) {
      val button = SidebarButton(page)
      this.addChild(button)
      buttons.add(button)
    }

    playerFace
  }

  override fun renderComponent() {
    SkiaShapes.drawHalfRoundedRect(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      radius = CORNER_RADIUS,
      color = theme.backgroundSecondary.rgb,
      side = SkiaSide.LEFT
    )

    drawTitle()
    drawButtons()
    drawUserInfo()
  }


  private fun drawTitle() {
    val textWidth = SkiaText.getTextWidth(SkiaText.boldFont, TITLE_TEXT, TITLE_FONT_SIZE)
    val textX = xPos + (width - textWidth) / 2
    val textY = yPos + TITLE_PADDING

    SkiaText.drawText(
      SkiaText.boldFont,
      TITLE_TEXT,
      Vec2f(textX, textY),
      TextStyle(fontSize = TITLE_FONT_SIZE, color = theme.textPrimary.rgb)
    )
  }

  private fun drawButtons() {
    val buttonX = xPos + (width - SidebarButton.WIDTH) / 2f
    var buttonY = yPos + TITLE_FONT_SIZE + (TITLE_PADDING * 2)

    buttons.forEach { button ->
      button
        .updateBounds(buttonX, buttonY)
        .renderComponent()

      buttonY += SidebarButton.HEIGHT + BUTTONS_SPACING
    }
  }

  private fun drawUserInfo() {
    val boxX = xPos + USER_INFO_OUTER_PADDING
    val boxY = yPos + SIDEBAR_HEIGHT - (USER_INFO_HEIGHT + USER_INFO_OUTER_PADDING)

    drawUserInfoBox(boxX, boxY)
    drawPlayerFace(boxX, boxY)
    drawUserInfoText(boxX, boxY)
  }

  private fun drawUserInfoBox(boxX: Float, boxY: Float) {
    SkiaShapes.drawRoundedRect(
      Vec2f(boxX, boxY),
      Dimensions(USER_INFO_WIDTH, USER_INFO_HEIGHT),
      radius = USER_INFO_CORNER_RADIUS,
      color = theme.backgroundPrimary.rgb,
    )

    SkiaShapes.drawRoundedOutline(
      Vec2f(boxX, boxY),
      Dimensions(USER_INFO_WIDTH, USER_INFO_HEIGHT),
      radius = USER_INFO_CORNER_RADIUS,
      color = theme.border.rgb
    )
  }

  private fun drawPlayerFace(boxX: Float, boxY: Float) {
    val playerFaceX = boxX + USER_INFO_INNER_PADDING
    val playerFaceY = boxY + (USER_INFO_HEIGHT - PLAYER_FACE_SIDE_LENGTH) / 2

    SkiaImages.drawImage(
      playerFace,
      Vec2f(playerFaceX, playerFaceY),
      Dimensions(PLAYER_FACE_SIDE_LENGTH, PLAYER_FACE_SIDE_LENGTH)
    )
  }

  private fun drawUserInfoText(boxX: Float, boxY: Float) {
    val textX = boxX + PLAYER_FACE_SIDE_LENGTH + (USER_INFO_INNER_PADDING * 2)
    val textY = boxY + USER_INFO_INNER_PADDING

    SkiaText.drawText(
      SkiaText.regularFont,
      minecraft.gameProfile.name,
      Vec2f(textX, textY),
      TextStyle(fontSize = USER_INFO_TEXT_SIZE, color = theme.textPrimary.rgb),
    )

    SkiaText.drawText(
      SkiaText.regularFont,
      LocalDate.now().format(DateTimeFormatter.ofPattern("MM.dd.yyyy")),
      Vec2f(textX, textY + USER_INFO_TEXT_SIZE + 2f),
      TextStyle(fontSize = USER_INFO_TEXT_SIZE, color = theme.textSecondary.rgb),
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

  private const val TITLE_TEXT = Cobalt.NAMESPACE
  private const val TITLE_FONT_SIZE = 28f
  private const val TITLE_PADDING = 50f

  private const val BUTTONS_SPACING = 5f

  private const val USER_INFO_OUTER_PADDING = 15f
  private const val USER_INFO_INNER_PADDING = 12f
  private const val USER_INFO_CORNER_RADIUS = 5f
  private const val USER_INFO_TEXT_SIZE = 12.5f
  private const val USER_INFO_HEIGHT = 55f
  private const val USER_INFO_WIDTH = SIDEBAR_WIDTH - (USER_INFO_OUTER_PADDING * 2)
  private const val PLAYER_FACE_SIDE_LENGTH = USER_INFO_HEIGHT - (USER_INFO_INNER_PADDING * 2)

  private const val CORNER_RADIUS: Float = 10f

}
