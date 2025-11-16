package org.cobalt.api.module.setting.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.cobalt.api.module.setting.Setting

class KeyBindSetting(
  name: String,
  description: String,
  subCategory: String,
  defaultValue: Int
) : Setting<Int>(name, description, subCategory, defaultValue) {

  override fun read(element: JsonElement) {
    this.value = element.asInt
  }

  override fun write(): JsonElement {
    return JsonPrimitive(value)
  }

}
