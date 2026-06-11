package org.cobalt.module

import net.minecraft.client.gui.screens.LevelLoadingScreen
import net.minecraft.client.gui.screens.ProgressScreen
import org.cobalt.Cobalt.minecraft
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.HudEvent
import org.cobalt.module.impl.render.PerformanceHUD
import org.cobalt.ui.screen.HudEditorScreen
import org.cobalt.util.skia.Skia
import org.cobalt.util.skia.SkiaPIP

object ModuleManager {

  val modules = mutableSetOf<Module>()

  init {
    EventBus.register(this)
  }

  internal fun registerModules() {
    val builtIn = arrayOf(
      PerformanceHUD
    )

    builtIn.forEach { module ->
      addModule(module)
    }
  }

  fun addModule(module: Module) {
    if (!modules.add(module)) {
      error("'${module.name}' is already registered")
    }

    module.loadConfig()
    module.onRegistration()
  }

  fun getModule(moduleName: String): Module? {
    return modules.find { module ->
      module.name.equals(moduleName, true)
    }
  }

  private val shouldSkipRender: Boolean
    get() {
      return minecraft.level == null ||
        minecraft.player == null ||
        minecraft.options.hideGui ||
        minecraft.debugOverlay.showDebugScreen() ||
        minecraft.screen is LevelLoadingScreen ||
        minecraft.screen is ProgressScreen ||
        minecraft.screen is HudEditorScreen
    }

  @SubscribeEvent
  fun drawRenderableModules(event: HudEvent) {
    if (shouldSkipRender) {
      return
    }

    SkiaPIP.drawSkia(event.graphics) {
      modules.filterIsInstance<RenderableModule>()
        .filter { module -> module.enabled }
        .forEach { module ->
          Skia.push()

          val (x, y) = module.screenPosition
          val scale = module.scale

          Skia.translate(x, y)
          Skia.scale(scale, scale)
          Skia.translate(-x, -y)

          module.renderComponent()

          Skia.pop()
        }
    }
  }

}


