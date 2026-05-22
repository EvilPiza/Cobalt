package org.cobalt.util.setting.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.cobalt.util.setting.Setting

class TextSetting(
  name: String,
  description: String,
  defaultValue: String,
) : Setting<String>(name, description, defaultValue) {

  override fun read(element: JsonElement) {
    this.value = element.asString
  }

  override fun write(): JsonElement {
    return JsonPrimitive(value)
  }

}
