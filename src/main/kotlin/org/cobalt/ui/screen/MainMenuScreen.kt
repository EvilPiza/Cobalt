package org.cobalt.ui.screen

import io.github.humbleui.skija.RuntimeEffect
import java.awt.Color
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen
import net.minecraft.client.gui.screens.options.OptionsScreen
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.ui.component.MainMenuButton
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.WindowUtils.windowHeight
import org.cobalt.util.WindowUtils.windowWidth
import org.cobalt.util.helper.TickScheduler
import org.cobalt.util.skia.SkiaShaders
import org.cobalt.util.skia.SkiaText
import org.cobalt.util.skia.TextStyle

object MainMenuScreen : Screen(Component.empty()) {

  private var runtimeEffect: RuntimeEffect? = null
  private val buttons = mutableListOf<MainMenuButton>()

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
    runtimeEffect = SkiaShaders.loadShader("/assets/cobalt/shader/blur.sksl")
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

    runtimeEffect?.let { effect ->
      SkiaShaders.renderShader(Vec2f(0f, 0f), Dimensions(windowWidth, windowHeight), effect)
    }

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
      TextStyle(fontSize = TITLE_FONT_SIZE, color = Color.WHITE.rgb)
    )

    currentY += TITLE_FONT_SIZE + TEXT_TO_BUTTONS_SPACING

    buttons.forEach { button ->
      button
        .updateBounds(buttonX, currentY)
        .renderComponent()

      currentY += MainMenuButton.HEIGHT + BUTTON_SPACING
    }
  }

  override fun shouldCloseOnEsc(): Boolean = false

  override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
    if (buttons.any { it.mouseClicked(event.button()) }) {
      return true
    }

    return super.mouseClicked(event, doubleClick)
  }

  private const val TITLE_TEXT = "cobalt"
  private const val TITLE_FONT_SIZE = 50f
  private const val TEXT_TO_BUTTONS_SPACING = 30f
  private const val BUTTON_SPACING = 10f

}
