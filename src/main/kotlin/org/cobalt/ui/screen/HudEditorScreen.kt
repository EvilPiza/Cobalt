package org.cobalt.ui.screen

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.module.ModuleManager
import org.cobalt.module.RenderableModule
import org.cobalt.util.Vec2f
import org.cobalt.util.WindowUtils
import org.cobalt.util.skia.SkiaTransforms

internal object HudEditorScreen : Screen(Component.empty()) {

  private val modules: List<RenderableModule>
    get() = ModuleManager.getModules()
      .filterIsInstance<RenderableModule>()
      .filter { it.isEnabled() }

  init {
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onSkiaDraw(@Suppress("UnusedParameter") event: SkiaDrawEvent) {
    if (minecraft.screen != this) {
      return
    }

    modules.forEach { module ->
      SkiaTransforms.save()

      val originX = module.xPos
      val originY = module.yPos
      val moduleScale = module.scale * WindowUtils.windowScale

      SkiaTransforms.translate(Vec2f(originX, originY))
      SkiaTransforms.scale(Vec2f(moduleScale, moduleScale))
      SkiaTransforms.translate(Vec2f(-originX, -originY))

      module.renderComponent()

      SkiaTransforms.restore()
    }
  }

}
