package org.cobalt.module

import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet
import kotlin.enums.EnumEntries

object ModuleManager {

  private val modules = ObjectRBTreeSet<Module>()

  fun registerModules() {
//    val builtIn = arrayOf(
//    )
//
//    builtIn.forEach { module ->
//      addModule(module)
//    }
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

  fun getCategories(): EnumEntries<Module.Category> {
    return Module.Category.entries
  }

  fun getModules(): Set<Module> {
    return modules
  }

}
