package org.cobalt.module

abstract class Module(
  val name: String,
  val category: ModuleCategory,
) {

  var enabled: Boolean = false

  open fun onRegistration() {}

  fun isRenderable(): Boolean {
    return this is RenderableModule
  }

}

abstract class RenderableModule(
  name: String,
  category: ModuleCategory,
  var xPos: Float,
  var yPos: Float,
) : Module(name, category) {

  var scale: Float = 1.0f

  abstract fun getWidth(): Float
  abstract fun getHeight(): Float
  abstract fun renderModule()

}

class ModuleCategory private constructor(val displayName: String) {
  companion object {

    private val entries = mutableMapOf<String, ModuleCategory>()

    @JvmField
    val RENDER = register(displayName = "Render")

    fun register(displayName: String): ModuleCategory {
      return entries.getOrPut(displayName.lowercase()) {
        ModuleCategory(displayName)
      }
    }

    fun getCategories(): Collection<ModuleCategory> {
      return entries.values
    }

  }
}
