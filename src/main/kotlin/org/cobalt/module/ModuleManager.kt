package org.cobalt.module

import org.cobalt.Cobalt.minecraft
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.module.impl.render.PerformanceHUD
import org.cobalt.render.skia.SkiaRenderer

/** Manager responsible for registering, storing and dispatching modules. */
object ModuleManager {

  private val modules = mutableSetOf<Module>()

  init {
    EventBus.register(this)
  }

  /** Register built-in modules and perform their onRegistration lifecycle call. */
  fun registerModules() {
    val builtIn = arrayOf(
      PerformanceHUD
    )

    builtIn.forEach { module ->
      addModule(module)
    }
  }

  /** Add a module to the manager; will throw if a module with the same name is already registered. */
  fun addModule(module: Module) {
    if (!modules.add(module)) {
      error("'${module.name}' is already registered")
    }

    module.onRegistration()
  }

  /** Lookup a module by its name (case-insensitive). */
  fun getModule(moduleName: String): Module? {
    return modules.find { module ->
      module.name.equals(moduleName, true)
    }
  }

  /** Return the set of registered modules. */
  fun getModules(): Set<Module> {
    return modules
  }

  /** Draw all enabled modules that implement renderable behavior during the Skia render pass. */
  @SubscribeEvent
  fun drawRenderableModules(@Suppress("UnusedParameter") event: SkiaDrawEvent) {
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
