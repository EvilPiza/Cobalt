package org.cobalt.script

class ScriptCategory private constructor(
  val displayName: String,
) {

  companion object {

    private val entries = mutableMapOf<String, ScriptCategory>()

    @JvmField
    val SKILLS = register(displayName = "Skills")

    fun register(displayName: String): ScriptCategory {
      return entries.getOrPut(displayName.lowercase()) {
        ScriptCategory(displayName)
      }
    }

    fun getCategories(): Collection<ScriptCategory> {
      return entries.values
    }

  }

}
