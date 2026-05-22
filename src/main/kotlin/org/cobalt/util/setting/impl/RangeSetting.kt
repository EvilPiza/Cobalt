package org.cobalt.util.setting.impl

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.cobalt.util.setting.Setting

class RangeSetting(
  name: String,
  description: String,
  default: Pair<Int, Int>,
  val min: Int,
  val max: Int,
) : Setting<Pair<Int, Int>>(name, description, default) {

  override fun read(element: JsonElement) {
    if (element.isJsonObject) {
      val obj = element.asJsonObject
      val start = obj.get("start")?.asInt ?: defaultValue.first
      val end = obj.get("end")?.asInt ?: defaultValue.second
      clamp(Pair(start, end))
    }
  }

  override fun write(): JsonElement {
    return JsonObject().apply {
      add("start", JsonPrimitive(value.first))
      add("end", JsonPrimitive(value.second))
    }
  }

  private fun clamp(newValue: Pair<Int, Int>) {
    val clampedStart = newValue.first.coerceIn(min, max)
    val clampedEnd = newValue.second.coerceIn(min, max)

    this.value = Pair(
      minOf(clampedStart, clampedEnd),
      maxOf(clampedStart, clampedEnd)
    )
  }

}
