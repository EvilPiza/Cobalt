package org.cobalt.script

import net.minecraft.ChatFormatting
import org.cobalt.util.ChatUtils

object ScriptManager {

  val scripts = mutableSetOf<Script>()
  var currentScript: Script? = null

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

  fun startScript(script: Script) {
    if (currentScript != null && currentScript != script) {
      stopAllScripts()
      ChatUtils.sendSystemMessage(
        "${ChatFormatting.RED}Cannot start a different script when one is currently active, disabling all scripts..."
      )

      return
    }

    currentScript = script
    script.startScript()
  }

  fun stopScript() {
    if (currentScript == null) {
      ChatUtils.sendSystemMessage("${ChatFormatting.RED}There is no script currently running")
      return
    }

    currentScript?.startScript().also {
      currentScript = null
    }
  }

  fun stopAllScripts() {
    scripts.forEach { script ->
      script.stopScript()
    }

    currentScript = null
  }

  fun getScript(scriptName: String): Script? {
    return scripts.find { script ->
      script.name.equals(scriptName, true)
    }
  }

}


