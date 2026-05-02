package org.cobalt.module

import org.cobalt.Cobalt.minecraft
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.module.impl.render.PerformanceHUD
import org.cobalt.util.Vec2f
import org.cobalt.util.WindowUtils
import org.cobalt.util.WindowUtils.windowScale
import org.cobalt.util.skia.SkiaTransforms

object ModuleManager {

  private val modules = mutableSetOf<Module>()

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

    module.onRegistration()
  }

  fun removeModule(module: Module): Boolean {
    return modules.remove(module)
  }

  fun getModule(moduleName: String): Module? {
    return modules.find { module ->
      module.name.equals(moduleName, true)
    }
  }

  fun getModules(): Set<Module> {
    return modules
  }

  @SubscribeEvent
  fun drawRenderableModules(@Suppress("UnusedParameter") event: SkiaDrawEvent) {
    if (minecraft.level == null || minecraft.player == null) {
      return
    }

    if (minecraft.options.hideGui || minecraft.debugOverlay.showDebugScreen()) {
      return
    }

    modules
      .filter { module -> module.isEnabled() && module is Renderable }
      .forEach { module ->
        val renderable = module as Renderable

        SkiaTransforms.save()

        val originX = renderable.xPos
        val originY = renderable.yPos
        val moduleScale = renderable.scale * windowScale

        SkiaTransforms.translate(Vec2f(originX, originY))
        SkiaTransforms.scale(Vec2f(moduleScale, moduleScale))
        SkiaTransforms.translate(Vec2f(-originX, -originY))

        renderable.renderComponent()

        SkiaTransforms.restore()
      }
  }

}


