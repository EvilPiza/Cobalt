package org.cobalt.module

import org.cobalt.Cobalt.minecraft
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.module.impl.render.PerformanceHUD
import org.cobalt.util.skia.SkiaRenderer

object ModuleManager {

  private val modules = mutableSetOf<Module>()

  init {
    EventBus.register(this)
  }

  fun registerModules() {
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

  fun getModule(moduleName: String): Module? {
    return modules.find { module ->
      module.name.equals(moduleName, true)
    }
  }

  fun getModules(): Set<Module> {
    return modules
  }

  @SubscribeEvent
  fun drawRenderableModules(event: SkiaDrawEvent) {
    if (minecraft.level == null) {
      return
    }

    val windowScale = SkiaRenderer.getWindowScale()

    modules
      .filter { module -> module.enabled && module.isRenderable() }
      .forEach { module ->
        val renderable = module as RenderableModule

        SkiaRenderer.save()

        val originX = renderable.xPos
        val originY = renderable.yPos
        val moduleScale = renderable.scale * windowScale

        SkiaRenderer.translate(originX, originY)
        SkiaRenderer.scale(moduleScale, moduleScale)
        SkiaRenderer.translate(-originX, -originY)

        renderable.renderModule()

        SkiaRenderer.restore()
      }
  }

}
