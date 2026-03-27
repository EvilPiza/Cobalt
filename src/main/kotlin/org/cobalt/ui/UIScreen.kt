package org.cobalt.ui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.cobalt.Cobalt
import org.cobalt.util.helper.TickScheduler
import org.cobalt.util.render.Render2D

abstract class UIScreen : Screen(Component.empty()) {

  protected val mc: Minecraft
    get() = Minecraft.getInstance()

  override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
    val window = mc.window
    val screenWidth = window.width.toFloat()
    val screenHeight = window.height.toFloat()

    Render2D.render(screenWidth, screenHeight) {
      renderScreen(screenWidth, screenHeight, Render2D)
    }
  }

  override fun extractBlurredBackground(graphics: GuiGraphicsExtractor) {}
  override fun extractMenuBackground(graphics: GuiGraphicsExtractor) {}

  fun openScreen() {
    TickScheduler.schedule(1) {
      mc.setScreen(this)
    }
  }

  abstract fun renderScreen(screenWidth: Float, screenHeight: Float, renderer: Render2D)

}
