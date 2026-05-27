package org.cobalt.script

object ScriptManager {

  val scripts = mutableSetOf<Script>()

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

  fun getScript(scriptName: String): Script? {
    return scripts.find { script ->
      script.name.equals(scriptName, true)
    }
  }

}


