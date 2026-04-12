package org.cobalt.module

/** Base class for all client modules/features. */
abstract class Module(
  /** Human-readable module name. */
  val name: String,
  /** Category used to group modules in UI. */
  val category: ModuleCategory,
) {

  /** Whether this module is currently enabled. */
  var enabled: Boolean = false

  /** Called when the module is registered with the module manager. */
  open fun onRegistration() {}

  /** Return true when this module supports render callbacks. */
  fun isRenderable(): Boolean {
    return this is RenderableModule
  }

}

/** Module variant that exposes screen-space rendering hooks and layout properties. */
abstract class RenderableModule(
  name: String,
  category: ModuleCategory,
  /** UI X position for rendering the module. */
  var xPos: Float,
  /** UI Y position for rendering the module. */
  var yPos: Float,
) : Module(name, category) {

  /** UI scale factor for rendering the module. */
  var scale: Float = 1.0f

  /** Return the current width of the module's rendered area. */
  abstract fun getWidth(): Float

  /** Return the current height of the module's rendered area. */
  abstract fun getHeight(): Float

  /** Render the module's UI onto the current canvas/context. */
  abstract fun renderModule()

}

/** Logical grouping for modules used by UI and registration. */
class ModuleCategory private constructor(
  /** Human-friendly display name for this category. */
  val displayName: String
) {
  companion object {

    private val entries = mutableMapOf<String, ModuleCategory>()

    /** Pre-registered render category used for renderable modules. */
    @JvmField
    val RENDER = register(displayName = "Render")

    /** Register or return an existing ModuleCategory for the given display name. */
    fun register(displayName: String): ModuleCategory {
      return entries.getOrPut(displayName.lowercase()) {
        ModuleCategory(displayName)
      }
    }

    /** Return all registered module categories. */
    fun getCategories(): Collection<ModuleCategory> {
      return entries.values
    }

  }
}
