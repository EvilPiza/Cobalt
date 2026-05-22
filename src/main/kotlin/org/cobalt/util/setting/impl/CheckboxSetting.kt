package org.cobalt.util.setting.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.cobalt.util.setting.Setting

class CheckboxSetting(
  name: String,
  description: String,
  defaultValue: Boolean,
) : Setting<Boolean>(name, description, defaultValue) {

  override fun read(element: JsonElement) {
    this.value = element.asBoolean
  }

  override fun write(): JsonElement {
    return JsonPrimitive(value)
  }

}
