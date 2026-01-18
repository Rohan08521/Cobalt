package org.cobalt.internal.helper

import com.google.gson.*
import java.io.File
import net.minecraft.client.MinecraftClient
import org.cobalt.internal.loader.AddonLoader

internal object Config {

  private val mc: MinecraftClient = MinecraftClient.getInstance()
  private val gson = GsonBuilder().setPrettyPrinting().create()
  private val modulesFile = File(mc.runDirectory, "config/cobalt/addons.json")

  fun loadModulesConfig() {
    if (!modulesFile.exists()) {
      modulesFile.parentFile?.mkdirs()
      modulesFile.createNewFile()
      return
    }

    val text = modulesFile.bufferedReader().use { it.readText() }
    if (text.isEmpty()) return

    val addonsMap = AddonLoader.getAddons().associate { it.first.id to it.second }

    runCatching {
      JsonParser.parseString(text).asJsonArray
    }.getOrNull()?.forEach { element ->
      val addonObj = element.asJsonObject
      val addonId = addonObj.get("addon").asString
      val addon = addonsMap[addonId] ?: return@forEach

      val modulesMap = addon.getModules().associateBy { it.name }
      val settingsMap = modulesMap.values.flatMap { it.getSettings() }.associateBy { it.name }

      addonObj.getAsJsonArray("modules")?.forEach { moduleElement ->
        val moduleObj = moduleElement.asJsonObject
        val moduleName = moduleObj.get("name").asString
        modulesMap[moduleName] ?: return@forEach

        moduleObj.getAsJsonObject("settings")?.entrySet()?.forEach { (key, value) ->
          settingsMap[key]?.read(value)
        }
      }
    }
  }

  fun saveModulesConfig() {
    val jsonArray = JsonArray().apply {
      AddonLoader.getAddons().forEach { (metadata, addon) ->
        add(JsonObject().apply {
          add("addon", JsonPrimitive(metadata.id))
          add("modules", JsonArray().apply {
            addon.getModules().forEach { module ->
              add(JsonObject().apply {
                add("name", JsonPrimitive(module.name))
                add("settings", JsonObject().apply {
                  module.getSettings().forEach { setting ->
                    add(setting.name, setting.write())
                  }
                })
              })
            }
          })
        })
      }
    }

    modulesFile.bufferedWriter().use { it.write(gson.toJson(jsonArray)) }
  }

}
