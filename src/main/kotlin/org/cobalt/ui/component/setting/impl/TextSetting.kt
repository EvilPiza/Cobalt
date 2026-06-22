package org.cobalt.ui.component.setting.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.cobalt.ui.component.setting.Setting

class TextSetting(
  name: String,
  description: String,
  defaultValue: String,
  val placeholder: String = "Enter text..."
) : Setting<String>(name, description, defaultValue) {

  override fun read(element: JsonElement) {
    this.value = element.asString
  }

  override fun write(): JsonElement {
    return JsonPrimitive(value)
  }

  override fun renderSetting() = Unit

}
