package org.cobalt.script

import org.cobalt.event.EventBus

object ScriptManager {

  val scripts = mutableSetOf<Script>()

  init {
    EventBus.register(this)
  }

  internal fun registerScripts() {
    val builtIn = arrayOf<Script>()

    builtIn.forEach { script ->
      addScript(script)
    }
  }

  fun addScript(script: Script) {
    if (!scripts.add(script)) {
      error("'${script.name}' is already registered")
    }

    script.loadConfig()
  }

  fun removeScript(script: Script): Boolean {
    return scripts.remove(script)
  }

  fun getScript(scriptName: String): Script? {
    return scripts.find { script ->
      script.name.equals(scriptName, true)
    }
  }

}


