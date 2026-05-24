package org.cobalt.ui.screen

import net.minecraft.SharedConstants
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen
import net.minecraft.client.gui.screens.options.OptionsScreen
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import org.cobalt.Cobalt
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.ui.component.button.MainMenuButton
import org.cobalt.ui.theme.Theme
import org.cobalt.ui.theme.ThemeManager
import org.cobalt.util.Vec2f
import org.cobalt.util.WindowUtils.windowHeight
import org.cobalt.util.WindowUtils.windowWidth
import org.cobalt.util.helper.TickScheduler
import org.cobalt.util.skia.SkiaText
import org.cobalt.util.skia.TextStyle

object MainMenuScreen : Screen(Component.empty()) {

  private val buttons = mutableListOf<MainMenuButton>()
  private var fading = false
  private var fadeStart = 0L

  private val theme: Theme
    get() = ThemeManager.activeTheme

  init {
    buttons.add(
      MainMenuButton(
        label = "Singleplayer",
        onClick = { TickScheduler.schedule(1L) { minecraft.setScreen(SelectWorldScreen(this)) } }
      ))

    buttons.add(
      MainMenuButton(
        label = "Multiplayer",
        onClick = { TickScheduler.schedule(1L) { minecraft.setScreen(JoinMultiplayerScreen(this)) } }
      ))

    buttons.add(
      MainMenuButton(
        label = "Options",
        onClick = { TickScheduler.schedule(1L) { minecraft.setScreen(OptionsScreen(this, minecraft.options, false)) } }
      ))

    buttons.add(
      MainMenuButton(
        label = "Quit",
        onClick = { minecraft.stop() }
      ))
  }

  override fun added() {
    fading = minecraft.overlay != null
    fadeStart = 0L

    EventBus.register(this)
  }

  override fun removed() {
    EventBus.unregister(this)
  }

  @SubscribeEvent
  fun onSkiaDraw(@Suppress("UnusedParameter") event: SkiaDrawEvent) {
    if (minecraft.screen != this) {
      return
    }

    if (minecraft.overlay != null) {
      return
    }

    val canvas = event.canvas

    if (fading && fadeStart == 0L && minecraft.overlay == null) {
      fadeStart = System.currentTimeMillis()
    }

    if (fading) {
      val alpha = ((System.currentTimeMillis() - fadeStart) / MS_PER_SECOND).coerceIn(0f, 1f)
      canvas.saveLayerAlpha(null, (alpha * MAX_VALUE).toInt())
    }

    drawElements()
    drawInfoText()

    if (fading) {
      canvas.restore()
    }
  }

  private fun drawElements() {
    val buttonColumnHeight = (MainMenuButton.HEIGHT + BUTTON_SPACING) * (buttons.size - 1) + MainMenuButton.HEIGHT
    val totalGroupHeight = TITLE_FONT_SIZE + TEXT_TO_BUTTONS_SPACING + buttonColumnHeight

    val textWidth = SkiaText.getTextWidth(SkiaText.boldFont, TITLE_TEXT, TITLE_FONT_SIZE)
    val textX = (windowWidth - textWidth) / 2

    val buttonX = (windowWidth - MainMenuButton.WIDTH) / 2f
    var currentY = (windowHeight - totalGroupHeight) / 2f

    SkiaText.drawText(
      SkiaText.boldFont,
      TITLE_TEXT,
      Vec2f(textX, currentY),
      TextStyle(fontSize = TITLE_FONT_SIZE, color = theme.textPrimary.rgb)
    )

    currentY += TITLE_FONT_SIZE + TEXT_TO_BUTTONS_SPACING

    buttons.forEach { button ->
      button
        .updateBounds(buttonX, currentY)
        .renderComponent()

      currentY += MainMenuButton.HEIGHT + BUTTON_SPACING
    }
  }

  private fun drawInfoText() {
    val leftText = "${Cobalt.MOD_NAME} ${Cobalt.MINECRAFT_VERSION} (v${Cobalt.MOD_VERSION})"
    val rightText = "Not affiliated with Mojang or Microsoft"
    val textY = windowHeight - INFO_TEXT_PADDING - INFO_TEXT_SIZE

    SkiaText.drawText(
      SkiaText.regularFont,
      leftText,
      Vec2f(INFO_TEXT_PADDING, textY),
      TextStyle(fontSize = INFO_TEXT_SIZE, color = theme.textSecondary.rgb)
    )

    val textWidth = SkiaText.getTextWidth(SkiaText.regularFont, rightText, INFO_TEXT_SIZE)
    val textX = windowWidth - textWidth - INFO_TEXT_PADDING

    SkiaText.drawText(
      SkiaText.regularFont,
      rightText,
      Vec2f(textX, textY),
      TextStyle(fontSize = INFO_TEXT_SIZE, color = theme.textSecondary.rgb)
    )
  }

  override fun shouldCloseOnEsc(): Boolean = false

  override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
    if (buttons.any { it.mouseClicked(event.button()) }) {
      return true
    }

    return super.mouseClicked(event, doubleClick)
  }

  private const val TITLE_TEXT = Cobalt.NAMESPACE
  private const val TITLE_FONT_SIZE = 50f
  private const val TEXT_TO_BUTTONS_SPACING = 30f
  private const val BUTTON_SPACING = 10f
  private const val MAX_VALUE = 255f
  private const val MS_PER_SECOND = 1000f
  private const val INFO_TEXT_SIZE = 12f
  private const val INFO_TEXT_PADDING = 15f

}
