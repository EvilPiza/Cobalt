package org.cobalt.ui.component.setting.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.cobalt.ui.component.setting.Setting

class ButtonSetting(
  name: String,
  description: String,
  val buttonLabel: String
) : Setting<String>(name, description, "") {

  override fun read(element: JsonElement) = Unit
  override fun write(): JsonElement = JsonPrimitive("")

  override fun renderSetting() = Unit

}
