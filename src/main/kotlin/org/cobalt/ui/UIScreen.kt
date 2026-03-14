package org.cobalt.ui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.cobalt.Cobalt
import org.cobalt.util.helper.TickScheduler
import org.cobalt.util.ui.Renderer

abstract class UIScreen : Screen(Component.empty()) {

  protected val mc: Minecraft
    get() = Minecraft.getInstance()

  override fun render(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
    val window = mc.window
    val screenWidth = window.width.toFloat()
    val screenHeight = window.height.toFloat()
    val renderer = Cobalt.renderer

    renderer.render(screenWidth, screenHeight) {
      renderScreen(screenWidth, screenHeight, renderer)
    }
  }

  override fun renderBlurredBackground(guiGraphics: GuiGraphics) {}
  override fun renderMenuBackground(guiGraphics: GuiGraphics) {}

  fun openScreen() {
    TickScheduler.schedule(1) {
      mc.setScreen(this)
    }
  }

  abstract fun renderScreen(screenWidth: Float, screenHeight: Float, renderer: Renderer)

}
