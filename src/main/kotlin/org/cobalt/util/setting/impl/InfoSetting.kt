package org.cobalt.util.setting.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.cobalt.util.setting.Setting

class InfoSetting(
  val text: String,
  val type: InfoType = InfoType.INFO,
) : Setting<String>("", "", "") {

  override fun read(element: JsonElement) {}
  override fun write(): JsonElement = JsonPrimitive("")

}

enum class InfoType {
  INFO, WARNING, SUCCESS, ERROR
}
