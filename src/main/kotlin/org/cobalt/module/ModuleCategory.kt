package org.cobalt.module

enum class ModuleCategory(val displayName: String) {

  COMBAT("Combat"),
  FARMING("Farming"),
  SKILLS("Skills"),
  FAILSAFE("Failsafe"),
  VISUAL("Visual"),
  MISC("Misc");

  val iconPath = "/assets/cobalt/ui/category/${name.lowercase()}.svg"

}
