package org.cobalt.util.setting.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.cobalt.util.setting.Setting

class SliderSetting(
  name: String,
  description: String,
  defaultValue: Int,
  val min: Int,
  val max: Int,
) : Setting<Int>(name, description, defaultValue) {

  override fun read(element: JsonElement) {
    this.value = element.asInt.coerceIn(min, max)
  }

  override fun write(): JsonElement {
    return JsonPrimitive(value)
  }

}
