package org.cobalt.module

import org.cobalt.Cobalt.minecraft
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.math.Vec2f
import org.cobalt.module.impl.render.PerformanceHUD
import org.cobalt.util.WindowUtils
import org.cobalt.util.skia.SkiaTransforms

/**
 * Central registry and lifecycle manager for all client modules.
 */
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

  /**
   * Adds a module to the registry.
   *
   * @throws IllegalStateException if a module with the same name is already registered
   */
  fun addModule(module: Module) {
    if (!modules.add(module)) {
      error("'${module.name}' is already registered")
    }

    module.onRegistration()
  }

  /**
   * Removes a module from the registry.
   *
   * @param module the module to remove
   * @return true if the module was removed, false if it was not registered
   */
  fun removeModule(module: Module): Boolean {
    return modules.remove(module)
  }

  /**
   * Returns a module by name (case-insensitive).
   *
   * @param moduleName the name of the module
   * @return the matching module, or null if not found
   */
  fun getModule(moduleName: String): Module? {
    return modules.find { module ->
      module.name.equals(moduleName, true)
    }
  }

  /**
   * Returns all registered modules.
   *
   * @return a set of all modules in the registry
   */
  fun getModules(): Set<Module> {
    return modules
  }

  @Suppress("UndocumentedPublicFunction")
  @SubscribeEvent
  fun drawRenderableModules(@Suppress("UnusedParameter") event: SkiaDrawEvent) {
    if (minecraft.level == null) {
      return
    }

    val windowScale = WindowUtils.getWindowScale()

    modules
      .filter { module -> module.isEnabled() && module.isRenderable() }
      .forEach { module ->
        val renderable = module as RenderableModule

        SkiaTransforms.save()

        val originX = renderable.xPos
        val originY = renderable.yPos
        val moduleScale = renderable.scale * windowScale

        SkiaTransforms.translate(Vec2f(originX, originY))
        SkiaTransforms.scale(Vec2f(moduleScale, moduleScale))
        SkiaTransforms.translate(Vec2f(-originX, -originY))

        renderable.renderModule()

        SkiaTransforms.restore()
      }
  }

}


