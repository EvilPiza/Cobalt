package org.cobalt.module

class ModuleCategory private constructor(
  val displayName: String,
) {

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
