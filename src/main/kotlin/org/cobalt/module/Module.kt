package org.cobalt.module

/**
 * Base class for all client modules/features.
 *
 * A module represents a toggleable feature within the client.
 *
 * @property name the module name
 * @property category the category used to group modules in the UI
 *
 * @see RenderableModule for modules with rendering capabilities
 */
abstract class Module(
  val name: String,
  val category: ModuleCategory,
) {

  private var enabled: Boolean = false

  /**
   * Called when the module is registered in the module system.
   */
  open fun onRegistration() {}

  /**
   * Returns whether this module is currently enabled.
   *
   * @return true if the module is enabled, false otherwise
   */
  fun isEnabled(): Boolean = enabled

  /**
   * Sets the enabled state of this module.
   *
   * @param enabled whether the module should be enabled or disabled
   */
  fun setEnabled(enabled: Boolean) {
    this.enabled = enabled
  }

  /**
   * Returns whether this module supports rendering capabilities.
   *
   * @return true if this module is a [RenderableModule], false otherwise
   */
  fun isRenderable(): Boolean {
    return this is RenderableModule
  }

}

/**
 * Module variant that exposes screen-space rendering hooks and layout properties.
 *
 * Extends [Module] with rendering capabilities.
 *
 * @property xPos UI X position for rendering the module.
 * @property yPos UI Y position for rendering the module.
 */
abstract class RenderableModule(
  name: String,
  category: ModuleCategory,
  var xPos: Float,
  var yPos: Float,
) : Module(name, category) {

  /**
   * UI scale factor for rendering the module.
   */
  var scale: Float = 1.0f

  /**
   * Returns the rendered width of this module in screen space.
   *
   * @return the module width in pixels
   */
  abstract fun getWidth(): Float

  /**
   * Returns the rendered height of this module in screen space.
   *
   * @return the module height in pixels
   */
  abstract fun getHeight(): Float

  /**
   * Renders this module to the screen.
   */
  abstract fun renderModule()

}

/**
 * Represents a grouping category for modules used in the UI.
 *
 * @property displayName the name shown in the UI
 */
class ModuleCategory private constructor(
  val displayName: String
) {

  companion object {

    private val entries = mutableMapOf<String, ModuleCategory>()

    /**
     * Predefined category for rendering-related modules.
     */
    @JvmField
    val RENDER = register(displayName = "Render")

    /**
     * Registers a new module category or returns an existing one.
     *
     * Categories are stored case-insensitively.
     *
     * @param displayName the name of the category
     * @return the existing or newly created [ModuleCategory]
     */
    fun register(displayName: String): ModuleCategory {
      return entries.getOrPut(displayName.lowercase()) {
        ModuleCategory(displayName)
      }
    }

    /**
     * Returns all registered module categories.
     *
     * @return a collection of all [ModuleCategory] instances
     */
    fun getCategories(): Collection<ModuleCategory> {
      return entries.values
    }

  }

}
