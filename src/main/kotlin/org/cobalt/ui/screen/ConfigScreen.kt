package org.cobalt.ui.screen

import java.awt.Color
import org.cobalt.ui.UIScreen
import org.cobalt.util.ui.Renderer

internal object ConfigScreen : UIScreen() {

  override fun renderScreen(screenWidth: Float, screenHeight: Float, renderer: Renderer) {
    renderer.rect(0f, 0f, 100f, 100f, Color.WHITE.rgb)
  }

}
