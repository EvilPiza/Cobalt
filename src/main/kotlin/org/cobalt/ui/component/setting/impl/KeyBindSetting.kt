package org.cobalt.ui.component.setting.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.mojang.blaze3d.platform.InputConstants
import org.cobalt.ui.component.setting.Setting

class KeyBindSetting(
  name: String,
  description: String,
  defaultValue: InputConstants.Key,
) : Setting<InputConstants.Key>(name, description, defaultValue) {

  val keyName: String
    get() = value.displayName.string

  val keyCode: Int
    get() = value.value

  constructor(
    name: String,
    description: String,
    defaultKeyCode: Int
  ) : this(name, description, InputConstants.Type.KEYSYM.getOrCreate(defaultKeyCode))

  override fun read(element: JsonElement) {
    this.value = InputConstants.getKey(element.asString)
  }

  override fun write(): JsonElement {
    return JsonPrimitive(value.name)
  }

  override fun renderSetting() = Unit

}
