package org.cobalt.module

abstract class Module(
  val name: String,
  val icon: String,
  val category: Category,
) {

  var enabled: Boolean = false

  open fun onRegistration() {}

  enum class Category {
    GENERAL,
    COMBAT,
    MINING,
    SKILLS,
    DUNGEONS,
    FARMING,
    MISC
  }

}
