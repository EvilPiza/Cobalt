package org.cobalt.util.setting.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.cobalt.util.setting.Setting

class ModeSetting(
  name: String,
  description: String,
  defaultValue: Int,
  val options: Array<String>,
) : Setting<Int>(name, description, defaultValue) {

  override fun read(element: JsonElement) {
    this.value = element.asInt
  }

  override fun write(): JsonElement {
    return JsonPrimitive(value)
  }

}
